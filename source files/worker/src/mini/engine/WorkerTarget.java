package mini.engine;

import mini.engine.row.GeneralInfoTableviewRow;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class WorkerTarget {
    private final String missionName;
    private final String taskType;
    private final String targetName;
    private String status;
    private String price;   //(only if finished)

    private final List<String> logs;// (only if running now)

    private Integer priceForTheTarget;  //the price when the target will be finish

    public WorkerTarget(String missionNameIn, String taskTypeIn, String targetNameIn){
        missionName = missionNameIn;
        taskType = taskTypeIn;
        targetName = targetNameIn;
        logs=new LinkedList<>();
    }

    public GeneralInfoTableviewRow createGeneralInfoTableviewRow(){
        return new GeneralInfoTableviewRow(missionName,taskType,targetName,status,price);
    }

    public Consumer<String> getWriterToLogs(){
        return new Consumer<String>() {
            @Override
            public void accept(String s) {
                logs.add(s);
            }
        };
    }

    public List<String> getLogs(){
        if(status.equals("IN_PROCESS")){
            return logs;
        }
        else {
            List<String>tmp=new LinkedList<>();
            tmp.add("");
            return tmp;
        }
    }

    public void setPriceForTheTarget(Integer priceForTheTarget) {
        this.priceForTheTarget = priceForTheTarget;
    }

    public Integer getPriceForTheTarget() {
        return priceForTheTarget;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMissionName() {
        return missionName;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }
}
