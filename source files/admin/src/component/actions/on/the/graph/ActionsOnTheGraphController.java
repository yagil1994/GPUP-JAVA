package component.actions.on.the.graph;

import component.supercontroller.AdminSuperController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import output.AllDependenciesOfTargetDto;
import output.AllPathsOfTwoTargetsDto;
import output.CirclePathForTargetDto;
import output.FilteredAllDependenciesOfTargetDto;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ActionsOnTheGraphController {
    private AdminSuperController sController;

    @FXML private Button startActionButton;
    @FXML private  Button clearBoardButton;
    @FXML private ChoiceBox<String> firstTargetChoiceBox;
    @FXML private ChoiceBox<String> secondTargetChoiceBox;
    @FXML private ChoiceBox<String> dependencyTypeChoiceBox;

    @FXML private RadioButton isTargetInCircleRadio;
    @FXML private RadioButton checkTargetDependenciesRadio;
    @FXML private RadioButton findPathBetweenTwoTargetsRadio;

    @FXML private CheckBox rootCheckBoxDependencies;
    @FXML private CheckBox middleCheckBoxDependencies;
    @FXML private CheckBox leafCheckBoxDependencies;
    @FXML private CheckBox independentCheckBoxDependencies;

    @FXML private TextArea resultDisplayedInText;

    private final SimpleBooleanProperty isTargetInCircleReadyForStartProperty;
    private final SimpleBooleanProperty isCheckTargetDependenciesReadyForStartProperty;
    private final SimpleBooleanProperty isFindPathBetweenTwoTargetsReadyForStartProperty;
    private final SimpleBooleanProperty allDisabledProperty;

    private String selectedGraphName;

    public ActionsOnTheGraphController(){
        isTargetInCircleReadyForStartProperty= new SimpleBooleanProperty(false);
        isCheckTargetDependenciesReadyForStartProperty= new SimpleBooleanProperty(false);
        isFindPathBetweenTwoTargetsReadyForStartProperty= new SimpleBooleanProperty(false);
        allDisabledProperty = new SimpleBooleanProperty(true);
    }

    @FXML public void initialize(){
        rootCheckBoxDependencies.disableProperty().bind(checkTargetDependenciesRadio.selectedProperty().not());
        middleCheckBoxDependencies.disableProperty().bind(checkTargetDependenciesRadio.selectedProperty().not());
        leafCheckBoxDependencies.disableProperty().bind(checkTargetDependenciesRadio.selectedProperty().not());
        independentCheckBoxDependencies.disableProperty().bind(checkTargetDependenciesRadio.selectedProperty().not());

        checkTargetDependenciesRadio.selectedProperty().addListener((observable, oldValue, newValue) ->
                {
                    if (!checkTargetDependenciesRadio.isSelected()) {
                        rootCheckBoxDependencies.selectedProperty().setValue(true);
                        middleCheckBoxDependencies.selectedProperty().setValue(true);
                        leafCheckBoxDependencies.selectedProperty().setValue(true);
                        independentCheckBoxDependencies.selectedProperty().setValue(true);
                    }
                });

         String textToShow="If you wish to have specific types of levels,tick them!\n"
        +"In case the levels are available,(in advance to the target and dependency you've chosen),they will be shown.\n"+
        "Pay attention! if you don't tick anything or you tick all the checkboxes:\n"+
        "The system will show all the available levels.\n";
         Tooltip checkTargetDependenciesRadioTooltip=new Tooltip();
        checkTargetDependenciesRadioTooltip.setText(textToShow);
        try {
            Field fieldBehavior = checkTargetDependenciesRadioTooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(checkTargetDependenciesRadioTooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(500)));
        } catch (Exception e) {}
        checkTargetDependenciesRadio.setTooltip(checkTargetDependenciesRadioTooltip);

        allDisabledProperty.bind(isTargetInCircleRadio.selectedProperty().not()
                .and(checkTargetDependenciesRadio.selectedProperty().not()
                        .and(findPathBetweenTwoTargetsRadio.selectedProperty().not())));

        firstTargetChoiceBox.disableProperty().bind(
                allDisabledProperty
        );
        secondTargetChoiceBox.disableProperty().bind(
                allDisabledProperty.or(findPathBetweenTwoTargetsRadio.selectedProperty().not())
        );
        dependencyTypeChoiceBox.disableProperty().bind(
                allDisabledProperty.or(isTargetInCircleRadio.selectedProperty())
        );

        isTargetInCircleReadyForStartProperty.bind(
                firstTargetChoiceBox.getSelectionModel().selectedItemProperty().isNull().not()
                        .and(isTargetInCircleRadio.selectedProperty())
        );
        isCheckTargetDependenciesReadyForStartProperty.bind(
                firstTargetChoiceBox.getSelectionModel().selectedItemProperty().isNull().not()
                        .and(dependencyTypeChoiceBox.getSelectionModel().selectedItemProperty().isNull().not())
                                .and(checkTargetDependenciesRadio.selectedProperty())
        );
        isFindPathBetweenTwoTargetsReadyForStartProperty.bind(
                firstTargetChoiceBox.getSelectionModel().selectedItemProperty().isNull().not()
                        .and(dependencyTypeChoiceBox.getSelectionModel().selectedItemProperty().isNull().not())
                                .and(secondTargetChoiceBox.getSelectionModel().selectedItemProperty().isNull().not())
                                        .and(findPathBetweenTwoTargetsRadio.selectedProperty())
        );
        startActionButton.disableProperty().bind(
                isTargetInCircleReadyForStartProperty.not()
                        .and(isCheckTargetDependenciesReadyForStartProperty.not()
                        .and(isFindPathBetweenTwoTargetsReadyForStartProperty.not()))
        );
    }

    public void setAdminSuperController(AdminSuperController superCont){
      sController=superCont;
    }

    public void initActionsOnGraphWhenGraphSelected(List<String> lst, String selectedGraphNameIn){
        selectedGraphName=selectedGraphNameIn;
        ObservableList<String> targetNames= FXCollections.observableArrayList(lst);
        ObservableList<String> dependencies= FXCollections.observableArrayList("Depend on","Required for");
        firstTargetChoiceBox.setItems(targetNames);
        secondTargetChoiceBox.setItems(targetNames);
        dependencyTypeChoiceBox.setItems(dependencies);
    }

    public void clearTextArea(){
        clearBoard();
    }

    @FXML private void clearBoard() {
       resultDisplayedInText.clear();
    }

    @FXML private void onActionStartButton(){
        try {
            if (findPathBetweenTwoTargetsRadio.isSelected()) {
                activateFindAllPathsOfTwoTargets();
            } else if (isTargetInCircleRadio.isSelected()) {
                activateCheckIfTargetInCircle();
            } else if (checkTargetDependenciesRadio.isSelected()) {
                activateCheckTargetDependencies();
            }
        }
        catch (IOException ignore){}
    }

    private void activateCheckIfTargetInCircle() throws IOException {
        String targetName =firstTargetChoiceBox.getValue();
        CirclePathForTargetDto circlePath= sController.checkIfTargetIsInCircle(targetName, selectedGraphName);

        if (circlePath.getResult().length==0) {
            resultDisplayedInText.appendText("There isn't any circle that includes '" + targetName + "'\n");
            return;
        }
        String[] circle = circlePath.getResult();
        int len = circle.length;
        for (int i = 0; i < len - 1; i++) {
            System.out.format("%s-> ", circle[i]);
            resultDisplayedInText.appendText(circle[i]+"-> ");
        }
        resultDisplayedInText.appendText(circle[len - 1]);
        resultDisplayedInText.appendText("\n");
    }

    private void activateCheckTargetDependencies() throws IOException {
        boolean checkAllLevels, rootTicked, middleTicked, leafTicked, independentTicked,allAreTicked,nothingIsTicked;
        rootTicked = rootCheckBoxDependencies.isSelected();
        middleTicked = middleCheckBoxDependencies.isSelected();
        leafTicked = leafCheckBoxDependencies.isSelected();
        independentTicked = independentCheckBoxDependencies.isSelected();
        allAreTicked=rootTicked&&middleTicked&&leafTicked&&independentTicked;
        nothingIsTicked=(!rootTicked)&&(!middleTicked)&&(!leafTicked)&&(!independentTicked);
        checkAllLevels = allAreTicked || nothingIsTicked;

        String name = firstTargetChoiceBox.getValue();
        String isDependStr = dependencyTypeChoiceBox.getValue();
        boolean isDepend = (isDependStr.equals("Depend on"));
        AllDependenciesOfTargetDto data = sController.getAllDependenciesOfTargetDtoFromServer(name,selectedGraphName);
        FilteredAllDependenciesOfTargetDto filteredData=new FilteredAllDependenciesOfTargetDto(data,checkAllLevels,rootTicked,middleTicked,leafTicked,independentTicked);
        Map<String,String> mapToPrint;
        if (isDepend) {
            mapToPrint = filteredData.getTotalFilteredDependsOn();
        } else {
            mapToPrint = filteredData.getTotalFilteredRequiredFor();
        }
        if (mapToPrint.isEmpty()) {
            if (isDepend) {
                resultDisplayedInText.appendText("the target: " + name + " does not have any targets it depends on them(in accordance to what is ticked as well)\n");
            } else {
                resultDisplayedInText.appendText("the target: " + name + " does not have any targets it required for them(in accordance to what is ticked as well)\n");
            }
        } else {
            if (isDepend) {
                resultDisplayedInText.appendText("The targets that " + name + " depend on them are: ");
            } else {
                resultDisplayedInText.appendText("The targets that " + name + " required for them are: ");
            }
            resultDisplayedInText.appendText(mapToPrint.toString()+'\n');
        }
    }

    private void activateFindAllPathsOfTwoTargets() throws IOException {
        String name1 = firstTargetChoiceBox.getValue();
        String name2 = secondTargetChoiceBox.getValue();
        String isDependStr = dependencyTypeChoiceBox.getValue();
        boolean isDepend = (isDependStr.equals("Depend on"));
       AllPathsOfTwoTargetsDto allPaths = sController.findAllPathsOfTwoTargetsFromEngine(name1, name2, isDepend,selectedGraphName);
        final List<List<String>> pathsList =allPaths.getAllPaths();
        printPaths(pathsList,isDepend);
    }

    private void printPaths(List<List<String>> pathsList,boolean isSwitched){
        if (pathsList.isEmpty()) {
            resultDisplayedInText.appendText("There isn't any path between the two of the targets\n");
        }
        else if (!isSwitched) {
            for (List<String> currList : pathsList) {
                for (String s : currList) {
                    if (s.equals(currList.get(currList.size() - 1))) {//if we got the "tail"
                        resultDisplayedInText.appendText(s);
                    }
                    else {//if we don't hold the "tail"
                        resultDisplayedInText.appendText(s+"->");
                    }
                }
                resultDisplayedInText.appendText("\n");
            }
        } else{//if it's switched
            String helper = "";
            for (List<String> currList : pathsList) {
                for (String s : currList) {
                    if (s.equals(currList.get(0))) {//if we got the "head"
                        helper = s;
                    }
                    else {//if we don't hold the "head"
                        helper = s + "-> " + helper;
                    }
                }
                resultDisplayedInText.appendText(helper + '\n');
            }
        }
            resultDisplayedInText.appendText("\n");
    }

}




