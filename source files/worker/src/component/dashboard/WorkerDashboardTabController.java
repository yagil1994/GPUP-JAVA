package component.dashboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import component.dashboard.refresher.CheckIfThereIsWorkRefresher;
import component.dashboard.refresher.UserTableViewRefresher;
import component.dashboard.refresher.WorkerPendingMissionsTableViewRefresher;
import component.supercontroller.WorkerSuperController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mini.engine.*;
import misc.MissionNamesContainer;
import mission.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import row.UserTableViewRow;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class WorkerDashboardTabController {

    private WorkerSuperController sController;
    private Boolean isRegisteredToOneTaskAtLeast;
    private Timer usersTimer,workerPendingMissionsTimer,checkIfThereIsWorkTimer;
    private TimerTask userListTableRefresher,workerPendingMissionsTableViewRefresher,checkIfThereIsWorkRefresher;
    private String workerUserName;

    @FXML private TableView<WorkerPendingMissionsTableViewDto> workerPendingMissionsTableView;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto, String> selectedColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto, String> missionNameColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> createdByColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> taskTypeColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> taskLeavesColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> taskIndependentsColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> taskMiddlesColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> taskRootsColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> totalTargetsColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> priceForTargetColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> missionStatusColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> amountOfWorkersOnTaskColumn;
    @FXML private TableColumn<WorkerPendingMissionsTableViewDto,String> registeredColumn;
    @FXML private TableView<UserTableViewRow> userListTableView;
    @FXML private TableColumn<UserTableViewRow, String> userNameColumn;
    @FXML private TableColumn<UserTableViewRow,String> roleColumn;

    @FXML private RadioButton DefaultThemeRadioButton;
    @FXML private ToggleGroup theme;
    @FXML private RadioButton DarkModeThemeRadioButton;
    @FXML private RadioButton BreezeModeThemeRadioButton;

    @FXML private Label userNameLabel;

    public WorkerDashboardTabController(){
        isRegisteredToOneTaskAtLeast=false;
    }

    public void setWorkerSuperController(WorkerSuperController superCont){
        sController=superCont;
    }

    public void updateWorkerUserNameLabel(String workerUserNameIn){
        workerUserName=workerUserNameIn;
        userNameLabel.setText(workerUserName);
    }

    public void startUsersTableViewRefresher() {
        userListTableRefresher = new UserTableViewRefresher(
                this::updateUsersTableView);
        usersTimer = new Timer();
        usersTimer.schedule(userListTableRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void startCheckIfThereIsWorkRefresher() {
        checkIfThereIsWorkRefresher = new CheckIfThereIsWorkRefresher(
                this::sendToWorkAndUpdateServer,sController.getWorkerUserName(),sController.getInner());
        checkIfThereIsWorkTimer = new Timer();
        checkIfThereIsWorkTimer.schedule(checkIfThereIsWorkRefresher, Constants.REFRESH_RATE,Constants.REFRESH_RATE);
        sController.startUpdatingTablesAtTask();
    }

    private Map<String,List<TargetNameAndExtraInfoDto>>  initAllTheTargetsThatDidNotRunMap(Map<String,List<TargetNameAndExtraInfoDto>> originalMap){
       Map<String,List<TargetNameAndExtraInfoDto>>  allTheTargetsThatDidNotRunMap=new HashMap<>();
       for(Map.Entry<String,List<TargetNameAndExtraInfoDto>> x:originalMap.entrySet()){
           List<TargetNameAndExtraInfoDto> tmp=new ArrayList<>();
           for(TargetNameAndExtraInfoDto s:x.getValue()){
               TargetNameAndExtraInfoDto tmpDto=new TargetNameAndExtraInfoDto(s.getTargetName(),s.getExtraInfo());
               tmp.add(tmpDto);
           }
           allTheTargetsThatDidNotRunMap.put(x.getKey(),tmp);
       }
       return allTheTargetsThatDidNotRunMap;
   }

    private void removeExecutedTargetFromMap(Map<String,List<TargetNameAndExtraInfoDto>> copyMap,String missionName,String targetName){
        List<TargetNameAndExtraInfoDto> targetsNamesInTheSameMission=copyMap.get(missionName);
       TargetNameAndExtraInfoDto removeIT=null;
        for(TargetNameAndExtraInfoDto dto:targetsNamesInTheSameMission){
            if(dto.getTargetName().equals(targetName)){
                removeIT=dto;
                break;
            }
        }
       targetsNamesInTheSameMission.remove(removeIT);
   }

    private void sendToWorkAndUpdateServer(Map<String,List<TargetNameAndExtraInfoDto>> mapMissionsNamesToStrListOfAvailableTargetsNames) {
        Map<String,List<TargetNameAndExtraInfoDto>> allTheTargetsThatDidNotRunMap=initAllTheTargetsThatDidNotRunMap(mapMissionsNamesToStrListOfAvailableTargetsNames);
        for (Map.Entry<String, List<TargetNameAndExtraInfoDto>> m : mapMissionsNamesToStrListOfAvailableTargetsNames.entrySet()) {
            String missionName = m.getKey();
            List<TargetNameAndExtraInfoDto> targetsToRunOnTheMission = m.getValue();
            for (TargetNameAndExtraInfoDto availableTargetsFromSpecificMission : targetsToRunOnTheMission) {//all of these targets are in the same mission we try to send them to execution
                if (sController.getInner().getWorkerThreadPoolManager().getAmountOfAvailableThreads() == 0) {//we still have enough threads
                    updateServerInTargetsThatDidNotDidTask(allTheTargetsThatDidNotRunMap);
                    return;
                }
                else {

                    String taskTypeToRun = sController.getInner().getRegisteredMissionsMap().get(missionName).getTaskType();
                    sController.getInner().addProcessedTarget(availableTargetsFromSpecificMission.getTargetName(),missionName,new WorkerTarget(m.getKey(),taskTypeToRun,availableTargetsFromSpecificMission.getTargetName()));
                    if (taskTypeToRun.equals("Simulation")) {
                        SimulationForWorkerDto simulationForWorkerDto = sController.getInner().getRegisteredMissionsMap().get(missionName).getMissionDto().getSimulationForWorkerDto();
                        removeExecutedTargetFromMap(allTheTargetsThatDidNotRunMap,missionName,availableTargetsFromSpecificMission.getTargetName());
                        sController.getInner().getWorkerThreadPoolManager().getWorkerThreadPool().execute(() -> {
                            String runningRes=Work.doSimulation(availableTargetsFromSpecificMission.getExtraInfo(), availableTargetsFromSpecificMission.getTargetName(),missionName, simulationForWorkerDto,
                                    sController.getInner().getProcessedTargets().get(new TargetMission(availableTargetsFromSpecificMission.getTargetName(),missionName)).getWriterToLogs());
                            sController.getInner().SetTargetStatus(availableTargetsFromSpecificMission.getTargetName(),missionName,runningRes);
                        });
                    }
                    else {

                        CompilationForWorkerDto compilationForWorkerDto = sController.getInner().getRegisteredMissionsMap().get(missionName).getMissionDto().getCompilationTaskInputDto();
                        removeExecutedTargetFromMap(allTheTargetsThatDidNotRunMap,missionName,availableTargetsFromSpecificMission.getTargetName());
                        String runningRes=Work.doCompilation(availableTargetsFromSpecificMission.getExtraInfo(),availableTargetsFromSpecificMission.getTargetName(),missionName,
                                compilationForWorkerDto, sController.getInner().getProcessedTargets().get(new TargetMission(availableTargetsFromSpecificMission.getTargetName(),missionName)).getWriterToLogs());
                        sController.getInner().SetTargetStatus(availableTargetsFromSpecificMission.getTargetName(),missionName,runningRes);
                    }
                }
            }
        }
        updateServerInTargetsThatDidNotDidTask(allTheTargetsThatDidNotRunMap);
    }

    private Map<String,TargetNameAndExtraInfoDto[]> convertTheMap(Map<String,List<TargetNameAndExtraInfoDto>> convertIt){
        Map<String,TargetNameAndExtraInfoDto[]> res=new HashMap<>();
        for(Map.Entry<String,List<TargetNameAndExtraInfoDto>> e:convertIt.entrySet()){
            List<TargetNameAndExtraInfoDto> tmpList=e.getValue();
            TargetNameAndExtraInfoDto[] tmp=new TargetNameAndExtraInfoDto[tmpList.size()];
            int i=0;
            for(TargetNameAndExtraInfoDto t:tmpList){
                tmp[i]=t;
                i++;
            }
            res.put(e.getKey(),tmp);
        }
        return res;
    }

    private void updateServerInTargetsThatDidNotDidTask(Map<String,List<TargetNameAndExtraInfoDto>> allTheTargetsThatDidNotRunMap){
        Gson gson = new Gson();
        Map<String,TargetNameAndExtraInfoDto[]> allTheTargetsThatDidNotRun=convertTheMap(allTheTargetsThatDidNotRunMap);
        String jsonAllTheTargetsThatDidNotRun=gson.toJson(allTheTargetsThatDidNotRun);

        String finalUrl=Constants.UPDATE_SERVER_IN_TARGETS_THAT_DID_NOT_DID_TASK;
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("jsonAllTheTargetsThatDidNotRun",jsonAllTheTargetsThatDidNotRun)
                        .build();

        HttpClientUtil.runAsyncPost(finalUrl,body, new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

    private void updateUsersTableView(Map<String, String> usersNames) {
        UserTableViewRow[] theUsers=new UserTableViewRow[usersNames.size()];
        int i=0;
        for(Map.Entry<String, String> e:usersNames.entrySet()){
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

    public void startWorkerPendingMissionsTableViewRefresher(String workerName) {
        workerPendingMissionsTableViewRefresher = new WorkerPendingMissionsTableViewRefresher(
                this::updateWorkerPendingMissionsTableView,workerName);
        workerPendingMissionsTimer = new Timer();
        workerPendingMissionsTimer.schedule(workerPendingMissionsTableViewRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }


    @FXML private void onActionRegisterButton(){
        Set<String> allSelectedTasksNames= getSelectedMissions();
        Set<WorkerPendingMissionsTableViewDto> allSelectedRows=getSelectedWorkerPendingMissionsTableViewDtos();
        if(allSelectedTasksNames.isEmpty()){
            return;
        }
        sController.getInner().addMissions(allSelectedRows);
        Gson gson = new Gson();
        String[] allSelectedTasksNamesInArr=new String[allSelectedTasksNames.size()];
        int i=0;
        for(String s:allSelectedTasksNames){
           allSelectedTasksNamesInArr[i]=s;
           i++;
        }
        String jsonAllSelectedTasksNamesInArr=gson.toJson(allSelectedTasksNamesInArr);
        String finalUrl=Constants.REGISTER_WORKER_TO_TASKS;
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("jsonAllSelectedTasksNamesInArr",jsonAllSelectedTasksNamesInArr)
                        .addFormDataPart("username",sController.getWorkerUserName())
                        .build();

          HttpClientUtil.runAsyncPost(finalUrl,body, new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @Override public void onResponse(@NotNull Call call, @NotNull Response response){
                if (response.code() == 200) {
                    String jsonListOfMediators=null;
                    try {
                         jsonListOfMediators = response.body().string();
                    }
                    catch (Exception ignore){}
                    Type type = new TypeToken<MissionNameAndDto[]>() { }.getType();
                    MissionNameAndDto[] missionNameAndDto = Constants.GSON_INSTANCE.fromJson(jsonListOfMediators,type);
                    addNewDtoInfoToAllMissionsParametersMap(missionNameAndDto);

                    boolean changed=false;
                    synchronized (this) {
                        if (!isRegisteredToOneTaskAtLeast) {
                            isRegisteredToOneTaskAtLeast = true;
                            changed = true;
                        }
                    }
                    if(changed){
                        startCheckIfThereIsWorkRefresher();
                    }
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
        SelectMissionsThatWereSelected(allSelectedTasksNames,false);
    }

    private void addNewDtoInfoToAllMissionsParametersMap(MissionNameAndDto[] missionNameAndDto){
         for(MissionNameAndDto d:missionNameAndDto){
             sController.getInner().getRegisteredMissionsMap().get(d.getMissionName()).updateDto(d);
         }
    }

    private void SelectMissionsThatWereSelected(Set<String> allSelectedMissions, Boolean setTo){
        final ObservableList<WorkerPendingMissionsTableViewDto> rows = FXCollections.observableArrayList(workerPendingMissionsTableView.getItems());
        for(WorkerPendingMissionsTableViewDto r:rows){
            if(allSelectedMissions.contains(r.getMissionName())){
                r.getSelectedMissionCheckBox().setSelected(setTo);
            }
        }
    }

    private Set<String> getSelectedMissions(){
        Set<String> allSelectedMissions=new HashSet<>();
        final ObservableList<WorkerPendingMissionsTableViewDto> rows = FXCollections.observableArrayList(workerPendingMissionsTableView.getItems());
        for(WorkerPendingMissionsTableViewDto r:rows){
            if(r.getSelectedMissionCheckBox().isSelected()){
                allSelectedMissions.add(r.getMissionName());
            }
        }
        return allSelectedMissions;
    }

    private Set<WorkerPendingMissionsTableViewDto> getSelectedWorkerPendingMissionsTableViewDtos(){
        Set<WorkerPendingMissionsTableViewDto> allSelectedMissions=new HashSet<>();
        final ObservableList<WorkerPendingMissionsTableViewDto> rows = FXCollections.observableArrayList(workerPendingMissionsTableView.getItems());
        for(WorkerPendingMissionsTableViewDto r:rows){
            if(r.getSelectedMissionCheckBox().isSelected()){
                allSelectedMissions.add(r);
            }
        }
        return allSelectedMissions;
    }

    private void updateWorkerPendingMissionsTableView(WorkerPendingMissionsTableViewDto[] availableTasks) {
        Platform.runLater(() -> {
            sController.getInner().updateMissions(availableTasks);
            Set<String> selectedMissions=getSelectedMissions();
            final ObservableList<WorkerPendingMissionsTableViewDto> items = FXCollections.observableArrayList(availableTasks);
            selectedColumn.setCellValueFactory(
                    new PropertyValueFactory<>("selectedMissionCheckBox")
            );
            missionNameColumn.setCellValueFactory(
                    new PropertyValueFactory<>("missionName")
            );
            createdByColumn.setCellValueFactory(
                    new PropertyValueFactory<>("createdBy")
            );
            taskTypeColumn.setCellValueFactory(
                    new PropertyValueFactory<>("taskType")
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
            totalTargetsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("totalTargetsAmount")
            );
            priceForTargetColumn.setCellValueFactory(
                    new PropertyValueFactory<>("priceForTarget")
            );
            missionStatusColumn.setCellValueFactory(
                    new PropertyValueFactory<>("missionStatus")
            );
            amountOfWorkersOnTaskColumn.setCellValueFactory(
                    new PropertyValueFactory<>("amountOfWorkersOnTask")
            );
            registeredColumn.setCellValueFactory(
                    new PropertyValueFactory<>("isRegistered")
            );

            workerPendingMissionsTableView.setItems(items);
            workerPendingMissionsTableView.getColumns().clear();
            workerPendingMissionsTableView.getColumns().addAll(selectedColumn,missionNameColumn,createdByColumn,taskTypeColumn,
                    taskLeavesColumn,taskIndependentsColumn,taskMiddlesColumn,taskRootsColumn,totalTargetsColumn,
                    priceForTargetColumn,missionStatusColumn,amountOfWorkersOnTaskColumn,registeredColumn);
            SelectMissionsThatWereSelected(selectedMissions,true);
        });
    }

    @FXML private void onActionChatButton(){
        sController.openChat();
    }

    @FXML private void setTheme(){
        Scene scene=userNameLabel.getScene();
        Scene chatScene= sController.getChatScene();
        if(BreezeModeThemeRadioButton.isSelected()){
            scene.getStylesheets().clear();
            chatScene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("breezeMode2.css").toExternalForm());
            chatScene.getStylesheets().add(getClass().getResource("breezeMode2.css").toExternalForm());
        }
        else if(DarkModeThemeRadioButton.isSelected()){
            scene.getStylesheets().clear();
            chatScene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("darkMode2.css").toExternalForm());
            chatScene.getStylesheets().add(getClass().getResource("darkMode2.css").toExternalForm());
        }
        else{
            scene.getStylesheets().clear();
            chatScene.getStylesheets().clear();
        }
    }
}

