package component.run.task.task.properties;

import com.google.gson.Gson;
import component.run.task.RunTaskTabController;
import component.run.task.task.properties.tableViewRow.Row;
import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import mission.AdminTotalInfoForMissionWithOutParams;
import okhttp3.*;
import output.AllDependenciesOfTargetDto;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.UnaryOperator;

public class TaskPropertiesController {
    private RunTaskTabController runTaskTabCont;
    private Tooltip sourceJavaFilesFolderTooltip;
    private Tooltip destinationCompiledFilesFolderTooltip;
    private String selectedGraphName;

    private Set<Row> allRowsForTableView;
    private List<String> targetsNamesFromLastRun;
    private SimpleStringProperty pathToSourceJavaFilesFolder;
    private SimpleStringProperty pathToDestinationCompiledFilesFolder;
    private SimpleBooleanProperty readyToSetDependenciesOnTableView;
    private SimpleBooleanProperty isThereAtLeastOneCheckBoxSelectedInTableView;
    private SimpleBooleanProperty haveAllSimulationParamsReady;
    private SimpleBooleanProperty haveAllCompilationParamsReady;
    private SimpleBooleanProperty commonParamsForAllTaskReady;
    private SimpleBooleanProperty finishedRunTaskPropertiesController;

    @FXML private TableView<Row> dynamicTargetsTableView;
    @FXML private TableColumn<Row,String> targetsColumn;
    @FXML private TableColumn<Row,String> SelectTargetColumn;
    @FXML private CheckBox runTaskOnSpecificTargetWithDirectionCheckBox;
    @FXML private VBox directionsVbox;
    @FXML private RadioButton dependsOnRadioButton;
    @FXML private RadioButton requiredForRadioButton;
    @FXML private RadioButton simulationRadioButton;
    @FXML private RadioButton compilationRadioButton;
    @FXML private Button createTaskButton;
    @FXML private Label successProbabilityViewLabel;
    @FXML private Slider simulationProbabilityOfSuccessSlider;
    @FXML private Label warningProbabilityViewLabel;
    @FXML private Slider simulationProbabilityOfWarningSlider;
    @FXML private TextField processTimeTextField;
    @FXML private CheckBox isRandomTimeCheckBox;
    @FXML private Label chooseOneTargetLabel;
    @FXML private VBox simulationParametersVbox;
    @FXML private VBox compilationParametersVbox;
    @FXML private Button sourceJavaFilesFolderButton;
    @FXML private Button destinationCompiledFilesFolderButton;
    @FXML private ToolBar selectAllAndRemoveAllToolbar;
    @FXML private VBox chooseDirectionAndTaskTypeDirectionVbox;
    @FXML private TextField taskNameTextField;
    @FXML private Label uniqueTaskNameErrorLabel;

   public TaskPropertiesController(){
       selectedGraphName=null;
       targetsNamesFromLastRun=null;
       pathToSourceJavaFilesFolder=new SimpleStringProperty("Not selected yet!");
       pathToDestinationCompiledFilesFolder=new SimpleStringProperty("Not selected yet!");
       readyToSetDependenciesOnTableView =new SimpleBooleanProperty(false);
       isThereAtLeastOneCheckBoxSelectedInTableView=new SimpleBooleanProperty(false);
       sourceJavaFilesFolderTooltip=new Tooltip();
       destinationCompiledFilesFolderTooltip=new Tooltip();
       haveAllSimulationParamsReady=new SimpleBooleanProperty(false);
       haveAllCompilationParamsReady=new SimpleBooleanProperty(false);
       commonParamsForAllTaskReady=new SimpleBooleanProperty(false);
       finishedRunTaskPropertiesController=new SimpleBooleanProperty(false);
   }

      @FXML public void initialize() {
        finishedRunTaskPropertiesController.addListener(observable ->
                {
                    if (finishedRunTaskPropertiesController.getValue()) {
                        String lastTaskName= taskNameTextField.getText();
                        targetsNamesFromLastRun = runTaskTabCont.getTargetsNamesInGraphTaskOnlyFromSuper(selectedGraphName,lastTaskName);
                        showOnlyTargetsFromLastRun();
                        selectAllAndRemoveAllToolbar.setDisable(true);
                        chooseDirectionAndTaskTypeDirectionVbox.setDisable(true);
                    }
                }
        );
       commonParamsForAllTaskReady.bind(isThereAtLeastOneCheckBoxSelectedInTableView
               .and(taskNameTextField.textProperty().isEmpty().not()));

         haveAllSimulationParamsReady.bind(simulationRadioButton.selectedProperty()
                 .and(processTimeTextField.textProperty().isEmpty().not()));

        haveAllCompilationParamsReady.bind(compilationRadioButton.selectedProperty()
                .and(pathToDestinationCompiledFilesFolder.isEqualTo("Not selected yet!").not()
                        .and(pathToSourceJavaFilesFolder.isEqualTo("Not selected yet!").not())));

        readyToSetDependenciesOnTableView.bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty()
                .and(dependsOnRadioButton.selectedProperty()
                        .or(requiredForRadioButton.selectedProperty()))
                .and(isThereAtLeastOneCheckBoxSelectedInTableView));

           createTaskButton.disableProperty().bind((commonParamsForAllTaskReady.and(haveAllCompilationParamsReady.or(haveAllSimulationParamsReady))).not());
             readyToSetDependenciesOnTableView.addListener((observer, oldValue, newValue) -> {
                 if(newValue) {updateTargetDependenciesOnTableView();}});

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        processTimeTextField.setTextFormatter(new TextFormatter<String>(integerFilter));

        simulationParametersVbox.disableProperty().bind(simulationRadioButton.selectedProperty().not());
        compilationParametersVbox.disableProperty().bind(compilationRadioButton.selectedProperty().not());

        directionsVbox.disableProperty().bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty().not());
        chooseOneTargetLabel.visibleProperty().bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty());

        successProbabilityViewLabel.textProperty().bind(simulationProbabilityOfSuccessSlider.accessibleTextProperty());
        StringExpression successProbabilityViewLabelExp = Bindings.concat("Probability: ",simulationProbabilityOfSuccessSlider.valueProperty());
        successProbabilityViewLabel.textProperty().bind(successProbabilityViewLabelExp);

        warningProbabilityViewLabel.textProperty().bind(simulationProbabilityOfWarningSlider.accessibleTextProperty());
        StringExpression warningProbabilityViewLabelExp = Bindings.concat("Probability: ",simulationProbabilityOfWarningSlider.valueProperty());
        warningProbabilityViewLabel.textProperty().bind(warningProbabilityViewLabelExp);

        dynamicTargetsTableView.disableProperty().bind(runTaskOnSpecificTargetWithDirectionCheckBox.selectedProperty()
                       .and(requiredForRadioButton.selectedProperty().not()
                .and(dependsOnRadioButton.selectedProperty().not())));
        ///************************************************************

        sourceJavaFilesFolderTooltip.setText(pathToSourceJavaFilesFolder.getValue());
        try {
            Field fieldBehavior = sourceJavaFilesFolderTooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(sourceJavaFilesFolderTooltip);
            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(500)));
        } catch (Exception e) {}
        sourceJavaFilesFolderButton.setTooltip(sourceJavaFilesFolderTooltip);

        destinationCompiledFilesFolderTooltip.setText(pathToDestinationCompiledFilesFolder.getValue());
        try {
            Field fieldBehavior = destinationCompiledFilesFolderTooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(destinationCompiledFilesFolderTooltip);
            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(500)));
        } catch (Exception e) {}
        destinationCompiledFilesFolderButton.setTooltip(destinationCompiledFilesFolderTooltip);
///************************************************************
    }

   private void clearAllPropertiesSelections() {
       OnActionRemoveAllTargetsButton();
       dependsOnRadioButton.setSelected(false);
       requiredForRadioButton.setSelected(false);
       simulationRadioButton.setSelected(false);
       compilationRadioButton.setSelected(false);
       runTaskOnSpecificTargetWithDirectionCheckBox.setSelected(false);
       isRandomTimeCheckBox.setSelected(false);
       simulationProbabilityOfWarningSlider.setValue(0);
       simulationProbabilityOfSuccessSlider.setValue(0);
       processTimeTextField.clear();
        sourceJavaFilesFolderTooltip.setText(pathToSourceJavaFilesFolder.getValue());
        pathToSourceJavaFilesFolder.setValue("Not selected yet!");
        pathToDestinationCompiledFilesFolder.setValue("Not selected yet!");
        destinationCompiledFilesFolderTooltip.setText(pathToDestinationCompiledFilesFolder.getValue());
        sourceJavaFilesFolderTooltip.setText(pathToSourceJavaFilesFolder.getValue());
        selectAllAndRemoveAllToolbar.setDisable(false);
        chooseDirectionAndTaskTypeDirectionVbox.setDisable(false);
        OnActionRemoveAllTargetsButton();
        showAllTargetsInTableView();
        isThereAtLeastOneCheckBoxSelectedInTableView.setValue(false);
        taskNameTextField.clear();
       uniqueTaskNameErrorLabel.setText("");
    }

   public void setRunTaskTabController(RunTaskTabController runTaskTabCon, SimpleBooleanProperty finishedRunTaskFather){
        runTaskTabCont=runTaskTabCon;
        finishedRunTaskPropertiesController.bind(finishedRunTaskFather);
        targetsNamesFromLastRun=null;
    }

   private void updateTargetDependenciesOnTableView() {
        boolean isDepend = dependsOnRadioButton.isSelected();
        String targetName = null;
        for (Row r : allRowsForTableView) {
            if (r.getSelectTarget().isSelected()) {
                targetName = r.getTargetName();
                break;
            }
        }
        AllDependenciesOfTargetDto data = runTaskTabCont.getAllDependenciesOfTargetDtoFromSuper(targetName,selectedGraphName);
        if(isDepend){
            printTheDependenciesToTableView( data.getTotalDependsOn());
        }
        else {
            printTheDependenciesToTableView(data.getTotalRequiredFor());
        }
    }

   private void printTheDependenciesToTableView (Map < String, String > dependencies){
            for (Row r : allRowsForTableView) {
                if (dependencies.containsKey(r.getTargetName())) {
                    r.getSelectTarget().setSelected(true);
                }
            }
        }

   @FXML private void OnActionSelectAllTargetsButton () {
            ChooseTargetsButtonState(true);
        }

   @FXML private void OnActionRemoveAllTargetsButton () {
            ChooseTargetsButtonState(false);
        }

   @FXML private void OnActionRunTaskOnSpecificTargetWithDirectionCheckBox () {
            if (runTaskOnSpecificTargetWithDirectionCheckBox.isSelected()) {
                OnActionRemoveAllTargetsButton();
            } else {
                dependsOnRadioButton.setSelected(false);
                requiredForRadioButton.setSelected(false);
            }
        }

   @FXML private void OnActionCreateTaskButton(){
       if (finishedRunTaskPropertiesController.getValue()) {
           String lastTaskName= taskNameTextField.getText();
           targetsNamesFromLastRun = runTaskTabCont.getTargetsNamesInGraphTaskOnlyFromSuper(selectedGraphName,lastTaskName);
           selectAllAndRemoveAllToolbar.setDisable(false);
           showAllTargetsInTableView();
           chooseDirectionAndTaskTypeDirectionVbox.setDisable(false);
       }
       String missionName = taskNameTextField.getText();
       String createdBy = runTaskTabCont.getCreatedByUserNameFromSuper();
       String graphName = selectedGraphName;
       String taskType;
       Boolean isScratchIn = true;
       runTaskTabCont.updateSuperAboutNewMission(missionName, graphName);
       String[] targetsToRun = getSelectedTargets();
       TaskInputDto taskInput;
       if (simulationRadioButton.isSelected()) {
           taskType = "Simulation";
           Double successRateIn = simulationProbabilityOfSuccessSlider.getValue();
           Double warningRateIn = simulationProbabilityOfWarningSlider.getValue();
           Boolean isRandomIn = isRandomTimeCheckBox.isSelected();
           Long processTimeIn = Long.valueOf(processTimeTextField.getText());
           taskInput = new SimulationTaskInputDto(processTimeIn, isRandomIn, successRateIn, warningRateIn, isScratchIn, targetsToRun);
       } else {//compilation radio button is selected
           taskType = "Compilation";
           taskInput = new CompilationTaskInputDto(isScratchIn, pathToSourceJavaFilesFolder.getValue(), pathToDestinationCompiledFilesFolder.getValue(), targetsToRun);
       }
       AdminTotalInfoForMissionWithOutParams adminTotalInfoForMissionWithOutParams = new AdminTotalInfoForMissionWithOutParams(missionName, graphName, createdBy, taskType);
       Gson gson = new Gson();
       String jsonAdminTotalInfoForMissionWithOutParams = gson.toJson(adminTotalInfoForMissionWithOutParams);
       String jsonTaskInput = gson.toJson(taskInput);

       RequestBody body =
               new MultipartBody.Builder()
                       .addFormDataPart("jsonAdminTotalInfoForMissionWithOutParams", jsonAdminTotalInfoForMissionWithOutParams)
                       .addFormDataPart("jsonTaskInput", jsonTaskInput)
                       .build();

       Request request = new Request.Builder()
               .url(Constants.SEND_NEW_MISSION_TO_SERVER)
               .post(body)
               .build();
       Call call = HttpClientUtil.getOkHttpClient().newCall(request);
       try {
           Response response = call.execute();
           if (response.code() != 200) {
               Platform.runLater(() -> {//mission did not creat successfully
                   {
                       uniqueTaskNameErrorLabel.setText("Task name already exists!");
                   }
               });
           }
           else{
               Platform.runLater(() -> {//mission did not creat successfully
                   {
                       uniqueTaskNameErrorLabel.setText("");
                       runTaskTabCont.handleTabsWhenTaskCreated();
                   }
               });
           }
       }
       catch (IOException e) {}
    }

   @FXML private void onActionSourceJavaFilesFolderButton () {
        DirectoryChooser directoryChooser  = new DirectoryChooser();
        directoryChooser.setTitle("Select the java source files directory");
        File srcDirectory = directoryChooser.showDialog(runTaskTabCont.getPrimaryStageFromSuper());
        if (srcDirectory == null) {
            return;
        }
        String absolutePath = srcDirectory.getAbsolutePath();
        pathToSourceJavaFilesFolder.setValue(absolutePath);
        sourceJavaFilesFolderTooltip.setText("Selected folder:\n"+absolutePath+"\n");
        }

   @FXML private void onActionDestinationCompiledFilesFolderButton () {
        DirectoryChooser directoryChooser  = new DirectoryChooser();
        directoryChooser.setTitle("Select the compiled files destination folder");
        File destDirectory = directoryChooser.showDialog(runTaskTabCont.getPrimaryStageFromSuper());
        if (destDirectory == null) {
            return;
        }
        String absolutePath = destDirectory.getAbsolutePath();
        pathToDestinationCompiledFilesFolder.setValue(absolutePath);
        destinationCompiledFilesFolderTooltip.setText("Selected folder:\n"+absolutePath+"\n");
        }

   private String[] getSelectedTargets(){
        Set<String> selectedTargets=new HashSet<>();
        for(Row r:dynamicTargetsTableView.getItems()) {
            if(r.getSelectTarget().isSelected()){
                selectedTargets.add(r.getTargetName());
            }
        }
        String[] res=new String[selectedTargets.size()];
        int i=0;
        for(String s:selectedTargets){
            res[i]=s;
            i++;
        }
        return res;
    }

   private void updateIfAnyCheckBoxInTableViewIsTicked(){
        for(Row r:dynamicTargetsTableView.getItems()) {
            if(r.getSelectTarget().isSelected()){
                isThereAtLeastOneCheckBoxSelectedInTableView.setValue(true);
                return;
            }
        }
        isThereAtLeastOneCheckBoxSelectedInTableView.setValue(false);
    }

   private Row getTargetRow(String targetName){
        for(Row r:allRowsForTableView) {
            if(r.getTargetName().equals(targetName)){
                return r;
            }
        }
        return null;
    }

   private void showAllTargetsInTableView() {
            if (targetsNamesFromLastRun != null) {
             for(Row r:dynamicTargetsTableView.getItems()) {
                String targetFromTableViewName=r.getTargetName();
                    Row disableThisTargetRow=getTargetRow(targetFromTableViewName);
                    disableThisTargetRow.getSelectTarget().disableProperty().setValue(false);
            }
        }
    }

   @FXML private void showOnlyTargetsFromLastRun() {
        if (targetsNamesFromLastRun != null) {
            for (Row r : dynamicTargetsTableView.getItems()) {
                String targetFromTableViewName = r.getTargetName();
                Row disableThisTargetRow = getTargetRow(targetFromTableViewName);
                disableThisTargetRow.getSelectTarget().disableProperty().setValue(true);
                if (!targetsNamesFromLastRun.contains(targetFromTableViewName)) {
                    disableThisTargetRow.getSelectTarget().setSelected(false);
                } else {
                    disableThisTargetRow.getSelectTarget().setSelected(true);
                }
            }
        }
    }

   private void ChooseTargetsButtonState(Boolean state){
       for(Row r:dynamicTargetsTableView.getItems()) {r.getSelectTarget().selectedProperty().setValue(state);}
   }

   public void initAllWhenGraphSelected(String selectedGraphNameIn, Map<String,String> graphPricePerTargetAccordingToTaskTypeMap){
       selectedGraphName=selectedGraphNameIn;
       initTargetsTableView();
       clearAllPropertiesSelections();
       targetsNamesFromLastRun=null;
       setTaskTypeRadioButtonsDisable(graphPricePerTargetAccordingToTaskTypeMap);
    }

   private void setTaskTypeRadioButtonsDisable(Map<String,String> graphPricePerTargetAccordingToTaskTypeMap){
        if(graphPricePerTargetAccordingToTaskTypeMap!=null) {
            boolean isPossibleToDoSimulation = true;
            boolean isPossibleToDoCompilation = true;
            if (graphPricePerTargetAccordingToTaskTypeMap.get("Simulation").equals(" ")) {
                isPossibleToDoSimulation = false;
            }
            if (graphPricePerTargetAccordingToTaskTypeMap.get("Compilation").equals(" ")) {
                isPossibleToDoCompilation = false;
            }
             if(isPossibleToDoCompilation && isPossibleToDoSimulation){//two of the options are open
                compilationRadioButton.setDisable(false);
                simulationRadioButton.setDisable(false);
            }
            else if(!isPossibleToDoCompilation){//the compilation is not possible but the simulation is possible
                compilationRadioButton.setDisable(true);
                simulationRadioButton.setDisable(false);
            }
            else{
                compilationRadioButton.setDisable(false);
                simulationRadioButton.setDisable(true);
            }

            }



    }

   private void initTargetsTableView(){
       try {
           allRowsForTableView = runTaskTabCont.getAllTargetsNamesInSetFromRunTask(selectedGraphName);
       }
       catch (IOException ignore){}
        for(Row row : allRowsForTableView)
        {
            row.getSelectTarget().selectedProperty().addListener((observer,prev,newValue)->{
                updateIfAnyCheckBoxInTableViewIsTicked();
            });
        }
        final ObservableList<Row> data =FXCollections.observableList(new ArrayList<>(allRowsForTableView));
        targetsColumn.setCellValueFactory(
                new PropertyValueFactory<>("targetName")
        );
        SelectTargetColumn.setCellValueFactory(
                new PropertyValueFactory<>("selectTarget")
        );
        dynamicTargetsTableView.setItems(data);
        dynamicTargetsTableView.getColumns().clear();
        dynamicTargetsTableView.getColumns().addAll(SelectTargetColumn,targetsColumn);
    }

   public void cleanTab(){
       clearAllPropertiesSelections();
    }

    @FXML private void onActionChatButton(){
        runTaskTabCont.openChat();
    }
}
