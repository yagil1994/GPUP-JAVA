package mission;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class AdminPendingMissionsTableViewDto {
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
    private RadioButton selectedRadioButton;

    public AdminPendingMissionsTableViewDto(AdminPendingMissionsTableViewDtoWithoutRadioButton dto, ToggleGroup toggleGroup){
        missionName = new SimpleStringProperty(dto.getMissionName());
        graphName = new SimpleStringProperty(dto.getGraphName());
        createdBy = new SimpleStringProperty(dto.getCreatedBy());
        leavesAmount = new SimpleStringProperty(dto.getLeavesAmount());
        independentsAmount = new SimpleStringProperty(dto.getIndependentsAmount());
        middlesAmount = new SimpleStringProperty(dto.getMiddlesAmount());
        rootsAmount = new SimpleStringProperty(dto.getRootsAmount());
        totalTaskPrice = new SimpleStringProperty(dto.getTotalTaskPrice());
        amountOfWorkersOnTask = new SimpleStringProperty(dto.getAmountOfWorkersOnTask());
        missionStatus = new SimpleStringProperty(dto.getMissionStatus());
        selectedRadioButton=new RadioButton();
        selectedRadioButton.setToggleGroup(toggleGroup);
    }

    public String getMissionStatus() {return missionStatus.get();}

    public SimpleStringProperty missionStatusProperty() {return missionStatus;}

    public void setMissionStatus(String missionStatus) {
        this.missionStatus.set(missionStatus);
    }

    public RadioButton getSelectedRadioButton() {
        return selectedRadioButton;
    }

    public void setSelectedRadioButton(RadioButton selectedRadioButton) {
        this.selectedRadioButton = selectedRadioButton;
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
