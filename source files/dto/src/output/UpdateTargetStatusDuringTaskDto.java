package output;

public class UpdateTargetStatusDuringTaskDto {
    //this class has two different usages:
    // #1 it helps the Worker send information to the server when a worker finishes an execution
    // #2 it uses as a "row" for updating a list view (probably in the Admin's class)
    private String targetName,prevStatusOnTask, newStatusOnTask,taskTime,missionName,taskType,fileName;
    public UpdateTargetStatusDuringTaskDto(String targetNameIn,String prevStatusOnTaskIn,
                                           String newStatusOnTaskIn,String taskTimeIn,String missionNameIn
                                          ,String taskTypeIn,String fileNameIn){
        targetName=targetNameIn;
        prevStatusOnTask=prevStatusOnTaskIn;
        newStatusOnTask=newStatusOnTaskIn;
        taskTime=taskTimeIn;
        missionName=missionNameIn;
        taskType=taskTypeIn;
        fileName=fileNameIn;
    }

    public String getFileName() {return fileName;}
    public String getTaskType() {return taskType;}
    //public FileWriter getTargetFile() {return targetFile;}
    public String getMissionName() {return missionName;}
    public String getTargetName() {
        return targetName;
    }
    public String getPrevStatusOnTask() {
        return prevStatusOnTask;
    }
    public String getTaskTime() {return taskTime;}
    public String getNewStatusOnTask() {
        return newStatusOnTask;
    }
}
