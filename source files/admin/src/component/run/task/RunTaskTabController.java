package component.run.task;

import component.run.task.task.properties.TaskPropertiesController;
import component.run.task.task.properties.tableViewRow.Row;
import component.run.task.the.task.TheTaskController;
import component.supercontroller.AdminSuperController;
import inner.InnerInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import output.AllDependenciesOfTargetDto;
import java.io.IOException;
import java.util.*;

public class RunTaskTabController {

    private AdminSuperController sController;
    private SimpleBooleanProperty finishedRunTaskFather;
    private String selectedGraphName;

    @FXML private TheTaskController TheTaskTabContentController;
    @FXML private ScrollPane TheTaskTabContent;

    @FXML private TaskPropertiesController TaskPropertiesTabContentController;
    @FXML private ScrollPane TaskPropertiesTabContent;

    @FXML private TabPane thePhysicalRunTaskTab;
    @FXML private Tab TaskPropertiesTab;
    @FXML private Tab TheTaskTab;

    public RunTaskTabController(){
        selectedGraphName=null;
        finishedRunTaskFather=new SimpleBooleanProperty(false);
    }
    @FXML public void initialize() {
        if (TheTaskTabContentController != null) {
            TheTaskTabContentController.setRunTaskTabController(this,finishedRunTaskFather);
        }
        if (TaskPropertiesTabContentController != null) {
            TaskPropertiesTabContentController.setRunTaskTabController(this,finishedRunTaskFather);
        }
        TheTaskTab.setDisable(true);
    }

    public void freezeTaskTabInsController(){
        sController.freezeTaskTab();
    }

    public void sameGraphSelected(){
        TaskPropertiesTab.setDisable(false);
        TheTaskTab.setDisable(true);
        thePhysicalRunTaskTab.getSelectionModel().select(TaskPropertiesTab);
        TaskPropertiesTabContentController.cleanTab();
    }
    public void updateTaskReportInTaskController(List<String> taskReportStringList){
       Platform.runLater(()-> TheTaskTabContentController.updateTaskReportDisplayedInText(taskReportStringList));
   }
    public void initSubTabsOnGraphSelected(String selectedGraphNameIn, Map<String, String> graphPricePerTargetAccordingToTaskTypeMap) {
        selectedGraphName=selectedGraphNameIn;
        TaskPropertiesTab.setDisable(false);
        TaskPropertiesTabContentController.cleanTab();
        TheTaskTabContentController.getFinishedRunTaskTheTaskController().setValue(false);
        thePhysicalRunTaskTab.getSelectionModel().select(TaskPropertiesTab);
        TheTaskTab.setDisable(true);
        TaskPropertiesTabContentController.initAllWhenGraphSelected(selectedGraphName,graphPricePerTargetAccordingToTaskTypeMap);
    }

    public void initAndClearSubTabsOnTaskSelected(String missionName){
        TheTaskTabContentController.initTask(missionName);
        thePhysicalRunTaskTab.getSelectionModel().select(TheTaskTab);
        TheTaskTab.setDisable(false);
        TaskPropertiesTab.setDisable(true);
    }

    public void handleTabsWhenTaskCreated(){
        sController.handleTabsWhenTaskCreated();
    }

    public InnerInfo getInnerInfoFromSuper(){return sController.getInfo();}

    public void updateSuperAboutNewMission(String missionName,String graphName) {
        sController.addNewAdminMission(missionName,graphName);
    }

    public String getCreatedByUserNameFromSuper(){return sController.getCreatedByUserName();}

    public List<String> getTargetsNamesInGraphTaskOnlyFromSuper(String selectedGraphName,String lastTaskName) {
       String[] input;
       List<String> res= new ArrayList<>();
       try {
           input = sController.getTargetsNamesInGraphTaskOnly(selectedGraphName,lastTaskName);
           res.addAll(Arrays.asList(input));
       }
       catch (IOException ignore){}
       return res;
    }

    public void setAdminSuperController(AdminSuperController superCont){
        sController=superCont;
    }

    public Set<Row> getAllTargetsNamesInSetFromRunTask(String selectedGraphName) throws IOException {
       String[] allTargetsNames= sController.getAllTargetsNamesInSetFromServer(selectedGraphName);
        Set<Row> allRows=new HashSet<>();
        for(String s:allTargetsNames){
            allRows.add(new Row(s));
        }
        return allRows;
    }

    public Stage getPrimaryStageFromSuper() {return sController.getPrimaryStage();}

    public AllDependenciesOfTargetDto getAllDependenciesOfTargetDtoFromSuper(String name,String selectedGraphName) {
        try {
            return sController.getAllDependenciesOfTargetDtoFromServer(name, selectedGraphName);
        }
        catch (IOException ignore){}
        return null;
    }

    public void openChat(){
        sController.openChat();
    }
}
