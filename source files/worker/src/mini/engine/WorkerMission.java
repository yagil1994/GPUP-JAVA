package mini.engine;

import mission.MissionNameAndDto;
import mission.WorkerPendingMissionsTableViewDto;

import java.util.HashSet;
import java.util.Set;

public class WorkerMission {

    private String missionName;
    private String createdBy;
    private String taskType;
    private String leavesAmount;
    private String independentsAmount;
    private String middlesAmount;
    private String rootsAmount;
    private String totalTargetsAmount;
    private String priceForTarget;

    private String missionStatus;
    private String amountOfWorkersOnTask;
    private Set<String> processedTargets;

    private Boolean pause;

    private MissionNameAndDto missionDto;

//    public WorkerMission(String missionNameIn, String createdByIn, String taskTypeIn, String leavesAmountIn,
//                         String independentsAmountIn,String middlesAmountIn, String  rootsAmountIn,
//                         String totalTargetsAmountIn,String  priceForTargetIn){
//
//        missionName = missionNameIn;
//        createdBy = createdByIn;
//        taskType = taskTypeIn;
//        leavesAmount = leavesAmountIn;
//        independentsAmount = independentsAmountIn;
//        middlesAmount = middlesAmountIn;
//        rootsAmount = rootsAmountIn;
//        totalTargetsAmount = totalTargetsAmountIn;
//        priceForTarget = priceForTargetIn;
//        missionStatus = null;
//        amountOfWorkersOnTask = "0";
//        pause=false;
//        processedTargets=new HashSet<>();
//
//    }

//    public WorkerMission(String missionNameIn){
//        missionName = missionNameIn;
//        amountOfWorkersOnTask = "0";
//        pause=false;
//        processedTargets=new HashSet<>();
//
//        missionStatus="";
//        createdBy = "";
//        taskType = "";
//        leavesAmount = "0";
//        independentsAmount = "0";
//        middlesAmount = "0";
//        rootsAmount = "0";
//        totalTargetsAmount = "0";
//        priceForTarget = "0";
//    }

    public WorkerMission( WorkerPendingMissionsTableViewDto row){
        missionName = row.getMissionName();
        amountOfWorkersOnTask = row.getAmountOfWorkersOnTask();
        pause=false;
        processedTargets=new HashSet<>();
        missionStatus=row.getMissionStatus();
        createdBy = row.getCreatedBy();
        taskType = row.getTaskType();
        leavesAmount = row.getLeavesAmount();
        independentsAmount = row.getIndependentsAmount();
        middlesAmount = row.getMiddlesAmount();
        rootsAmount = row.getRootsAmount();
        totalTargetsAmount = row.getTotalTargetsAmount();
        priceForTarget = row.getPriceForTarget();
    }

    public void updateMembers(String createdByIn, String taskTypeIn, String leavesAmountIn,
                              String independentsAmountIn,String middlesAmountIn, String  rootsAmountIn,
                              String totalTargetsAmountIn,String  priceForTargetIn){
        createdBy = createdByIn;
        taskType = taskTypeIn;
        leavesAmount = leavesAmountIn;
        independentsAmount = independentsAmountIn;
        middlesAmount = middlesAmountIn;
        rootsAmount = rootsAmountIn;
        totalTargetsAmount = totalTargetsAmountIn;
        priceForTarget = priceForTargetIn;
    }

    public void updateDto(MissionNameAndDto dto){
        missionDto=dto;
    }


    public MissionNameAndDto getMissionDto() {return missionDto;}

    public Set<String> getProcessedTargets() {return processedTargets;}

    public Integer getProcessedTargetsAmount() {return processedTargets.size();}

    public Double getProgressPresent(){
        int processedTargetsAmount=processedTargets.size();
        int totalTargetsAmountInt= Integer.parseInt(totalTargetsAmount);
        double res=0.0;
        if(totalTargetsAmountInt!=0) {

            res =(((double)(processedTargetsAmount)) / totalTargetsAmountInt);
        }
        return res;
    }

    public String getProgressPresentStr(){return getProgressPresent().toString()+"%";}

    public void setProcessedTargets(Set<String> processedTargets) {this.processedTargets = processedTargets;}

    public void setMissionStatus(String missionStatus) {
        this.missionStatus = missionStatus;
    }

    public void setAmountOfWorkersOnTask(String amountOfWorkersOnTask) {
        this.amountOfWorkersOnTask = amountOfWorkersOnTask;
    }


    public Boolean isPause(){return pause;}
    public void setPause(){pause=true;}
    public void setResume(){pause=false;}

    public String getMissionName() {
        return missionName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getLeavesAmount() {
        return leavesAmount;
    }

    public String getIndependentsAmount() {
        return independentsAmount;
    }

    public String getMiddlesAmount() {
        return middlesAmount;
    }

    public String getRootsAmount() {
        return rootsAmount;
    }

    public String getTotalTargetsAmount() {
        return totalTargetsAmount;
    }

    public String getPriceForTarget() {
        return priceForTarget;
    }

    public String getMissionStatus() {
        return missionStatus;
    }

    public String getAmountOfWorkersOnTask() {
        return amountOfWorkersOnTask;
    }
}
