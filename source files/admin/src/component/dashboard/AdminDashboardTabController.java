package component.dashboard;
import component.dashboard.refresher.AdminPendingMissionsTableViewRefresher;
import component.dashboard.refresher.AvailableGraphTableViewRefresher;
import component.dashboard.refresher.UserTableViewRefresher;
import mission.AdminPendingMissionsTableViewDto;
import mission.AdminPendingMissionsTableViewDtoWithoutRadioButton;
import mission.GraphNameAndNewMissionNameDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import row.AvailableGraphsTableViewRow;
import component.supercontroller.AdminSuperController;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import row.UserTableViewRow;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.*;

public class AdminDashboardTabController {
    @FXML private Label loadStatusLabel;
    @FXML private RadioButton BreezeModeThemeRadioButton;
    @FXML private RadioButton DarkModeThemeRadioButton;
    @FXML private Button runMissionAgainFromScratchButton;
    @FXML private Button runMissionAgainFromIncrementButton;

    @FXML private TableView<UserTableViewRow> userListTableView;
    @FXML private TableColumn<UserTableViewRow,String> userNameColumn;
    @FXML private TableColumn<UserTableViewRow,String> roleColumn;

    @FXML private TableView<AvailableGraphsTableViewRow> availableGraphsTableView;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> graphNameColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> uploadedByColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> leavesColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> independentsColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> middlesColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> rootsColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> totalTargetsColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> graphTasksPriceListColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> simulationColumn;
    @FXML private TableColumn<AvailableGraphsTableViewRow,String> compilationColumn;

    @FXML private TableView<AdminPendingMissionsTableViewDto> adminPendingMissionsTableView;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> taskGraphNameColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> missionNameColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> createdByColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> taskLeavesColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> taskIndependentsColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> taskMiddlesColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> taskRootsColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> totalTaskPriceColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> amountOfWorkersOnTaskColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> selectedColumn;
    @FXML private TableColumn<AdminPendingMissionsTableViewDto,String> missionStatusColumn;

    @FXML private ListView<String> myMissionsListView;

    @FXML private Label userNameLabel;

    private AdminSuperController sController;
    private SimpleBooleanProperty isGraphSelected;
    private String lastSelectedGraph,adminUserName;
    private Timer usersTimer,availableGraphsTimer,adminPendingMissionsTimer;
    private TimerTask userListTableRefresher,availableGraphTableViewRefresher,adminPendingMissionsTableViewRefresher;
    private ToggleGroup missionSelectionToggleGroup;
    private SimpleBooleanProperty canDoMissionAgainIncrementProperty;
    private SimpleBooleanProperty canDoMissionAgainScratchProperty;
    private String selectedMission;

    public AdminDashboardTabController(){
        isGraphSelected= new SimpleBooleanProperty(false);
        missionSelectionToggleGroup=new ToggleGroup();

        canDoMissionAgainIncrementProperty=new SimpleBooleanProperty(false);
        canDoMissionAgainScratchProperty=new SimpleBooleanProperty(false);

        selectedMission=null;
    }
    @FXML private void initialize() {
        runMissionAgainFromIncrementButton.disableProperty().bind(canDoMissionAgainIncrementProperty.not());
        runMissionAgainFromScratchButton.disableProperty().bind(canDoMissionAgainScratchProperty.not());
    }
    public void startUsersTableViewRefresher() {
        userListTableRefresher = new UserTableViewRefresher(
                this::updateUsersTableView);
        usersTimer = new Timer();
        usersTimer.schedule(userListTableRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void updateUsersTableView(Map<String,String> usersNames) {
        UserTableViewRow[] theUsers =new UserTableViewRow[usersNames.size()];
        int i=0;
        for(Map.Entry<String,String> e:usersNames.entrySet()){
            theUsers[i]=new UserTableViewRow(e.getKey(),e.getValue());
            i++;
        }
        Platform.runLater(() -> {
            final ObservableList<UserTableViewRow> items = FXCollections.observableArrayList(theUsers);
            userNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("userName")
            );
            roleColumn.setCellValueFactory(
                    new PropertyValueFactory<>("role")
            );

            userListTableView.setItems(items);
            userListTableView.getColumns().clear();
            userListTableView.getColumns().addAll(userNameColumn,roleColumn);
        });
    }

    public void startAvailableGraphTableViewRefresher() {
        availableGraphTableViewRefresher = new AvailableGraphTableViewRefresher(
                this::updateAvailableGraphTableView);
        availableGraphsTimer = new Timer();
        availableGraphsTimer.schedule(availableGraphTableViewRefresher, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }

    public void startAdminPendingMissionsTableViewRefresher() {
        adminPendingMissionsTableViewRefresher = new AdminPendingMissionsTableViewRefresher(
                this::updateAdminPendingMissionsTableView);
        adminPendingMissionsTimer = new Timer();
        adminPendingMissionsTimer.schedule(adminPendingMissionsTableViewRefresher, Constants.REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }

    @FXML public void onClickedSelectedGraph(){
        if(availableGraphsTableView.getSelectionModel().selectedItemProperty().get()!=null){
           if(!isGraphSelected.getValue() || !(availableGraphsTableView.getSelectionModel().selectedItemProperty().get().
                   getGraphNameCol().equals(lastSelectedGraph))){
                lastSelectedGraph=availableGraphsTableView.getSelectionModel().selectedItemProperty().get().getGraphNameCol();
                String selectedGraphName=availableGraphsTableView.getSelectionModel().selectedItemProperty().get().getGraphNameCol();
                isGraphSelected.setValue(true);
                sController.initAndClearSubTabsOnGraphSelected(selectedGraphName);
           }
           else if(availableGraphsTableView.getSelectionModel().selectedItemProperty().get().
                   getGraphNameCol().equals(lastSelectedGraph)){
               sController.sameGraphSelected();
           }
            sController.handleDisableWhenGraphSelected();
        }
    }

   @FXML private void onActionIncrementalButton(){
       sentToServerRunTaskAgainRequest("RunTaskAgainWithIncrement");
   }
   @FXML private void onActionFromScratchButton(){
        sentToServerRunTaskAgainRequest("RunTaskAgainWithScratch");
    }

    private void sentToServerRunTaskAgainRequest(String runMissionAgainType){
        String finalUrl = HttpUrl
                .parse(Constants.RUN_MISSION_AGAIN)
                .newBuilder()
                .addQueryParameter("missionName",selectedMission)
                .addQueryParameter("runMissionAgainType",runMissionAgainType)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl,new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200) {
                    String jsonGraphNameAndNewMissionNameDto= response.body().string();
                    GraphNameAndNewMissionNameDto graphNameAndNewMissionNameDto = Constants.GSON_INSTANCE.fromJson(jsonGraphNameAndNewMissionNameDto, GraphNameAndNewMissionNameDto.class);
                    synchronized (this){
                    sController.addNewAdminMission(graphNameAndNewMissionNameDto.getNewMissionName(),graphNameAndNewMissionNameDto.getGraphName());
                    }
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
    @FXML public void onActionSelectedTask() {
        String selectedMission=null;
        if(!myMissionsListView.getSelectionModel().getSelectedItems().isEmpty()){
             selectedMission= myMissionsListView.getSelectionModel().getSelectedItem();
            myMissionsListView.getSelectionModel().clearSelection();
            sController.initAndClearSubTabsOnTaskSelected(selectedMission);
       }
        //todo i moved it inside, I think it does not make any scene here , if it's null?
      //  sController.initAndClearSubTabsOnTaskSelected(selectedMission);
    }
    private void updateAvailableGraphTableView(AvailableGraphsTableViewRow[] availableGraphs) {
        Platform.runLater(() -> {
            final ObservableList<AvailableGraphsTableViewRow> items = FXCollections.observableArrayList(availableGraphs);
            graphNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("graphNameCol")
            );
            uploadedByColumn.setCellValueFactory(
                    new PropertyValueFactory<>("uploadedByCol")
            );
            leavesColumn.setCellValueFactory(
                    new PropertyValueFactory<>("leavesCol")
            );
            independentsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("independentsCol")
            );
            middlesColumn.setCellValueFactory(
                    new PropertyValueFactory<>("middlesCol")
            );
            rootsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("rootsCol")
            );
            totalTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("totalTargetsCol")
            );
            simulationColumn.setCellValueFactory(
                    new PropertyValueFactory<>("simulationCol")
            );
            compilationColumn.setCellValueFactory(
                    new PropertyValueFactory<>("compilationCol")
            );
            availableGraphsTableView.setItems(items);
            availableGraphsTableView.getColumns().clear();
            availableGraphsTableView.getColumns().addAll(graphNameColumn,uploadedByColumn,leavesColumn,
               independentsColumn, middlesColumn,rootsColumn,totalTargetsColumn,simulationColumn,compilationColumn);
        });
    }
    public void startRefreshers() {
      startUsersTableViewRefresher();
      startAvailableGraphTableViewRefresher();
     startAdminPendingMissionsTableViewRefresher();
    }

    private void updateAdminPendingMissionsTableView(AdminPendingMissionsTableViewDtoWithoutRadioButton[] availableTasks) {
        Platform.runLater(() -> {
            AdminPendingMissionsTableViewDto[] arr=new AdminPendingMissionsTableViewDto[availableTasks.length];
            for(int i=0;i<availableTasks.length;i++){
                arr[i]=new AdminPendingMissionsTableViewDto(availableTasks[i],missionSelectionToggleGroup);
            }
            selectedMission= getSelectedMission();

            final ObservableList<AdminPendingMissionsTableViewDto> items = FXCollections.observableArrayList(arr);
            selectedColumn.setCellValueFactory(
                    new PropertyValueFactory<>("selectedRadioButton")
            );
            missionNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("missionName")
            );
            taskGraphNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("graphName")
            );
            createdByColumn.setCellValueFactory(
                    new PropertyValueFactory<>("createdBy")
            );
            taskLeavesColumn.setCellValueFactory(
                    new PropertyValueFactory<>("leavesAmount")
            );
            taskIndependentsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("independentsAmount")
            );
            taskMiddlesColumn.setCellValueFactory(
                    new PropertyValueFactory<>("middlesAmount")
            );
            taskRootsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("rootsAmount")
            );
            totalTaskPriceColumn.setCellValueFactory(
                    new PropertyValueFactory<>("totalTaskPrice")
            );
            amountOfWorkersOnTaskColumn.setCellValueFactory(
                    new PropertyValueFactory<>("amountOfWorkersOnTask")
            );
            missionStatusColumn.setCellValueFactory(
                    new PropertyValueFactory<>("missionStatus")
            );
            adminPendingMissionsTableView.setItems(items);
            adminPendingMissionsTableView.getColumns().clear();
            adminPendingMissionsTableView.getColumns().addAll(selectedColumn,missionNameColumn,taskGraphNameColumn,createdByColumn,
                    taskLeavesColumn,taskIndependentsColumn,taskMiddlesColumn,taskRootsColumn,
                    totalTaskPriceColumn,amountOfWorkersOnTaskColumn,missionStatusColumn);
            String adminUserName=sController.getCreatedByUserName();

            SelectMissionThatWasSelected(selectedMission);
            AdminPendingMissionsTableViewDto selectedMissionRow=null;
            for(AdminPendingMissionsTableViewDto d:arr){
                String currentMissionName=d.getMissionName();
              boolean isMissionAlreadyInListView= myMissionsListView.getItems().contains(currentMissionName);
                if(d.getCreatedBy().equals(adminUserName)&&!isMissionAlreadyInListView){//todo- we assume that every task name is unique!
                    myMissionsListView.getItems().add(currentMissionName);
                }
                if(d.getMissionName().equals(selectedMission)){
                    selectedMissionRow=d;
                }
            }
            canDoMissionAgainScratchProperty.setValue(selectedMission != null && (selectedMissionRow.getMissionStatus().equals("STOPPED")
                                    || selectedMissionRow.getMissionStatus().equals("FINISHED")));
            canDoMissionAgainIncrementProperty.setValue(selectedMission != null && !sController.getInfo().getMyMissionInfoMap().get(selectedMission).getAllTargetsSucceed()
                    &&(selectedMissionRow.getMissionStatus().equals("STOPPED") || selectedMissionRow.getMissionStatus().equals("FINISHED")));
            sController.updateMissionsStatus(arr);
        });
    }

    private void SelectMissionThatWasSelected(String selectedMission){
        if(selectedMission!=null) {
            final ObservableList<AdminPendingMissionsTableViewDto> rows = FXCollections.observableArrayList(adminPendingMissionsTableView.getItems());
            for (AdminPendingMissionsTableViewDto r : rows) {
                if (selectedMission.equals(r.getMissionName())) {
                    r.getSelectedRadioButton().setSelected(true);
                    return;
                }
            }
        }
    }

    private String getSelectedMission(){
        final ObservableList<AdminPendingMissionsTableViewDto> rows = FXCollections.observableArrayList(adminPendingMissionsTableView.getItems());
        for(AdminPendingMissionsTableViewDto r:rows){
            if(r.getSelectedRadioButton().isSelected()){
                return r.getMissionName();
            }
        }
        return null;
    }

    public void updateAdminUserNameLabel(String adminUserNameIn){
        adminUserName=adminUserNameIn;
        userNameLabel.setText(adminUserName);
    }

    public void setAdminSuperController(AdminSuperController superCont,SimpleStringProperty loadStatusLabelAdminSuperControllerPropertyIn){
        sController=superCont;
        loadStatusLabel.textProperty().bind(loadStatusLabelAdminSuperControllerPropertyIn);
    }

    public SimpleBooleanProperty isGraphSelectedProperty(){return isGraphSelected;}

    @FXML private void loadXmlAction(){
        sController.openFileButtonAction();
    }

    @FXML private void setTheme(){
        Scene scene=loadStatusLabel.getScene();
        Scene chatScene= sController.getChatScene();
        if(BreezeModeThemeRadioButton.isSelected()){
            scene.getStylesheets().clear();
            chatScene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("breezeMode1.css").toExternalForm());
           chatScene.getStylesheets().add(getClass().getResource("breezeMode1.css").toExternalForm());
        }
        else if(DarkModeThemeRadioButton.isSelected()){
            scene.getStylesheets().clear();
            chatScene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("darkMode1.css").toExternalForm());
            chatScene.getStylesheets().add(getClass().getResource("darkMode1.css").toExternalForm());
        }
        else{
            scene.getStylesheets().clear();
            chatScene.getStylesheets().clear();
        }
    }

    @FXML private void onActionChatButton(){
        sController.openChat();
    }
}
