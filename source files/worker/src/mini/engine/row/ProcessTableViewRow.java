package mini.engine.row;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class ProcessTableViewRow {
    private RadioButton selectedMissionRadioButton;
    private SimpleStringProperty missionName;
    private SimpleStringProperty workersOnMission;
    private SimpleStringProperty progress;
    private SimpleStringProperty amountOfProcessedTargetsFrom;
    private SimpleStringProperty credits;


    public ProcessTableViewRow(String missionNameIn, String workersOnMissionIn, String progressIn,
                        String amountOfProcessedTargetsFromIn, String creditsIn,ToggleGroup missionSelect) {
        selectedMissionRadioButton =new RadioButton();
        missionName = new SimpleStringProperty(missionNameIn);
        workersOnMission = new SimpleStringProperty(workersOnMissionIn);
        progress = new SimpleStringProperty(progressIn);
        amountOfProcessedTargetsFrom = new SimpleStringProperty(amountOfProcessedTargetsFromIn);
        credits = new SimpleStringProperty(creditsIn);
        selectedMissionRadioButton.setToggleGroup(missionSelect);
    }

    public RadioButton getSelectedMissionRadioButton() {
        return selectedMissionRadioButton;
    }

    public void setSelectedMissionRadioButton(RadioButton selectedMissionRadioButton) {
        this.selectedMissionRadioButton = selectedMissionRadioButton;
    }

    public String getMissionName() {
        return missionName.get();
    }

    public SimpleStringProperty missionNameProperty() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName.set(missionName);
    }

    public String getWorkersOnMission() {
        return workersOnMission.get();
    }

    public SimpleStringProperty workersOnMissionProperty() {
        return workersOnMission;
    }

    public void setWorkersOnMission(String workersOnMission) {
        this.workersOnMission.set(workersOnMission);
    }

    public String getProgress() {
        return progress.get();
    }

    public SimpleStringProperty progressProperty() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress.set(progress);
    }

    public String getAmountOfProcessedTargetsFrom() {
        return amountOfProcessedTargetsFrom.get();
    }

    public SimpleStringProperty amountOfProcessedTargetsFromProperty() {
        return amountOfProcessedTargetsFrom;
    }

    public void setAmountOfProcessedTargetsFrom(String amountOfProcessedTargetsFrom) {
        this.amountOfProcessedTargetsFrom.set(amountOfProcessedTargetsFrom);
    }

    public String getCredits() {
        return credits.get();
    }

    public SimpleStringProperty creditsProperty() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits.set(credits);
    }

}