package component.supercontroller;

import com.google.gson.reflect.TypeToken;
import component.actions.on.the.graph.ActionsOnTheGraphController;
import chat.ChatRoomMainController;
import component.dashboard.AdminDashboardTabController;
import component.graph.Info.GraphInfoTabController;
import component.run.task.RunTaskTabController;
import inner.InnerInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mission.AdminPendingMissionsTableViewDto;
import okhttp3.*;
import output.*;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class AdminSuperController {
    private Stage primaryStage;
    private String adminUserName;
    private SimpleStringProperty loadStatusLabelAdminSuperControllerProperty;
    private InnerInfo info;
    private Scene chatScene;
    private ChatRoomMainController chatRoomMainController;

    @FXML private TabPane tabsMaster;
    @FXML private Tab dashboardTab;
    @FXML private Tab graphInfoTab;
    @FXML private Tab TaskTab;
    @FXML private Tab ActionsOnTheGraphTab;

    @FXML private ScrollPane DashboardTabContent;
    @FXML private AdminDashboardTabController DashboardTabContentController;

    @FXML private ScrollPane GraphInfoTabContent;
    @FXML  private GraphInfoTabController GraphInfoTabContentController;

    @FXML  private ScrollPane RunTaskTabContent;
    @FXML  private RunTaskTabController RunTaskTabContentController;

    @FXML private ScrollPane ActionsOnTheGraphTabContent;
    @FXML private ActionsOnTheGraphController ActionsOnTheGraphTabContentController;

     public AdminSuperController(){
       loadStatusLabelAdminSuperControllerProperty= new SimpleStringProperty("Load status: ");
       info=new InnerInfo();
   }
    @FXML public void initialize() {
        if (DashboardTabContentController != null && GraphInfoTabContentController != null && RunTaskTabContentController != null
                && ActionsOnTheGraphTabContentController != null) {
            DashboardTabContentController.setAdminSuperController(this,loadStatusLabelAdminSuperControllerProperty);
            GraphInfoTabContentController.setAdminSuperController(this);
            RunTaskTabContentController.setAdminSuperController(this);
            ActionsOnTheGraphTabContentController.setAdminSuperController(this);
            graphInfoTab.setDisable(true);
            TaskTab.setDisable(true);
            ActionsOnTheGraphTab.setDisable(true);
        }
        initChat();
    }
    public void updateMissionsStatus(AdminPendingMissionsTableViewDto[] adminPendingMissionsTableViewDtos){
         info.updateMissionsStatus(adminPendingMissionsTableViewDtos);
    }
    public void freezeTaskTab(){TaskTab.setDisable(true);}

    public void handleDisableWhenGraphSelected(){
        graphInfoTab.setDisable(false);
        TaskTab.setDisable(false);
        ActionsOnTheGraphTab.setDisable(false);
    }

    public void handleTabsWhenTaskCreated(){
        tabsMaster.getSelectionModel().select(dashboardTab);
    }

    public void initAndClearSubTabsOnTaskSelected(String missionName){
        tabsMaster.getSelectionModel().select(TaskTab);
        RunTaskTabContentController.initAndClearSubTabsOnTaskSelected(missionName);
    }

    public void addNewAdminMission(String missionName,String graphName){
        GraphInfoDto dto=null;
       try {
            dto= getGraphInfoDtoFromServer(graphName);
        }catch (Exception ignore){}

        Consumer<List<String>>updateTaskReportConsumer= strings -> RunTaskTabContentController.updateTaskReportInTaskController(strings);
        info.addAdminMission(missionName,graphName,dto,updateTaskReportConsumer,"NEW_MISSION");
    }
    public InnerInfo getInfo() {
        return info;
    }

    public void updateDashBoard(String userName) {
      adminUserName = userName;
      DashboardTabContentController.updateAdminUserNameLabel(adminUserName);
      DashboardTabContentController.startRefreshers();
      chatRoomMainController.setUserName(adminUserName);
    }

    public String getCreatedByUserName(){return adminUserName;}

    private void initChat() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("chat.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent root = fxmlLoader.load(url.openStream());
            chatRoomMainController = fxmlLoader.getController();
            chatScene = new Scene(root, 700, 600);
            chatRoomMainController.setChatSceneAndUserName(chatScene);
        }
         catch (Exception exception){}
    }

    public Scene getChatScene() {return chatScene;}

    public void openChat(){
         chatRoomMainController.showChat();
    }

    public void openFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }
        try {
            loadXmlFileAndSendFileToServer(selectedFile);
        } catch (IOException ignore) {}
    }

    public void sameGraphSelected(){
        RunTaskTabContentController.sameGraphSelected();
    }

    public void initAndClearSubTabsOnGraphSelected(String selectedGraphNameIn){
       graphInfoTab.setDisable(false);
       TaskTab.setDisable(false);
       ActionsOnTheGraphTab.setDisable(false);
       Map<String,String>GraphPricePerTargetAccordingToTaskType=null;
       GraphInfoTabContentController.initSubTabs(selectedGraphNameIn);
       try {
         GraphPricePerTargetAccordingToTaskType= getGraphPricePerTargetAccordingToTaskTypeFromServer(selectedGraphNameIn);
       }
       catch (IOException ignore){}
       RunTaskTabContentController.initSubTabsOnGraphSelected(selectedGraphNameIn,GraphPricePerTargetAccordingToTaskType);
       try {
           List<String> names = getAllTargetsNames(selectedGraphNameIn);
           names.sort(Comparator.comparing(String::toString));
           ActionsOnTheGraphTabContentController.initActionsOnGraphWhenGraphSelected(names, selectedGraphNameIn);
           ActionsOnTheGraphTabContentController.clearTextArea();
       }
       catch(IOException ignore){}
    }

    private Map<String,String> getGraphPricePerTargetAccordingToTaskTypeFromServer(String selectedGraphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.GET_GRAPH_PRICE_PER_TARGET_ACCORDING_TO_TASK_TYPE)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        Type type = new TypeToken<Map<String,String>>() { }.getType();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),type);
    }

    public Boolean loadXmlFileAndSendFileToServer(File selectedFile) throws IOException {
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart(adminUserName,selectedFile.getName(), RequestBody.create(selectedFile,
                                MediaType.parse("text/plain"))).build();

        Request request = new Request.Builder()
                .url(Constants.UPLOAD_XML_FILE)
                .post(body)
                .build();

        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        Response response=call.execute();
        if(response.code()==200){
            Platform.runLater(() -> {
              loadStatusLabelAdminSuperControllerProperty.set("Load status: Successfully");
        });}
            else{
                Platform.runLater(() -> {
                    try {
                        loadStatusLabelAdminSuperControllerProperty.set("Load status: error loading " +response.body().string());
                    } catch (IOException e) {}
                });}
       return (response.code()==200);
    }

    public void setPrimaryStage(Stage primaryStageIn) {
        primaryStage = primaryStageIn;
    }

    public AllDependenciesOfTargetDto getAllDependenciesOfTargetDtoFromServer(String targetName,String selectedGraphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.ALL_DEPENDENCIES_OF_TARGET_DTO)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .addQueryParameter("targetName", targetName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),AllDependenciesOfTargetDto.class);
    }

    public List<String> getAllTargetsNames(String graphName) throws IOException {
        List<String> res=new ArrayList<>();
       TargetInfoForTableViewDto[] dto=getAllInformationForTableViewFromMed(graphName);
        for(TargetInfoForTableViewDto t:dto){
            res.add(t.getName());
        }
        return res;
    }

    private TargetInfoForTableViewDto[] getAllInformationForTableViewFromMed(String graphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.TARGET_INFO_FOR_TABLEVIEW_DTO_ARRAY_SERVLET)
                .newBuilder()
                .addQueryParameter("graphName", graphName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),TargetInfoForTableViewDto[].class);
    }

    public String[] getTargetsNamesInGraphTaskOnly(String selectedGraphName,String lastTaskName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.GET_TARGETS_NAMES_IN_GRAPH_TASK_ONLY)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .addQueryParameter("lastTaskName", lastTaskName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),String[].class);
   }

    final public CirclePathForTargetDto checkIfTargetIsInCircle(String targetName,String selectedGraphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.CIRCLE_PATH_FOR_TARGET_DTO)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .addQueryParameter("targetName", targetName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),CirclePathForTargetDto.class);
    }

    public TargetInfoForTableViewDto[] getAllInformationForTableViewFromServer(String selectedGraphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.TARGET_INFO_FOR_TABLEVIEW_DTO)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),TargetInfoForTableViewDto[].class);
    }

    public TargetInfoDto getTargetInfoDtoFromServer(String targetName,String graphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.TARGET_INFO_DTO)
                .newBuilder()
                .addQueryParameter("graphName", graphName)
                .addQueryParameter("targetName", targetName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),TargetInfoDto.class);
    }

    public String[] getAllTargetsNamesInSetFromServer(String selectedGraphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.GET_ALL_TARGETS_NAMES_IN_SET_FROM_SERVER)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),String[].class);
    }

    public AllPathsOfTwoTargetsDto findAllPathsOfTwoTargetsFromEngine(String name1,String name2,Boolean isDepend,String graphName) throws IOException {
        if(isDepend){
            return findBindBetweenTwoTargetsInServer(name1,name2,graphName,isDepend);
        }
        else{
            return findBindBetweenTwoTargetsInServer(name2,name1,graphName,isDepend);
        }
    }

    AllPathsOfTwoTargetsDto findBindBetweenTwoTargetsInServer(String name1, String name2, String selectedGraphName, Boolean isDepend) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.ALL_PATHS_OF_TWO_TARGETS_DTO)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .addQueryParameter("target1", name1)
                .addQueryParameter("target2", name2)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
        return Constants.GSON_INSTANCE.fromJson(response.body().string(),AllPathsOfTwoTargetsDto.class);
    }

    public GraphInfoDto getGraphInfoDtoFromServer (String selectedGraphName) throws IOException {
        String finalUrl = HttpUrl
                .parse(Constants.GRAPH_INFO_DTO)
                .newBuilder()
                .addQueryParameter("graphName", selectedGraphName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        final Response response = call.execute();
      return Constants.GSON_INSTANCE.fromJson(response.body().string(),GraphInfoDto.class);
   }

    public Stage getPrimaryStage() {return primaryStage;}

}
