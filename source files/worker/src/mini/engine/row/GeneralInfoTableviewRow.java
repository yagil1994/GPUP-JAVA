package mini.engine.row;

import javafx.beans.property.SimpleStringProperty;

public class GeneralInfoTableviewRow {
    private SimpleStringProperty missionName;
    private SimpleStringProperty taskType;
    private SimpleStringProperty targetName;
    private SimpleStringProperty status;
    private SimpleStringProperty price;


    public GeneralInfoTableviewRow(String missionNameIn, String taskTypeIn, String targetNameIn,
                                   String statusIn, String priceIn){
        missionName = new SimpleStringProperty(missionNameIn);
        taskType = new SimpleStringProperty(taskTypeIn);
        targetName = new SimpleStringProperty(targetNameIn);
        status = new SimpleStringProperty(statusIn);
        price = new SimpleStringProperty(priceIn);
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

    public String getTaskType() {
        return taskType.get();
    }

    public SimpleStringProperty taskTypeProperty() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType.set(taskType);
    }

    public String getTargetName() {
        return targetName.get();
    }

    public SimpleStringProperty targetNameProperty() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName.set(targetName);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getPrice() {
        return price.get();
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }
}
