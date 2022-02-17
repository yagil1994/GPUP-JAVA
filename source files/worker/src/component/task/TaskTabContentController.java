package component.task;

import component.supercontroller.WorkerSuperController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mini.engine.TargetMission;
import mini.engine.row.GeneralInfoTableviewRow;
import mini.engine.row.ProcessTableViewRow;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TaskTabContentController {

    @FXML private Button pauseTaskButton;
    @FXML  private Button resumeTaskButton;
    @FXML  private Button stopTaskButton;
    @FXML private TextArea infoAboutSelectedTargetDuringTaskTextArea;
    @FXML private Label availableTreadsLabel;
    @FXML private Label busyTreadsLabel;
    @FXML private Label totalTreadsLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private HBox controlPanelHBox;

    @FXML private TableView<GeneralInfoTableviewRow> generalInfoTableview;
    @FXML private TableColumn<GeneralInfoTableviewRow, String> generalMissionNameCol;
    @FXML private TableColumn<GeneralInfoTableviewRow, String> generalTaskTypeCol;
    @FXML private TableColumn<GeneralInfoTableviewRow, String> generalTargetNameCol;
    @FXML private TableColumn<GeneralInfoTableviewRow, String> generalStatusCol;
    @FXML private TableColumn<GeneralInfoTableviewRow, String> generalPriceCol;

    @FXML private TableView<ProcessTableViewRow> processTableView;
    @FXML private TableColumn<ProcessTableViewRow, String> processSelection;
    @FXML private TableColumn<ProcessTableViewRow, String> processMissionNameCol;
    @FXML private TableColumn<ProcessTableViewRow, String> processWorkersOnMissionCol;
    @FXML private TableColumn<ProcessTableViewRow, String> processProgressCol;
    @FXML private TableColumn<ProcessTableViewRow, String> processAmountOfProcessedTargetsCol;
    @FXML private TableColumn<ProcessTableViewRow, String> processCreditsCol;

    private WorkerSuperController sController;
    private Integer amountOfTreads;
    private Timer processTimer, generalTargetsTimer,treadsTimer;
    private SimpleStringProperty creditsProperty;
    private ToggleGroup missionSelect;
    private SimpleBooleanProperty isThereAnyMissionSelected;
    private String selectedMissionFromProcessTableView;
    private Stage unregisteredStage;
    private Button yesButton;
    private Button noButton;
    private Scene scene;

    public TaskTabContentController(){
        creditsProperty=new SimpleStringProperty("0");
        missionSelect=new ToggleGroup();
        isThereAnyMissionSelected=new SimpleBooleanProperty(false);
        selectedMissionFromProcessTableView =null;
        unregisteredStage=new Stage();
        unregisteredStage.initModality(Modality.APPLICATION_MODAL);
        unregisteredStage.setTitle("Unregister mission");
        unregisteredStage.setResizable(false);
        Label infoToTheWorkerLabel= new Label();
        infoToTheWorkerLabel.setText("Are you sure?");
        infoToTheWorkerLabel.setFont(Font.font("verdana"));
        infoToTheWorkerLabel.setUnderline(true);
        VBox labelVBoxWrapper=new VBox();
        labelVBoxWrapper.getChildren().addAll(infoToTheWorkerLabel);
        labelVBoxWrapper.setAlignment(Pos.CENTER);
        HBox infoToTheWorkerHBox = new HBox(30);
        VBox infoToTheWorkerVBox= new VBox(20);
        infoToTheWorkerHBox.setPadding(new Insets(25, 25, 25, 25));
        yesButton= new Button();
        noButton= new Button();
        yesButton.setText("Yes");
        noButton.setText("No");
        infoToTheWorkerHBox.getChildren().addAll(yesButton,noButton);
        infoToTheWorkerVBox.getChildren().addAll(labelVBoxWrapper,infoToTheWorkerHBox);
        scene=new Scene(infoToTheWorkerVBox,150,130);
        unregisteredStage.setScene(scene);
    }

    @FXML public void initialize(){
        totalCreditsLabel.textProperty().bind(creditsProperty);
        controlPanelHBox.disableProperty().bind(isThereAnyMissionSelected.not());

        yesButton.setOnAction(event -> {
            if(selectedMissionFromProcessTableView!=null) {
                isThereAnyMissionSelected.setValue(false);
                sController.getInner().removeMission(selectedMissionFromProcessTableView);
                tellServerThatWorkerUnregisterMission(selectedMissionFromProcessTableView, sController.getWorkerUserName());
                selectedMissionFromProcessTableView=null;
                updateProcessTableView();
            }
           unregisteredStage.close();
        });

        noButton.setOnAction(event -> {
            unregisteredStage.close();
        });
    }

    public void setWorkerSuperController(WorkerSuperController superCont){sController=superCont;}

    private void updateCredits(){
        creditsProperty.setValue(sController.getInner().getTotalCredits());
    }
    public void updateAmountOfTreads(Integer amountOfTreadsIn){
        amountOfTreads=amountOfTreadsIn;
        totalTreadsLabel.setText(amountOfTreads.toString());
        startAvailableAndBusyTreadsRefresher();
    }

    public void startAvailableAndBusyTreadsRefresher() {
        treadsTimer = new Timer();
        class Helper extends TimerTask {
            public void run()
            {
                String availableTreads=sController.getWorkerThreadPoolManager().getAmountOfAvailableThreads().toString();
                String busyTreads=sController.getWorkerThreadPoolManager().getAmountOfBusyThreads().toString();
                updateAvailableAndBusyTreads(availableTreads, busyTreads);
            }
        }
        TimerTask AvailableAndBusyTreadsLabels = new Helper();
        treadsTimer.schedule(AvailableAndBusyTreadsLabels, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }

    private void updateAvailableAndBusyTreads(String availableTreads, String busyTreads){
        Platform.runLater(() -> {
            availableTreadsLabel.setText(availableTreads);
            busyTreadsLabel.setText(busyTreads);
                }
        );
    }

    @FXML private void onActionPauseButton(ActionEvent event) {
        String selectedMission=getSelectedMissionInProcessTableView();
        if(selectedMission!=null) {
            sController.pauseMission(selectedMission);
            pauseTaskButton.setDisable(true);
            resumeTaskButton.setDisable(false);
        }
    }

    @FXML private void onActionResumeButton(ActionEvent event) {
        String selectedMission=getSelectedMissionInProcessTableView();
        if(selectedMission!=null) {
            sController.resumeMission(selectedMission);
            pauseTaskButton.setDisable(false);
            resumeTaskButton.setDisable(true);
        }
    }

    @FXML private void onActionStopButton(ActionEvent event) {
       unregisteredStage.show();
    }

    private void setControlPanel(){
        if(selectedMissionFromProcessTableView!=null) {
            if (sController.isMissionExist(selectedMissionFromProcessTableView) && sController.isMissionPaused(selectedMissionFromProcessTableView)) {
                pauseTaskButton.setDisable(true);
                resumeTaskButton.setDisable(false);
            } else {
                pauseTaskButton.setDisable(false);
                resumeTaskButton.setDisable(true);
            }
        }
    }

    private void tellServerThatWorkerUnregisterMission(String missionName, String workerName){
        String finalUrl = HttpUrl
                .parse(Constants.TELL_SERVER_THAT_WORKER_UNREGISTER_MISSION)
                .newBuilder()
                .addQueryParameter("username",workerName)
                .addQueryParameter("missionName",missionName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

    public void updateGeneralInfoTableview() {
        Platform.runLater(() -> {
            updateCredits();
            List<GeneralInfoTableviewRow>targets= sController.getInner().getGeneralInfoTableviewRowList();

            final ObservableList<GeneralInfoTableviewRow> items = FXCollections.observableArrayList(targets);
            generalMissionNameCol.setCellValueFactory(
                    new PropertyValueFactory<>("missionName")
            );
            generalTaskTypeCol.setCellValueFactory(
                    new PropertyValueFactory<>("taskType")
            );
            generalTargetNameCol.setCellValueFactory(
                    new PropertyValueFactory<>("targetName")
            );
            generalStatusCol.setCellValueFactory(
                    new PropertyValueFactory<>("status")
            );
            generalPriceCol.setCellValueFactory(
                    new PropertyValueFactory<>("price")
            );

            generalInfoTableview.setItems(items);
            generalInfoTableview.getColumns().clear();
            generalInfoTableview.getColumns().addAll(generalMissionNameCol,generalTaskTypeCol,generalTargetNameCol,
                    generalStatusCol,generalPriceCol);
        });
    }

    private List<ProcessTableViewRow> createProcessTableViewRowList(){
       List<ProcessTableViewRow>res=new ArrayList<>();
        Set<String> missionNames = sController.getInner().getUnfinishedRegisteredMissionsNamesSet();
       for(String missionName:missionNames) {
           String workersOnMission=sController.getInner().getRegisteredMissionsMap().get(missionName).getAmountOfWorkersOnTask();
           Double progress=sController.getInner().getRegisteredMissionsMap().get(missionName).getProgressPresent();
           String ProgressStr=String.format("%.2f",(progress*100));
           String amountOfProcessedTargetsFrom=sController.getInner().getWorkerProcessedTargetsFromMissionAmount(missionName).toString();
           String credits=sController.getInner().getPriceFromMission(missionName).toString();
           res.add(new ProcessTableViewRow(missionName,workersOnMission,ProgressStr,amountOfProcessedTargetsFrom,credits,missionSelect));
       }
       return res;
    }

    public void startProcessTableViewRefresher() {
        processTimer = new Timer();
        class Helper extends TimerTask
        {
            public void run()
            {
                updateProcessTableView();
            }
        }
        TimerTask updateProcessTable = new Helper();
        processTimer.schedule(updateProcessTable, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }

    public void startGeneralInfoTableViewRefresher() {
        generalTargetsTimer = new Timer();
        class Helper extends TimerTask
        {
            public void run()
            {
                updateGeneralInfoTableview();
            }
        }
        TimerTask updateGeneralInfoTable = new Helper();
        generalTargetsTimer.schedule(updateGeneralInfoTable, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }

    @FXML void onClickedGeneralTable(){
        if(generalInfoTableview.getSelectionModel().selectedItemProperty().get()!=null){
            String selectedTargetName=generalInfoTableview.getSelectionModel().selectedItemProperty().get().getTargetName();
            String selectedMissionName=generalInfoTableview.getSelectionModel().selectedItemProperty().get().getMissionName();
            List<String> logs=sController.getInner().getLogs(selectedTargetName,selectedMissionName);
            infoAboutSelectedTargetDuringTaskTextArea.clear();
            logs.forEach(s->infoAboutSelectedTargetDuringTaskTextArea.appendText(s));
        }
    }

    public void updateProcessTableView() {
        Platform.runLater(() -> {
            String selectedMission=getSelectedMissionInProcessTableView();
            List<ProcessTableViewRow>MissionsInProcess= createProcessTableViewRowList();
            final ObservableList<ProcessTableViewRow> items = FXCollections.observableArrayList(MissionsInProcess);
            processSelection.setCellValueFactory(
                    new PropertyValueFactory<>("selectedMissionRadioButton")
            );
            processMissionNameCol.setCellValueFactory(
                    new PropertyValueFactory<>("missionName")
            );
            processWorkersOnMissionCol.setCellValueFactory(
                    new PropertyValueFactory<>("workersOnMission")
            );
            processProgressCol.setCellValueFactory(
                    new PropertyValueFactory<>("progress")
            );
            processAmountOfProcessedTargetsCol.setCellValueFactory(
                    new PropertyValueFactory<>("amountOfProcessedTargetsFrom")
            );
            processCreditsCol.setCellValueFactory(
                    new PropertyValueFactory<>("credits")
            );

            processTableView.setItems(items);
            processTableView.getColumns().clear();
            processTableView.getColumns().addAll(processSelection,processMissionNameCol,processWorkersOnMissionCol,
                    processProgressCol,processAmountOfProcessedTargetsCol,processCreditsCol);
            SelectMissionThatWereSelectedInProcessTableView(selectedMission,true);
        });
    }

    private String getSelectedMissionInProcessTableView(){
        final ObservableList<ProcessTableViewRow> rows = FXCollections.observableArrayList(processTableView.getItems());
        for(ProcessTableViewRow r:rows){
            if(r.getSelectedMissionRadioButton().isSelected()){
                isThereAnyMissionSelected.setValue(true);
                selectedMissionFromProcessTableView =r.getMissionName();
                return r.getMissionName();
            }
        }
        selectedMissionFromProcessTableView=null;
        isThereAnyMissionSelected.setValue(false);
        return null;
    }

    private void SelectMissionThatWereSelectedInProcessTableView(String selectedMission, Boolean setTo){
        if(selectedMissionFromProcessTableView!=null) {
            final ObservableList<ProcessTableViewRow> rows = FXCollections.observableArrayList(processTableView.getItems());
            for (ProcessTableViewRow r : rows) {
                if (selectedMissionFromProcessTableView.equals(r.getMissionName())) {
                    r.getSelectedMissionRadioButton().setSelected(setTo);
                    setControlPanel();
                    return;
                }
            }
        }
    }
    @FXML private void onActionChatButton(){
        sController.openChat();
    }
}
