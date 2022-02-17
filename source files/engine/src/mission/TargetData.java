package mission;

import output.UpdateTargetStatusDuringTaskDto;

public class TargetData {
    private final String targetName;
    private String prevStatusOnTask;
    private String newStatusOnTask;

    public TargetData(String targetNameIn,String newStatusOnTaskIn){
        targetName=targetNameIn;
        prevStatusOnTask=null;
        newStatusOnTask=newStatusOnTaskIn;
    }
    public UpdateTargetStatusDuringTaskDto getUpdateTargetStatusDuringTaskDto(){
        return new UpdateTargetStatusDuringTaskDto(targetName,prevStatusOnTask,newStatusOnTask,null,null,null,null);
    }

    public void updateNewStatusOnTask(String newStatusOnTaskIn) {
        prevStatusOnTask=newStatusOnTask;
        newStatusOnTask = newStatusOnTaskIn;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getPrevStatusOnTask() {
        return prevStatusOnTask;
    }

    public String getNewStatusOnTask() {
        return newStatusOnTask;
    }

}
