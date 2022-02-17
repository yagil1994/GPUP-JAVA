package mission;

import javafx.beans.property.SimpleStringProperty;

public class AdminPendingMissionsTableViewDtoWithoutRadioButton {
    private SimpleStringProperty missionName;
    private SimpleStringProperty graphName;
    private SimpleStringProperty createdBy;
    private SimpleStringProperty leavesAmount;
    private SimpleStringProperty independentsAmount;
    private SimpleStringProperty middlesAmount;
    private SimpleStringProperty rootsAmount;
    private SimpleStringProperty totalTaskPrice;
    private SimpleStringProperty amountOfWorkersOnTask;
    private SimpleStringProperty missionStatus;

    public AdminPendingMissionsTableViewDtoWithoutRadioButton(String missionNameIn, String  graphNameIn, String  createdByIn,
                                                              String leavesAmountIn, String independentsAmountIn, String middlesAmountIn, String rootsAmountIn,
                                                              String totalTaskPriceIn, String amountOfWorkersOnTaskIn, String missionStatusIn){
        missionName = new SimpleStringProperty(missionNameIn);
        graphName = new SimpleStringProperty(graphNameIn);
        createdBy = new SimpleStringProperty(createdByIn);
        leavesAmount = new SimpleStringProperty(leavesAmountIn);
        independentsAmount = new SimpleStringProperty(independentsAmountIn);
        middlesAmount = new SimpleStringProperty(middlesAmountIn);
        rootsAmount = new SimpleStringProperty(rootsAmountIn);
        totalTaskPrice = new SimpleStringProperty(totalTaskPriceIn);
        amountOfWorkersOnTask = new SimpleStringProperty(amountOfWorkersOnTaskIn);
        missionStatus = new SimpleStringProperty(missionStatusIn);

    }

    public String getMissionName() {
        return missionName.get();
    }

    public SimpleStringProperty missionNameProperty() {
        return missionName;
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

    public void setMissionName(String missionName) {
        this.missionName.set(missionName);
    }

    public String getGraphName() {
        return graphName.get();
    }

    public SimpleStringProperty graphNameProperty() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName.set(graphName);
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

    public String getTotalTaskPrice() {
        return totalTaskPrice.get();
    }

    public SimpleStringProperty totalTaskPriceProperty() {
        return totalTaskPrice;
    }

    public void setTotalTaskPrice(String totalTaskPrice) {
        this.totalTaskPrice.set(totalTaskPrice);
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

}
