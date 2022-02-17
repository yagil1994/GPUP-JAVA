package mission;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;

import java.util.Set;

public class WorkerPendingMissionsTableViewDto {
    private SimpleStringProperty missionName;
    private SimpleStringProperty createdBy;
    private SimpleStringProperty taskType;
    private SimpleStringProperty leavesAmount;
    private SimpleStringProperty independentsAmount;
    private SimpleStringProperty middlesAmount;
    private SimpleStringProperty rootsAmount;
    private SimpleStringProperty totalTargetsAmount;
    private SimpleStringProperty priceForTarget;
    private SimpleStringProperty missionStatus;
    private SimpleStringProperty amountOfWorkersOnTask;
    private SimpleStringProperty isRegistered;
    private CheckBox selectedMissionCheckBox;

    private Set<String> processedTargets;
    public WorkerPendingMissionsTableViewDto(WorkerPendingMissionsTableViewDtoWithOutCheckBox dtoWithOutCheckBox){
        missionName = new SimpleStringProperty(dtoWithOutCheckBox.getMissionName());
        createdBy = new SimpleStringProperty(dtoWithOutCheckBox.getCreatedBy());
        taskType=new SimpleStringProperty(dtoWithOutCheckBox.getTaskType());
        leavesAmount = new SimpleStringProperty(dtoWithOutCheckBox.getLeavesAmount());
        independentsAmount = new SimpleStringProperty(dtoWithOutCheckBox.getIndependentsAmount());
        middlesAmount = new SimpleStringProperty(dtoWithOutCheckBox.getMiddlesAmount());
        rootsAmount = new SimpleStringProperty(dtoWithOutCheckBox.getRootsAmount());
        totalTargetsAmount=new SimpleStringProperty(dtoWithOutCheckBox.getTotalTargetsAmount());
        priceForTarget=new SimpleStringProperty(dtoWithOutCheckBox.getPriceForTarget());
        missionStatus=new SimpleStringProperty(dtoWithOutCheckBox.getMissionStatus());
        amountOfWorkersOnTask = new SimpleStringProperty(dtoWithOutCheckBox.getAmountOfWorkersOnTask());
        isRegistered=new SimpleStringProperty(dtoWithOutCheckBox.getIsRegistered());
        selectedMissionCheckBox= new CheckBox();
        processedTargets= dtoWithOutCheckBox.getProcessedTargets();
        if(missionStatus.getValue().equals("STOPPED")||missionStatus.getValue().equals("FINISHED")){
            selectedMissionCheckBox.setDisable(true);
        }
    }

    public Set<String> getProcessedTargets() {return processedTargets;}

    public CheckBox getSelectedMissionCheckBox() {
        return selectedMissionCheckBox;
    }

    public void setSelectedMissionCheckBox(CheckBox selectedMissionCheckBox) {
        this.selectedMissionCheckBox = selectedMissionCheckBox;
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

    public String getCreatedBy() {
        return createdBy.get();
    }

    public SimpleStringProperty createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    public String getTaskType() {
        return taskType.get();
    }

    public SimpleStringProperty taskTypeProperty() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType.set(taskType);
    }

    public String getLeavesAmount() {
        return leavesAmount.get();
    }

    public SimpleStringProperty leavesAmountProperty() {
        return leavesAmount;
    }

    public void setLeavesAmount(String leavesAmount) {
        this.leavesAmount.set(leavesAmount);
    }

    public String getIndependentsAmount() {
        return independentsAmount.get();
    }

    public SimpleStringProperty independentsAmountProperty() {
        return independentsAmount;
    }

    public void setIndependentsAmount(String independentsAmount) {
        this.independentsAmount.set(independentsAmount);
    }

    public String getMiddlesAmount() {
        return middlesAmount.get();
    }

    public SimpleStringProperty middlesAmountProperty() {
        return middlesAmount;
    }

    public void setMiddlesAmount(String middlesAmount) {
        this.middlesAmount.set(middlesAmount);
    }

    public String getRootsAmount() {
        return rootsAmount.get();
    }

    public SimpleStringProperty rootsAmountProperty() {
        return rootsAmount;
    }

    public void setRootsAmount(String rootsAmount) {
        this.rootsAmount.set(rootsAmount);
    }

    public String getTotalTargetsAmount() {
        return totalTargetsAmount.get();
    }

    public SimpleStringProperty totalTargetsAmountProperty() {
        return totalTargetsAmount;
    }

    public void setTotalTargetsAmount(String totalTargetsAmount) {
        this.totalTargetsAmount.set(totalTargetsAmount);
    }

    public String getPriceForTarget() {
        return priceForTarget.get();
    }

    public SimpleStringProperty priceForTargetProperty() {
        return priceForTarget;
    }

    public void setPriceForTarget(String priceForTarget) {
        this.priceForTarget.set(priceForTarget);
    }

    public String getMissionStatus() {
        return missionStatus.get();
    }

    public SimpleStringProperty missionStatusProperty() {
        return missionStatus;
    }

    public void setMissionStatus(String missionStatus) {
        this.missionStatus.set(missionStatus);
    }

    public String getAmountOfWorkersOnTask() {
        return amountOfWorkersOnTask.get();
    }

    public SimpleStringProperty amountOfWorkersOnTaskProperty() {
        return amountOfWorkersOnTask;
    }

    public void setAmountOfWorkersOnTask(String amountOfWorkersOnTask) {
        this.amountOfWorkersOnTask.set(amountOfWorkersOnTask);
    }

    public String getIsRegistered() {
        return isRegistered.get();
    }

    public SimpleStringProperty isRegisteredProperty() {
        return isRegistered;
    }

    public void setIsRegistered(String isRegistered) {
        this.isRegistered.set(isRegistered);
    }
}
