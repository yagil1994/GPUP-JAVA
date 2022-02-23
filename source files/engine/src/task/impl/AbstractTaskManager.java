package task.impl;

import data.structure.Graph;
import data.structure.Target;
import input.task.TaskInputDto;
import manager.Mediator;
import mission.Mission;
import output.UpdateTargetStatusDuringTaskDto;
import mission.TargetListViewDetails;
import task.misc.UiAdapterInterface;
import task.thread.PauseManager;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


public abstract class AbstractTaskManager implements Serializable {
    private boolean isFirstTime;
    private LinkedList<Target> sortedTargetList;
    protected Map<Target, RunningState> targetRunningStateMap;
    protected Map<Target, RunningResult> targetRunningResultMap;
    private Map<Target, String> targetsRunningTimeMap;
    protected Set<Target> runningTargetsThatAlreadyFinishedOrSkippedMap;

    private Map<Target, Instant> targetsWaitingTimeMap;
    private Map<Target, Instant> targetsInProcessTimeMap;

    protected int amountOfRunningTargets;

    protected Graph g;
    private String taskPath;

    public enum RunningResult {
        SUCCESS, WARNING, FAILURE, FROZEN, SKIPPED,DONT_CHECK;
    }

    public enum RunningState {
        FROZEN, SKIPPED, WAITING, IN_PROCESS, FINISHED;
    }

    public AbstractTaskManager(final Graph gr,AbstractTaskManager originalTaskManager, boolean copyMaps) {
        runningTargetsThatAlreadyFinishedOrSkippedMap= new HashSet<>();
        taskPath = null;
        g = gr;
        isFirstTime = copyMaps;
        sortedTargetList = g.bottomUpSort();
        targetsRunningTimeMap = new HashMap<>();
        targetsWaitingTimeMap=new HashMap<>();
        targetsInProcessTimeMap=new HashMap<>();
        targetRunningResultMap= new HashMap<>();
        targetRunningStateMap=new HashMap<>();
        if(copyMaps){//we do run again with increment
            for(Map.Entry<Target,RunningResult> e:originalTaskManager.targetRunningResultMap.entrySet()){
               Target t= g.findTargetByName(e.getKey().getName());
               targetRunningResultMap.put(t,e.getValue());
            }
            for(Map.Entry<Target,RunningState> e:originalTaskManager.targetRunningStateMap.entrySet()){
                Target t= g.findTargetByName(e.getKey().getName());
                targetRunningStateMap.put(t,e.getValue());
            }
            incrementor();
        }
        else{//we do run again from scratch
            amountOfRunningTargets=gr.getTargetsAmount();
            for (Target t : sortedTargetList) {
                targetRunningResultMap.put(t, RunningResult.FROZEN);
                if (t.getStatus().equals(Target.TargetStatus.LEAF.toString()) ||
                        t.getStatus().equals(Target.TargetStatus.INDEPENDENT.toString())) {
                    targetRunningStateMap.put(t, RunningState.WAITING);
                    targetsWaitingTimeMap.put(t, Instant.now());
                }
                else {
                    targetRunningStateMap.put(t, RunningState.FROZEN);
                }
            }
        }
    }
    public AbstractTaskManager(final Graph gr) {
        amountOfRunningTargets=gr.getTargetsAmount();
        runningTargetsThatAlreadyFinishedOrSkippedMap= new HashSet<>();
        taskPath = null;
        g = gr;
        isFirstTime = true;
        sortedTargetList = g.bottomUpSort();
        targetRunningResultMap = new HashMap<>();
        targetRunningStateMap = new HashMap<>();
        targetsRunningTimeMap = new HashMap<>();
        targetsWaitingTimeMap=new HashMap<>();
        targetsInProcessTimeMap=new HashMap<>();

        for (Target t : sortedTargetList) {
            targetRunningResultMap.put(t, RunningResult.FROZEN);
            if (t.getStatus().equals(Target.TargetStatus.LEAF.toString()) ||
                    t.getStatus().equals(Target.TargetStatus.INDEPENDENT.toString())) {
                targetRunningStateMap.put(t, RunningState.WAITING);
                targetsWaitingTimeMap.put(t, Instant.now());
            }
            else {
                targetRunningStateMap.put(t, RunningState.FROZEN);
            }
        }
    }
    synchronized public void updateRunningStateMap(String TargetName,RunningState newRunningState){
        Target t=g.findTargetByName(TargetName);
        targetRunningStateMap.put(t,newRunningState);
    }
    synchronized public void updateRunningResultToTarget(String TargetName,RunningResult newRunningResult){
        Target t=g.findTargetByName(TargetName);
        targetRunningResultMap.put(t,newRunningResult);
    }
    public Map<String,String> getUpdatedTargetsStatusMap(){
        Map<String,String>targetsStatusMap=new HashMap<>();
        for(Map.Entry<Target, RunningState> m:targetRunningStateMap.entrySet()){
            if(m.getValue().equals(RunningState.FINISHED)){
                targetsStatusMap.put(m.getKey().getName(), targetRunningResultMap.get(m.getKey()).toString());
            }else {
                targetsStatusMap.put(m.getKey().getName(), m.getValue().toString());
            }
        }
        return targetsStatusMap;
    }
    public synchronized TargetListViewDetails getTargetListViewDetails(String targetName){
       Target t=g.findTargetByName(targetName);
        String level,finishRunningResultType=null,targetRunningState,MsWaitingTimeInQueueAlready=null,MsInProcessTimeTookAlready=null;
        Set<String> serialSetsName=null,directDependsList=null,directAndIndirectDependsListThatFailed=null;

        level=t.getStatus();    //level

            targetRunningState = targetRunningStateMap.get(t).toString();
       switch (targetRunningState) {
           case "FINISHED": {
               finishRunningResultType = targetRunningResultMap.get(t).toString();
               break;
           }
           case "WAITING" : {
               Instant endTime = Instant.now();
               Duration res = Duration.between(targetsWaitingTimeMap.get(t), endTime);
               MsWaitingTimeInQueueAlready = getFullTimeFromDuration(res);
               break;
           }
           case "IN_PROCESS" : {
               Instant endTime = Instant.now();
               Duration res = Duration.between(targetsInProcessTimeMap.get(t), endTime);
               MsInProcessTimeTookAlready = getFullTimeFromDuration(res);
               break;
           }
           case "FROZEN" : {
               List<Target> directDependOnList = t.getOutTargetList();
               if (!directDependOnList.isEmpty()) {
                   directDependsList = new HashSet<>();
                   for (Target tar : directDependOnList) {
                       RunningState TarRunningStateMapStr = targetRunningStateMap.get(tar);
                       if (TarRunningStateMapStr.equals(RunningState.FROZEN) ||
                               TarRunningStateMapStr.equals(RunningState.WAITING) ||
                               TarRunningStateMapStr.equals(RunningState.IN_PROCESS)) {
                           directDependsList.add(tar.getName());
                       }
                   }
               }
               break;
           }
           case "SKIPPED" : {
               Set<Target> accessibleTargets = g.getAccessibleTargets(false, t);
               if (!accessibleTargets.isEmpty()) {
                   directAndIndirectDependsListThatFailed = new HashSet<>();
                   for (Target tar : accessibleTargets) {
                       RunningState TarRunningStateMapStr = targetRunningStateMap.get(tar);
                       if (TarRunningStateMapStr.equals(RunningState.FINISHED) &&
                               targetRunningResultMap.get(tar).equals(RunningResult.FAILURE)) {
                           directAndIndirectDependsListThatFailed.add(tar.getName());
                       }
                   }
               }
               break;
           }
       }
        return new TargetListViewDetails(targetName,level,
                 directDependsList, MsWaitingTimeInQueueAlready,
                 directAndIndirectDependsListThatFailed,
                 MsInProcessTimeTookAlready, finishRunningResultType, targetRunningState);
    }
    synchronized public boolean checkIfAllTargetsSucceededMission(){
        for(Map.Entry<Target,RunningResult> entry:targetRunningResultMap.entrySet()){
            if(!(entry.getValue().equals(RunningResult.DONT_CHECK)||
               entry.getValue().equals(RunningResult.SUCCESS)||
               entry.getValue().equals(RunningResult.WARNING)))
            {
                return false;
            }
        }
        return true;
    }
    synchronized public void updateThatTargetRunningStateIsFinishedFromPostman(String runningResult,String targetName) {
        Target t = g.findTargetByName(targetName);
        RunningResult targetRunningResult = RunningResult.SUCCESS;
        if(runningResult.equals(RunningResult.FAILURE.toString())){
            targetRunningResult=RunningResult.FAILURE;
        }
        else if(runningResult.equals(RunningResult.WARNING.toString())){
            targetRunningResult=RunningResult.WARNING;
        }
        targetRunningResultMap.put(t, targetRunningResult);
        targetRunningStateMap.put(t, RunningState.FINISHED);
        if (!runningTargetsThatAlreadyFinishedOrSkippedMap.contains(t)) {
            runningTargetsThatAlreadyFinishedOrSkippedMap.add(t);
        }
        setFirstTime(false);
    }
    synchronized public void updateThatTargetRunningStateIsFinished(UpdateTargetStatusDuringTaskDto dto, FileWriter targetFile) {
        Target t = g.findTargetByName(dto.getTargetName());
        RunningResult targetRunningResult = RunningResult.SUCCESS;
        if(dto.getNewStatusOnTask().equals(RunningResult.FAILURE.toString())){
            targetRunningResult=RunningResult.FAILURE;
        }
        else if(dto.getNewStatusOnTask().equals(RunningResult.WARNING.toString())){
            targetRunningResult=RunningResult.WARNING;
        }
        targetRunningResultMap.put(t, targetRunningResult);
        targetRunningStateMap.put(t, RunningState.FINISHED);
        if (!runningTargetsThatAlreadyFinishedOrSkippedMap.contains(t)) {
            runningTargetsThatAlreadyFinishedOrSkippedMap.add(t);
        }
        targetsRunningTimeMap.put(t, dto.getTaskTime());

        String taskResStr = "The task on target: " + t.getName() + " has been finished with " + targetRunningResultMap.get(t) + "\n\n";
        try {
            targetFile.write(taskResStr);
          String taskType= dto.getTaskType();
            if (taskType.equals("Compilation")) {
                String compilerWorkingTimeStr;
                compilerWorkingTimeStr = targetsRunningTimeMap.get(t);
                String[] fullTime = compilerWorkingTimeStr.split(":");
                String compilingTimeInMs = fullTime[fullTime.length - 1];
                String compilerWorkingTime = "The compiler working time in milliseconds was: " + compilingTimeInMs;
                targetFile.write(compilerWorkingTime);
            }
              if (targetRunningResultMap.get(t).equals(RunningResult.FAILURE)) { //if U has failed
                  targetFailureCase(t, targetFile);
              } else { //else- U has succeeded or succeeded with warning
                  targetNotFailedCase(t, targetFile);
                }
            targetFile.write("\n");
        }
        catch (IOException ignore) {} finally {
            if (targetFile != null) {
                try {
                    targetFile.close();
                } catch (IOException ignore) {}
            }
        }
        setFirstTime(false);
    }
    synchronized public void updateThatTargetIsInProcessAndUpdateTheTimeMap(String targetName){
        Target t= g.findTargetByName(targetName);
        targetRunningStateMap.put(t,RunningState.IN_PROCESS);
        targetsInProcessTimeMap.put(t, Instant.now());
    }
    public String createTaskFolderAndGetPath(Mediator.TaskType type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        dateStr = dateStr.replaceAll(":", ".");

        String typeNameCapitalOnlyFirstLetter = type.toString();
        typeNameCapitalOnlyFirstLetter = typeNameCapitalOnlyFirstLetter.charAt(0) + typeNameCapitalOnlyFirstLetter.substring(1).toLowerCase();
        taskPath = g.getWorkingDirectory() + "\\" + typeNameCapitalOnlyFirstLetter + " " + dateStr;
        File currentTaskFolder = null;
        try {
            currentTaskFolder = new File(taskPath);
        } catch (Exception ignore) {
        }

        if (currentTaskFolder != null) {
            boolean isFolderCreated = currentTaskFolder.mkdir();
            if (!isFolderCreated)
                throw new SecurityException("The computer had a problem with creating a folder in the current path:\n");
        }
        return taskPath;
    }
    protected void setFirstTime(boolean input) {
        isFirstTime = input;
    }
    public void incrementor()  {
        for (Target t : sortedTargetList) {
            if (targetRunningResultMap.get(t).equals(RunningResult.FAILURE) ||
                    targetRunningResultMap.get(t).equals(RunningResult.SKIPPED)) {
                if (t.getStatus().equals(Target.TargetStatus.LEAF.toString()) || t.getStatus().equals(Target.TargetStatus.INDEPENDENT.toString())) {
                    targetRunningStateMap.put(t, RunningState.WAITING);
                    targetsWaitingTimeMap.put(t, Instant.now());
                }
                else {
                    targetRunningStateMap.put(t, RunningState.FROZEN);
                }
                targetRunningResultMap.put(t, RunningResult.FROZEN);
            }
            else {//if we do two times increment maybe the previous "real state" is dont check as well-> other radical case to check
                targetRunningResultMap.put(t,RunningResult.DONT_CHECK);
            }
        }
        for (Target u : sortedTargetList) {
            if (!u.getInTargetList().isEmpty()) {
                for (Target v : u.getInTargetList()) {
                    boolean toAdd = true;
                    for (Target t : v.getOutTargetList()) {
                        if ((targetRunningResultMap.get(t).equals(RunningResult.FAILURE)) ||
                                (targetRunningResultMap.get(t).equals(RunningResult.SKIPPED)) ||
                                targetRunningResultMap.get(t).equals(RunningResult.FROZEN)) {
                            toAdd = false;
                            break;
                        }
                    }
                    if (toAdd &&(!targetRunningStateMap.get(v).equals(RunningState.FINISHED))) {
                        targetRunningStateMap.put(v, RunningState.WAITING);
                        targetsWaitingTimeMap.put(v, Instant.now());
                    }
                }
            }
        }
        amountOfRunningTargets=getAmountOfRunningTargets();
        runningTargetsThatAlreadyFinishedOrSkippedMap.clear();
    }

    final public Map<Target, String> getTargetsRunningTimeMap() {
        return targetsRunningTimeMap;
    }

    final public Map<Target, RunningResult> getTargetRunningResultMap() {
        return targetRunningResultMap;
    }

    final public String getTaskPath() {
        return taskPath;
    }
    abstract public void updateMembers(TaskInputDto taskInput);

    synchronized public Boolean isMissionDone() {
        for (Target t : sortedTargetList) {
            if (!(targetRunningStateMap.get(t).equals(AbstractTaskManager.RunningState.FINISHED) ||
                    targetRunningStateMap.get(t).equals(AbstractTaskManager.RunningState.SKIPPED)))
                return false;
        }
        return true;
    }
    public int getAmountOfRunningTargets(){
        int counter=0;
       for(Target t: sortedTargetList){
           if(!targetRunningResultMap.get(t).equals(RunningResult.DONT_CHECK)){
               counter++;
           }
       }
       return counter;
    }
     synchronized public Long getTargetsInProcessAmount(){
        return sortedTargetList.stream().filter(t -> targetRunningStateMap.get(t).equals(RunningState.IN_PROCESS)).count();
    }
    synchronized public Long getWaitingTargetsAmount(){
        return sortedTargetList.stream().filter(t -> targetRunningStateMap.get(t).equals(RunningState.WAITING)).count();
    }

    synchronized public Set<String> getProcessedTargets(){
        Set<String> processedTargets=new HashSet<>();
        for(Target t:runningTargetsThatAlreadyFinishedOrSkippedMap){
            processedTargets.add(t.getName());
        }
        return processedTargets;
    }

   synchronized public Map<String,Mission.TARGET_AVAILABILITY> getWaitingTargetsNamesSetDuringTask(){
        Map<String, Mission.TARGET_AVAILABILITY> waitingTargetsNameMap=new HashMap<>();
        for(Target t:sortedTargetList){
            if(targetRunningStateMap.get(t).equals(RunningState.WAITING)  &&
               !targetRunningResultMap.get(t).equals(RunningResult.DONT_CHECK))
            {
                waitingTargetsNameMap.put(t.getName(), Mission.TARGET_AVAILABILITY.AVAILABLE);
            }
        }
        return waitingTargetsNameMap;
    }
    final public String getFullTimeFromDuration(Duration res) {
        long hours = res.toHours();
        res = res.minusHours(hours);
        long minutes = res.toMinutes();
        res = res.minusMinutes(minutes);
        long seconds = (res.toMinutes() / 60);
        res = res.minusSeconds(seconds);
        long millis = res.toMillis();
        return hours + ":" + minutes + ":" + seconds + ":" + millis+" ";
    }

    private void targetFailureCase(Target u, FileWriter targetFile) throws IOException {
        if (!u.getInTargetList().isEmpty()) {
            String intro = "The targets that depend on " + u.getName() + " directly and will be skipped are: ";
            targetFile.write(intro);
            for (Target x : u.getInTargetList()) {
                String xName = x.getName() + " ";
                targetFile.write(xName);
            }
            targetFile.write("\n\n");

            Set<Target> accessibleTargets = g.getAccessibleTargets(true, u);
            String introAccess = "All the targets that depend on " + u.getName() + " indirectly and directly and will be skipped are: ";
            targetFile.write(introAccess);

            for (Target x : accessibleTargets) {
                synchronized (this) {
                    targetRunningStateMap.put(x, RunningState.SKIPPED);
                    targetRunningResultMap.put(x, RunningResult.SKIPPED);
                    if(!runningTargetsThatAlreadyFinishedOrSkippedMap.contains(x)){
                        runningTargetsThatAlreadyFinishedOrSkippedMap.add(x);
                    }
                }
                String xName = x.getName() + " ";
                targetFile.write(xName);
            }
            targetFile.write("\n\n");
        } else {
            String notAvailableTargets = "There aren't targets that will not be available at all to work on because " + u.getName() +
                    " has failed \n\n";
            targetFile.write(notAvailableTargets);
        }
    }

    protected String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    synchronized private void targetNotFailedCase(Target u,
       FileWriter targetFile) throws IOException {

        //todo  make some order on: synchronized
        String notAvailableTargets = "There aren't new targets that will be available to work on \n\n";
        if (u.getInTargetList().isEmpty()) {
            targetFile.write(notAvailableTargets);
        }
        else {// u has at least one target that has been affected
            String affectedInTargets = "All the targets that depend on " + u.getName() + " directly and will be available to work on are: ";
            targetFile.write(affectedInTargets);
            Set<Target> newWaitingSet = new HashSet<>();
            for (Target v : u.getInTargetList()) {
                boolean toAdd = true;
                for (Target t : v.getOutTargetList()) {
                    if ((targetRunningResultMap.get(t).equals(RunningResult.FAILURE)) ||
                            (targetRunningResultMap.get(t).equals(RunningResult.SKIPPED)) ||
                            targetRunningResultMap.get(t).equals(RunningResult.FROZEN) ||
                            !targetRunningStateMap.get(t).equals(RunningState.FINISHED)) {
                        toAdd = false;
                        break;
                    }
                }
                if (toAdd && targetRunningStateMap.get(v).equals(RunningState.FROZEN)) {
                    targetRunningStateMap.put(v, RunningState.WAITING);
                    targetsWaitingTimeMap.put(v, Instant.now());
                    newWaitingSet.add(v);
                    String vName = v.getName() + " ";
                    targetFile.write(vName);
                    targetsWaitingTimeMap.put(v, Instant.now());
                }
            }
            if (newWaitingSet.isEmpty()) {
                targetFile.write(notAvailableTargets);
            } else {
                targetFile.write("\n\n");
            }
        }
    }
}


