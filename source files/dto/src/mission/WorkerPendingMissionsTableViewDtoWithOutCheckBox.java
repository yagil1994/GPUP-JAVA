package mission;

import javafx.beans.property.SimpleStringProperty;

import java.util.Set;

public class WorkerPendingMissionsTableViewDtoWithOutCheckBox {
    private final SimpleStringProperty missionName;
    private final SimpleStringProperty createdBy;
    private final SimpleStringProperty taskType;
    private final SimpleStringProperty leavesAmount;
    private final SimpleStringProperty independentsAmount;
    private final SimpleStringProperty middlesAmount;
    private final SimpleStringProperty rootsAmount;
    private final SimpleStringProperty totalTargetsAmount;
    private final SimpleStringProperty priceForTarget;
    private final SimpleStringProperty missionStatus;
    private final SimpleStringProperty amountOfWorkersOnTask;
    private final SimpleStringProperty isRegistered;
    private Set<String> processedTargets;
    public WorkerPendingMissionsTableViewDtoWithOutCheckBox(String missionNameIn, String createdByIn, String taskTypeIn,
                                                            String leavesAmountIn, String independentsAmountIn, String middlesAmountIn
          , String rootsAmountIn, String totalTargetsAmountIn, String priceForTargetIn, String missionStatusIn,
                                  String amountOfWorkersOnTaskIn, String isRegisteredIn, Set<String>processedTargetsIn){

        missionName = new SimpleStringProperty(missionNameIn);
        createdBy = new SimpleStringProperty(createdByIn);
        taskType=new SimpleStringProperty(taskTypeIn);
        leavesAmount = new SimpleStringProperty(leavesAmountIn);
        independentsAmount = new SimpleStringProperty(independentsAmountIn);
        middlesAmount = new SimpleStringProperty(middlesAmountIn);
        rootsAmount = new SimpleStringProperty(rootsAmountIn);
        totalTargetsAmount=new SimpleStringProperty(totalTargetsAmountIn);
        priceForTarget=new SimpleStringProperty(priceForTargetIn);
        missionStatus=new SimpleStringProperty(missionStatusIn);
        amountOfWorkersOnTask = new SimpleStringProperty(amountOfWorkersOnTaskIn);
        isRegistered=new SimpleStringProperty(isRegisteredIn);
        processedTargets=processedTargetsIn;
    }

    public Set<String> getProcessedTargets() {return processedTargets;}

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
