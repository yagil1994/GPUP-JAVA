package component.run.task.the.task;

import com.google.gson.reflect.TypeToken;
import component.run.task.RunTaskTabController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import mission.TargetListViewDetails;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import output.UpdateTargetStatusDuringTaskDto;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class TheTaskController {
    private RunTaskTabController runTaskTabCont;
    private SimpleBooleanProperty finishedRunTaskTheTaskController;
    private String missionName;
    private Timer missionDataTimer, listViewTimer;

    @FXML private Label missionNameLabel;
    @FXML private Label graphNameLabel;
    @FXML private Label totalWorkersRegisteredLabel;
    @FXML private Label targetsRunningToAvailableLabel;
    @FXML private Label totalTargetsLabel;
    @FXML private Label independentsLabel;
    @FXML private Label leavesLabel;
    @FXML private Label middlesLabel;
    @FXML private Label rootsLabel;
    @FXML private TextArea taskReportDisplayedInText;

    @FXML private Label progressTaskLabel;
    @FXML private ProgressBar taskProgressBar;
    @FXML private Button resumeTaskButton;
    @FXML private Button pauseTaskButton;
    @FXML private Button playTaskButton;
    @FXML private Button stopTaskButton;
    @FXML private TextArea taskDisplayedInText;
    @FXML private TextArea infoAboutSelectedTargetDuringTaskTextArea;
    @FXML private HBox rightControlPanelHBox;
    @FXML private HBox controlPanelHBox;

    @FXML private ListView<String> frozenTargetsListView;
    @FXML private ListView<String> skippedTargetsListView;
    @FXML private ListView<String> waitingTargetsListView;
    @FXML private ListView<String> inProgressTargetsListView;
    @FXML private ListView<String> finishedWIthSuccessTargetsListView;
    @FXML private ListView<String> finishedWIthWarningTargetsListView;
    @FXML private ListView<String> finishedWIthFailureTargetsListView;
    @FXML private ListView<String> finishedInPreviousTasksWithSuccessOrWarningListView;

    public TheTaskController() {
        finishedRunTaskTheTaskController = new SimpleBooleanProperty(false);
    }


    @FXML public void initialize() {
        pauseTaskButton.disableProperty().addListener((obserb) -> {
            if (pauseTaskButton.isDisable()) {
                resumeTaskButton.setDisable(false);
            } else {
                resumeTaskButton.setDisable(true);
            }
        });

        resumeTaskButton.disableProperty().addListener((obserb) -> {
            if (resumeTaskButton.isDisable()) {
                pauseTaskButton.setDisable(false);
            } else {
                pauseTaskButton.setDisable(true);
            }
        });
    }

    @FXML private void onActionPlayButton(){
        changeMissionStatus("PLAYING",missionName);
        rightControlPanelHBox.setDisable(false);
        playTaskButton.setDisable(true);
        startListViewRefresher();
        runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).startAskingForTaskReport();
    }

    @FXML private void onActionStopButton(){
        changeMissionStatus("STOPPED",missionName);
        controlPanelHBox.setDisable(true);
    }

    @FXML private void onActionPauseButton() {
        changeMissionStatus("PAUSED",missionName);
        pauseTaskButton.setDisable(true);
    }

    @FXML private void onActionResumeButton() {
        changeMissionStatus("RESUMED",missionName);
        resumeTaskButton.setDisable(true);
    }

    private void changeMissionStatus(String actionTodo,String missionName){
        String finalUrl = HttpUrl
                .parse(Constants.CHANGE_MISSION_STATUS)
                .newBuilder()
                .addQueryParameter("missionName", missionName)
                .addQueryParameter("actionToDoDuringTask",actionTodo)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl,new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->{}
                          //  errorMessageProperty.set("Something went wrong: " + e.getMessage())
                    );
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
    public void updateTaskReportDisplayedInText(List<String> taskReportStringList){
        taskReportStringList.forEach(s->taskReportDisplayedInText.appendText(s));
    }

    public void startRunningAndAvailableAndWorkersRefresher() {
        missionDataTimer = new Timer();
        class Helper extends TimerTask {
            public void run()
            {
                runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).requestToUpdateDataFromServer();
                String workersRegisteredAmount = runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getRegisteredWorkers().toString();
                String targetsRunningToAvailable = runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getTargetsRunningZAvailableTargets();
                updateRunningAndAvailableAndWorkers(workersRegisteredAmount, targetsRunningToAvailable);
                if (runTaskTabCont.getInnerInfoFromSuper().getMissionStatus(missionName).equals("FINISHED")) {
                    Platform.runLater(()-> controlPanelHBox.setDisable(true));
                }
            }
        }
        TimerTask UpdateRunningAndAvailableAndWorkers = new Helper();
        missionDataTimer.schedule(UpdateRunningAndAvailableAndWorkers, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }

    private void updateRunningAndAvailableAndWorkers(String workersRegisteredAmount, String targetsRunningToAvailable){
        Platform.runLater(() -> {
            totalWorkersRegisteredLabel.setText(workersRegisteredAmount);
            targetsRunningToAvailableLabel.setText(targetsRunningToAvailable);
            taskProgressBar.setProgress(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getProcessCalc());
            progressTaskLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getProcessCalcStr());

            }
        );
    }

    @FXML private void showTargetInfoDuringTask(MouseEvent event){
        infoAboutSelectedTargetDuringTaskTextArea.clear();
        String selectedTarget=null;
        boolean selectedAny=false;
       if(!frozenTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
             selectedTarget= frozenTargetsListView.getSelectionModel().getSelectedItem();
           frozenTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!skippedTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= skippedTargetsListView.getSelectionModel().getSelectedItem();
           skippedTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!waitingTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= waitingTargetsListView.getSelectionModel().getSelectedItem();
           waitingTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!inProgressTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= inProgressTargetsListView.getSelectionModel().getSelectedItem();
           inProgressTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedWIthSuccessTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedWIthSuccessTargetsListView.getSelectionModel().getSelectedItem();
           finishedWIthSuccessTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedWIthWarningTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedWIthWarningTargetsListView.getSelectionModel().getSelectedItem();
           finishedWIthWarningTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedWIthFailureTargetsListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedWIthFailureTargetsListView.getSelectionModel().getSelectedItem();
           finishedWIthFailureTargetsListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }
       else if(!finishedInPreviousTasksWithSuccessOrWarningListView.getSelectionModel().getSelectedItems().isEmpty()){
           selectedTarget= finishedInPreviousTasksWithSuccessOrWarningListView.getSelectionModel().getSelectedItem();
           finishedInPreviousTasksWithSuccessOrWarningListView.getSelectionModel().clearSelection();
           selectedAny=true;
       }

       if(selectedAny) {
           requestTargetListViewDetails(selectedTarget);
       }
    }
    public void requestTargetListViewDetails(String targetName){
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_TARGET_LIST_VIEW_DETAILS)
                .newBuilder()
                .addQueryParameter("missionName",missionName)
                .addQueryParameter("targetName",targetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Type type = new TypeToken<TargetListViewDetails>() {
                }.getType();
                String jsonTargetListViewDetails = response.body().string();
                TargetListViewDetails details = Constants.GSON_INSTANCE.fromJson(jsonTargetListViewDetails, type);
                synchronized (this) {
                    updateInfoAboutSelectedTargetDuringTaskTextArea(details);
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
    private void updateInfoAboutSelectedTargetDuringTaskTextArea(TargetListViewDetails targetListViewDetails) {
        String targetName = targetListViewDetails.getName();
        String level = targetListViewDetails.getLevel();
        Set<String> directDependsSet = targetListViewDetails.getDirectDependsList();
        String msWaitingTimeInQueueAlready = targetListViewDetails.getMsWaitingTimeInQueueAlready();
        String msInProcessTimeTookAlready = targetListViewDetails.getMsInProcessTimeTookAlready();
        Set<String> directAndIndirectDependsListThatFailed = targetListViewDetails.getDirectAndIndirectDependsListThatFailed();
        String finishRunningResultType = targetListViewDetails.getFinishRunningResultType();
        String targetRunningState = targetListViewDetails.getTargetRunningState();

        String targetNameLine = "Target Name: " + targetName + "\n";
        String levelLine = "Level: " + level + "\n";
        String res = "";
        switch (targetRunningState) {
            case "FROZEN": {
                res += frozenCase(directDependsSet, targetName);
                break;
            }
            case "SKIPPED": {
                res += skippedCase(directAndIndirectDependsListThatFailed, targetName);
                break;
            }
            case "WAITING": {
                res += "The target has been already waiting " + msWaitingTimeInQueueAlready + "milliseconds\n";
                break;
            }
            case "IN_PROCESS": {
                res += "The process time of the task on the target has already been for " + msInProcessTimeTookAlready + "milliseconds\n";
                break;
            }
            case "FINISHED": {
                if (finishRunningResultType.equals("DONT_CHECK")) {
                    res += "The target did not participate in this task- it finished in a previous task\n";
                    break;
                }
                res += "The task has been already finished on the target with " + finishRunningResultType.toLowerCase() + "\n";
                break;
            }
        }
        String finalInfo = targetNameLine + levelLine + res;
        infoAboutSelectedTargetDuringTaskTextArea.appendText(finalInfo);
    }
    private String frozenCase(Set<String> directDependsSet,String targetName){
        String res="The targets that "+targetName+" is waiting for their successful finish are: ";
        List<String> directDependsList= new ArrayList<>(directDependsSet);
        for(String t:directDependsList){
            if(directDependsList.get(0).equals(t)){
                res+=t;
            }
            else{
                res= res +", "+t;
            }
        }
        res=res+"\n";
        return res;
    }
    private String skippedCase( Set<String> directAndIndirectDependsListThatFailed,String targetName){
        String res="The targets that their process have been failed and because of them "+targetName+" is skipped are: ";
        List<String> directAndIndirectDependsListThatFailedList= new ArrayList<>(directAndIndirectDependsListThatFailed);
        for(String t:directAndIndirectDependsListThatFailedList){
            if(directAndIndirectDependsListThatFailedList.get(0).equals(t)){
                res+=t;
            }
            else{
                res= res +", "+t;
            }
        }
        res=res+"\n";
        return res;
    }


    private void clearTask() {
        frozenTargetsListView.getItems().clear();
        skippedTargetsListView.getItems().clear();
        waitingTargetsListView.getItems().clear();
        inProgressTargetsListView.getItems().clear();
        finishedWIthSuccessTargetsListView.getItems().clear();
        finishedWIthWarningTargetsListView.getItems().clear();
        finishedWIthFailureTargetsListView.getItems().clear();
        finishedInPreviousTasksWithSuccessOrWarningListView.getItems().clear();
        infoAboutSelectedTargetDuringTaskTextArea.clear();
        taskDisplayedInText.clear();
        taskReportDisplayedInText.clear();
    }
    public void initTask(String missionNameIn) {
        missionName=missionNameIn;
        clearTask();
        missionNameLabel.setText(missionName);
        graphNameLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getGraphName());
        totalTargetsLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getTotalTargets());
        independentsLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getIndependentsAmount());
        leavesLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getLeavesAmount());
        middlesLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getMiddlesAmount());
        rootsLabel.setText(runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getRootsAmount());
        startRunningAndAvailableAndWorkersRefresher();
        LinkedList<String>allLogs=runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getAllLogs();
        allLogs.forEach(s->taskDisplayedInText.appendText(s));
        List<String>taskReport=runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getTaskReport();
        if(taskReport!=null){
            taskReport.forEach(s->taskReportDisplayedInText.appendText(s));
        }
        String missionStatus= runTaskTabCont.getInnerInfoFromSuper().getMissionStatus(missionNameIn);
       switch (missionStatus){
           case "NEW_MISSION":{
               controlPanelHBox.setDisable(false);
               rightControlPanelHBox.setDisable(true);
               playTaskButton.setDisable(false);
               break;
           }
           case "PLAYING":{
               controlPanelHBox.setDisable(false);
               rightControlPanelHBox.setDisable(false);
               playTaskButton.setDisable(true);
               stopTaskButton.setDisable(false);
               pauseTaskButton.setDisable(false);
               break;
           }
           case "STOPPED":
           case "FINISHED": {
             controlPanelHBox.setDisable(true);
             break;
           }
           case "PAUSED":{
             controlPanelHBox.setDisable(false);
             rightControlPanelHBox.setDisable(false);
             resumeTaskButton.setDisable(false);
             stopTaskButton.setDisable(false);
              pauseTaskButton.setDisable(true);
               playTaskButton.setDisable(true);
               break;
           }
           case "RESUMED":{
             controlPanelHBox.setDisable(false);
             rightControlPanelHBox.setDisable(false);
             pauseTaskButton.setDisable(false);
             stopTaskButton.setDisable(false);
               playTaskButton.setDisable(true);
               break;
           }
       }
    }

    public SimpleBooleanProperty getFinishedRunTaskTheTaskController() {return finishedRunTaskTheTaskController;}

    public void setRunTaskTabController(RunTaskTabController runTaskTabCon, SimpleBooleanProperty finishedRunTaskFather) {
        runTaskTabCont = runTaskTabCon;
        finishedRunTaskFather.bind(finishedRunTaskTheTaskController);
    }

    public void startListViewRefresher() {
        listViewTimer = new Timer();
        class Helper extends TimerTask {
            public void run()
            {
                synchronized (this) {
                    List<UpdateTargetStatusDuringTaskDto> dtoList = runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getUpdateTargetStatusDuringTaskDtoList();
                    String log = runTaskTabCont.getInnerInfoFromSuper().getMyMissionInfoMap().get(missionName).getLog();
                    Platform.runLater(() -> {
                                updateListView(dtoList);
                                if (log != null) {
                                    taskDisplayedInText.appendText(log);
                                }
                            }
                    );
                }
            }
        }
        TimerTask UpdateListView = new Helper();
        listViewTimer.schedule(UpdateListView, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }
    public void updateListView(List<UpdateTargetStatusDuringTaskDto> dtoList) {
        frozenTargetsListView.getItems().clear();
        inProgressTargetsListView.getItems().clear();
        waitingTargetsListView.getItems().clear();
        skippedTargetsListView.getItems().clear();
        finishedInPreviousTasksWithSuccessOrWarningListView.getItems().clear();
        finishedWIthFailureTargetsListView.getItems().clear();
        finishedWIthSuccessTargetsListView.getItems().clear();
        finishedWIthWarningTargetsListView.getItems().clear();
        for (UpdateTargetStatusDuringTaskDto dto : dtoList) {
            if (dto.getNewStatusOnTask() != null) {
                addTargetStatusDuringTask(dto);
            }
        }
    }

    public void updateListView1(List<UpdateTargetStatusDuringTaskDto> dtoList) { // (can be also clearing all tables and add)
        for (UpdateTargetStatusDuringTaskDto dto : dtoList) {
            if (dto.getPrevStatusOnTask() != null) {
                removeTargetStatusDuringTask(dto);
            }
            if (dto.getNewStatusOnTask() != null) {
                addTargetStatusDuringTask(dto);
            }
        }
    }
    private void removeTargetStatusDuringTask(UpdateTargetStatusDuringTaskDto dto) {
         switch (dto.getPrevStatusOnTask()) {
             case "FROZEN": {
                 frozenTargetsListView.getItems().remove(dto.getTargetName());
                 break;
             }
             case "WAITING": {
                 waitingTargetsListView.getItems().remove(dto.getTargetName());
                 break;
             }
             case "IN_PROCESS": {
                 inProgressTargetsListView.getItems().remove(dto.getTargetName());
                 break;
             }
         }
     }
    private void addTargetStatusDuringTask(UpdateTargetStatusDuringTaskDto dto){
             switch (dto.getNewStatusOnTask()) {
                 case "FROZEN": {
                     if(!frozenTargetsListView.getItems().contains(dto.getTargetName())) {
                         frozenTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "SKIPPED": {
                     if(!skippedTargetsListView.getItems().contains(dto.getTargetName())) {
                         skippedTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "WAITING": {
                     if(!waitingTargetsListView.getItems().contains(dto.getTargetName())) {
                         waitingTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "IN_PROCESS": {
                     if(!inProgressTargetsListView.getItems().contains(dto.getTargetName())) {
                         inProgressTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "SUCCESS": {
                     if(!finishedWIthSuccessTargetsListView.getItems().contains(dto.getTargetName())) {
                         finishedWIthSuccessTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "WARNING": {
                     if(!finishedWIthWarningTargetsListView.getItems().contains(dto.getTargetName())) {
                         finishedWIthWarningTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "FAILURE": {
                     if(!finishedWIthFailureTargetsListView.getItems().contains(dto.getTargetName())) {
                         finishedWIthFailureTargetsListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
                 case "DONT_CHECK": {
                     if(!finishedInPreviousTasksWithSuccessOrWarningListView.getItems().contains(dto.getTargetName())) {
                         finishedInPreviousTasksWithSuccessOrWarningListView.getItems().add(dto.getTargetName());
                     }
                     break;
                 }
             }
        }

    @FXML private void onActionChatButton(){
        runTaskTabCont.openChat();
    }
}







