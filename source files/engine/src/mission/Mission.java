package mission;

import data.structure.Graph;
import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import manager.Mediator;
import output.UpdateTargetStatusDuringTaskDto;
import task.impl.AbstractTaskManager;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mission {
    private TaskInputDto parametersFromAdminDto;
    private AdminTotalInfoForMissionWithOutParams dtoFromTheAdmin;
    private String missionName;
    private final String graphName;
    private final String createdBy;
    private String leavesAmount;
    private String independentsAmount;
    private String middlesAmount;
    private String rootsAmount;
    private String totalTaskPrice;
    private final String taskType;
    private String totalTargetsAmount;
    private String priceForTarget;
    private MISSION_STATUS missionStatus;
    private Set<String> registeredWorkersNames;
    private String[] targetsToRunInTask;
    private Mediator med;
    private AbstractTaskManager task;
    private Graph graphForTaskOnly;
    private Map<String,TARGET_AVAILABILITY> waitingTargetsNamesMap;
    private Map<String, TargetData> targetDataMap;
    private final String missionFolder;
    private Instant startTime;
    private LinkedList<String>logs;
    private List<String> taskReportStringList;
    private Integer generationCounter;

    public enum MISSION_STATUS{
       STOPPED,PLAYING,PAUSED,FINISHED,NEW_MISSION,RESUMED;
    }
    public enum TARGET_AVAILABILITY {
        AVAILABLE,TAKEN;
    }
    public Mission(Mission oldMission,String newMissionName){
        missionName=newMissionName;
        registeredWorkersNames=new HashSet<>();
        dtoFromTheAdmin=oldMission.getCopyOfDtoFromTheAdmin();
        dtoFromTheAdmin.setMissionName(oldMission.missionName);
        taskType=dtoFromTheAdmin.getTaskType();
        targetsToRunInTask= oldMission.getCopyOfParametersFromAdminDto().getTargetsToRun();
        totalTargetsAmount=oldMission.getTotalTargetsAmount();
        priceForTarget=oldMission.getPriceForTarget();
        graphName=oldMission.getGraphName();
        createdBy=oldMission.dtoFromTheAdmin.getCreatedBy();
        leavesAmount=oldMission.dtoFromTheAdmin.getLeavesAmount();
        independentsAmount=oldMission.dtoFromTheAdmin.getIndependentsAmount();
        middlesAmount=oldMission.dtoFromTheAdmin.getMiddlesAmount();
        rootsAmount=oldMission.dtoFromTheAdmin.getRootsAmount();
        totalTaskPrice=oldMission.dtoFromTheAdmin.getTotalTaskPrice();
        med=oldMission.getMed();
        parametersFromAdminDto=oldMission.getCopyOfParametersFromAdminDto();
        task=med.getTaskManager(missionName);
        graphForTaskOnly=med.getGraphForTaskOnly(missionName);
        waitingTargetsNamesMap=task.getWaitingTargetsNamesSetDuringTask();
        Mediator.TaskType type= Mediator.TaskType.SIMULATION;
        if(taskType.equals("Compilation")){
            type= Mediator.TaskType.COMPILATION;
        }
        missionFolder =task.createTaskFolderAndGetPath(type);
        missionStatus= Mission.MISSION_STATUS.NEW_MISSION;
        targetDataMap=new HashMap<>();
        logs=new LinkedList<>();
        taskReportStringList=new ArrayList<>();
        generationCounter=0;
    }
    public Mission(AdminTotalInfoForMissionWithOutParams dtoFromAdmin, TaskInputDto parametersFromAdminDtoIn,Mediator medIn){//todo we will need to use synchronize!!!!!!
        registeredWorkersNames=new HashSet<>();
        dtoFromTheAdmin=dtoFromAdmin;
        taskType=dtoFromAdmin.getTaskType();
        targetsToRunInTask=parametersFromAdminDtoIn.getTargetsToRun();
        totalTargetsAmount=dtoFromAdmin.getTotalTargetsAmount();
        priceForTarget=dtoFromAdmin.getPriceForTarget();
        missionName=dtoFromAdmin.getMissionName();
        graphName=dtoFromAdmin.getGraphName();
        createdBy=dtoFromAdmin.getCreatedBy();
        leavesAmount=dtoFromAdmin.getLeavesAmount();
        independentsAmount=dtoFromAdmin.getIndependentsAmount();
        middlesAmount=dtoFromAdmin.getMiddlesAmount();
        rootsAmount=dtoFromAdmin.getRootsAmount();
        totalTaskPrice=dtoFromAdmin.getTotalTaskPrice();
        med=medIn;
        parametersFromAdminDto=parametersFromAdminDtoIn;
        task=med.getTaskManager(missionName);
        graphForTaskOnly=med.getGraphForTaskOnly(missionName);
        waitingTargetsNamesMap=task.getWaitingTargetsNamesSetDuringTask();
        Mediator.TaskType type= Mediator.TaskType.SIMULATION;
        if(taskType.equals("Compilation")){
            type= Mediator.TaskType.COMPILATION;
        }
        missionFolder =task.createTaskFolderAndGetPath(type);//the mission folder
        missionStatus= Mission.MISSION_STATUS.NEW_MISSION;
        targetDataMap=new HashMap<>();
        logs=new LinkedList<>();
        taskReportStringList=new ArrayList<>();
        generationCounter=0;
    }
    public int getAmountOfRunningTargetsOnTask(){return task.getAmountOfRunningTargets();}
    public Boolean areAllTargetsSucceed(){return task.checkIfAllTargetsSucceededMission();}

    synchronized public Integer getGenerationCounter() {return generationCounter;}

    synchronized public Integer addAndGetGenerationCounter() {
        generationCounter++;
        return generationCounter;
    }

    synchronized public String getGraphName(){return graphName;}

    public void requestToUpdateTargetData(){
        Map<String,String> targetsStatusMap = task.getUpdatedTargetsStatusMap();
        for(Map.Entry<String,String> m:targetsStatusMap.entrySet()){
            updateTargetData(m.getKey(),m.getValue());
        }
    }

    synchronized public Mediator getMed() {
        return med;
    }

    synchronized public TaskInputDto getCopyOfParametersFromAdminDto() {
        if(taskType.equals("Compilation")){
             CompilationTaskInputDto tmp= (CompilationTaskInputDto)(parametersFromAdminDto);
           return new CompilationTaskInputDto(tmp.getIsScratch(),tmp.getCompilationSourceFolderPath(),tmp.getCompilationDestFolderPath(),
                   tmp.getTargetsToRun());
        }
        else{
            SimulationTaskInputDto tmp= (SimulationTaskInputDto)(parametersFromAdminDto);
            Long processTimeInput=tmp.getProcessTime();
             Boolean isRandomInput=tmp.getIsRandom();
                    Double probabilitySuccessInput=tmp.getProbabilitySuccess();
            Double probabilityWarningInput=tmp.getProbabilityWarning();
                    Boolean isScratchInput=tmp.getIsScratch();
            String[] targetsToRunIn=tmp.getTargetsToRun();
            return new SimulationTaskInputDto(processTimeInput,isRandomInput,probabilitySuccessInput,
                    probabilityWarningInput,isScratchInput,targetsToRunIn);
        }
    }

    synchronized public AdminTotalInfoForMissionWithOutParams getCopyOfDtoFromTheAdmin() {
        AdminTotalInfoForMissionWithOutParams tmp= new AdminTotalInfoForMissionWithOutParams(missionName,graphName,createdBy,taskType);
        tmp.setLeavesAmount(leavesAmount);
        tmp.setIndependentsAmount(independentsAmount);
        tmp.setMiddlesAmount(middlesAmount);
        tmp.setRootsAmount(rootsAmount);
        tmp.setTotalTaskPrice(totalTaskPrice);
        tmp.setTotalTargetsAmount(totalTargetsAmount);
        tmp.setPriceForTarget(priceForTarget);
         return tmp;
    }

    synchronized private void updateTargetData(String targetName, String newStatus){
        if(targetDataMap.containsKey(targetName)) {
            targetDataMap.get(targetName).updateNewStatusOnTask(newStatus);
        }else {
            targetDataMap.put(targetName,new TargetData(targetName,newStatus));
        }
    }

    public List<UpdateTargetStatusDuringTaskDto> createUpdateTargetStatusDuringTaskDtoList(){
        List<UpdateTargetStatusDuringTaskDto> res=new ArrayList<>();
        for(Map.Entry<String, TargetData> t:targetDataMap.entrySet()){
            res.add(t.getValue().getUpdateTargetStatusDuringTaskDto());
        }
        return res;
    }

    synchronized public void updateTargetsThatDidNotRun(List<TargetNameAndExtraInfoDto> targetsNamesThatDidNotRunList ){
        for(TargetNameAndExtraInfoDto dto:targetsNamesThatDidNotRunList ){
            if(waitingTargetsNamesMap.containsKey(dto.getTargetName())){
                waitingTargetsNamesMap.put(dto.getTargetName(),TARGET_AVAILABILITY.AVAILABLE);
            }
        }
    }


    public Long getRunningTargetsAmount(){           //process amount
        return task.getTargetsInProcessAmount();
    }
    public Long getAvailableTargetsToRunAmount(){    //waiting amount
        return task.getWaitingTargetsAmount();
    }

    synchronized public Set<String> getProcessedTargets(){return task.getProcessedTargets();}

    private String getTargetLogs(String targetName){
        String fileName = missionFolder + "\\" +targetName + ".log";
        String content=null;

        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            content = lines.collect(Collectors.joining(System.lineSeparator()));
        } catch(IOException ignore){}
         return content;
    }

    public void updateThatTargetRunningStateIsFinishedInTask(UpdateTargetStatusDuringTaskDto dto){
        FileWriter targetFile = null;
        String fileName = missionFolder + "\\" + dto.getTargetName() + ".log";
        try {
            targetFile = new FileWriter(fileName, true);
            task.updateThatTargetRunningStateIsFinished(dto, targetFile);
            synchronized (this) {
                if (task.isMissionDone()) {
                    missionStatus = MISSION_STATUS.FINISHED;  // (think about the impacts on the admin side)
                    //end of mission
                    Mediator.TaskType theTaskType = Mediator.TaskType.SIMULATION;
                    if (taskType.equals("Compilation")) {
                        theTaskType = Mediator.TaskType.COMPILATION;
                    }
                    taskReportStringList = med.doTaskReport(missionFolder, missionName, startTime, theTaskType);
                }
                restartWaitingTargetsNamesMap();
            }
        } catch (Exception ignore) {ignore.printStackTrace();}
        logs.add(getTargetLogs(dto.getTargetName()));
    }
    synchronized public List<String> getTaskReportStringList(){return taskReportStringList;}
    public TargetListViewDetails getTargetListViewDetails(String targetName){
        return task.getTargetListViewDetails(targetName);
    }

    public String getLog(){
        if(!logs.isEmpty()) {
            return logs.removeFirst();
        }
        else {
            return null;
        }
    }

    public MissionNameAndDto getDtoPackForTargetExecution() {
        if(taskType.equals("Simulation")){
            SimulationTaskInputDto simulationTaskInputDto=(SimulationTaskInputDto)(parametersFromAdminDto);
            Long processTime=simulationTaskInputDto.getProcessTime();
            Double probabilityOfSuccess=simulationTaskInputDto.getProbabilitySuccess();
            Double probabilityOfWarning=simulationTaskInputDto.getProbabilityWarning();
            SimulationForWorkerDto simulationForWorkerDto=new SimulationForWorkerDto(processTime,probabilityOfSuccess,probabilityOfWarning,missionFolder);
            return new MissionNameAndDto(simulationForWorkerDto,null,missionName,taskType);
        }
        else{
            CompilationTaskInputDto compilationTaskInputDto=(CompilationTaskInputDto)(parametersFromAdminDto);
            CompilationForWorkerDto compilationForWorkerDto=new CompilationForWorkerDto(compilationTaskInputDto.getCompilationSourceFolderPath(),compilationTaskInputDto.getCompilationDestFolderPath(),missionFolder);
            return new MissionNameAndDto(null,compilationForWorkerDto,missionName,taskType);
        }
    }

    synchronized public void updateThatTargetIsInProcess(String targetName){
        task.updateThatTargetIsInProcessAndUpdateTheTimeMap(targetName);
         waitingTargetsNamesMap.remove(targetName);
     }

    synchronized void restartWaitingTargetsNamesMap() {
        waitingTargetsNamesMap.clear();
        waitingTargetsNamesMap = task.getWaitingTargetsNamesSetDuringTask();
    }

    synchronized public void registerNewWorkerToTheMission(String workerName){
            registeredWorkersNames.add(workerName);
    }

    synchronized public void removeWorkerNameFromTheMission(String workerName){//todo we will need to use synchronize!!!!!!
        if(registeredWorkersNames.contains(workerName)) {
            registeredWorkersNames.remove(workerName);
        }

    }

    synchronized public Integer getAvailableTargetsToWork(Integer amountOfAvailableThreadsRightNow, List<TargetNameAndExtraInfoDto> availableTargetsToWorkOnList){
        int updatedAmountOfAvailableThreads=amountOfAvailableThreadsRightNow;
        boolean areThereEnoughThreadsRightNow=true;
        if(updatedAmountOfAvailableThreads<=0||missionStatus.equals(MISSION_STATUS.PAUSED)
                ||missionStatus.equals(MISSION_STATUS.STOPPED)){
            return 0;
        }
         if(!waitingTargetsNamesMap.isEmpty()){
             for(Map.Entry<String,TARGET_AVAILABILITY> t:waitingTargetsNamesMap.entrySet()){
                 if(t.getValue().equals(TARGET_AVAILABILITY.AVAILABLE)){
                     if(areThereEnoughThreadsRightNow) {
                         String availableTargetName=t.getKey();
                         String availableTargetExtraInfo=med.getTargetInfoDto(availableTargetName).getTargetExtraInfo();
                         if(availableTargetExtraInfo==null){
                             availableTargetExtraInfo=" ";
                         }
                         waitingTargetsNamesMap.put(availableTargetName,TARGET_AVAILABILITY.TAKEN);
                         availableTargetsToWorkOnList.add(new TargetNameAndExtraInfoDto(availableTargetName,availableTargetExtraInfo));
                         updatedAmountOfAvailableThreads--;
                         if(updatedAmountOfAvailableThreads==0){
                             return 0;
                         }
                     }
                 }
             }
         }
        return updatedAmountOfAvailableThreads;
    }

    synchronized public void updateMissionStatus(MISSION_STATUS newMissionStatus){
        switch (newMissionStatus) {
            case PLAYING:
                if (task.getAmountOfRunningTargets() == 0) {
                    missionStatus = MISSION_STATUS.FINISHED;
                    Mediator.TaskType theTaskType = Mediator.TaskType.SIMULATION;
                    if (taskType.equals("Compilation")) {
                        theTaskType = Mediator.TaskType.COMPILATION;
                    }
                    taskReportStringList = med.doTaskReport(missionFolder, missionName, startTime, theTaskType);
                    return;
                }
                missionStatus = newMissionStatus;
                startTime = Instant.now();
                break;

            case PAUSED:
                missionStatus = MISSION_STATUS.PAUSED;
                break;

            case RESUMED:
                missionStatus = MISSION_STATUS.RESUMED;
                break;

            case FINISHED:
                missionStatus = MISSION_STATUS.FINISHED;
                break;

            case STOPPED:
                missionStatus = MISSION_STATUS.STOPPED;
                break;
        }

    }

    synchronized public Boolean isWorkerRegistered(String workerName){return registeredWorkersNames.contains(workerName);}

    public AdminPendingMissionsTableViewDtoWithoutRadioButton getAdminPendingMissionsTableViewDto(){//for the admins- every admin gets it with the timer and updates
       int amountOfWorkersOnTask=registeredWorkersNames.size();
        return new AdminPendingMissionsTableViewDtoWithoutRadioButton(missionName,graphName,createdBy,
         leavesAmount, independentsAmount, middlesAmount,rootsAmount,
          totalTaskPrice, Integer.toString(amountOfWorkersOnTask),missionStatus.toString());
    }

    public WorkerPendingMissionsTableViewDtoWithOutCheckBox getWorkerPendingMissionsTableViewDto(String workerName){//for the worker- every worker gets it with the timer and updates
       String isRegistered;
       synchronized (this) {
           if (registeredWorkersNames.contains(workerName)) {
               isRegistered = "Yes";
           } else {
               isRegistered = "No";
           }
       }
        Integer amountOfWorkersOnTask=registeredWorkersNames.size();
        return new WorkerPendingMissionsTableViewDtoWithOutCheckBox(missionName,createdBy,taskType,leavesAmount,
                independentsAmount,middlesAmount,rootsAmount,totalTargetsAmount,priceForTarget,
                missionStatus.toString(),amountOfWorkersOnTask.toString(),isRegistered,getProcessedTargets());
    }


    public String getMissionName() {
        return missionName;
    }
    synchronized public void setMissionName(String missionName) {
        this.missionName = missionName;
    }
    public String getLeavesAmount() {
        return leavesAmount;
    }
    synchronized public void setLeavesAmount(String leavesAmount) {
        this.leavesAmount = leavesAmount;
    }
    public String getIndependentsAmount() {
        return independentsAmount;
    }
    synchronized public void setIndependentsAmount(String independentsAmount) {
        this.independentsAmount = independentsAmount;
    }
    public String getMiddlesAmount() {
        return middlesAmount;
    }
    synchronized public void setMiddlesAmount(String middlesAmount) {
        this.middlesAmount = middlesAmount;
    }
    public String getRootsAmount() {
        return rootsAmount;
    }
    synchronized public void setRootsAmount(String rootsAmount) {
        this.rootsAmount = rootsAmount;
    }
    public String getTotalTaskPrice() {
        return totalTaskPrice;
    }
    synchronized public void setTotalTaskPrice(String totalTaskPrice) {
        this.totalTaskPrice = totalTaskPrice;
    }
    public String getAmountOfWorkersOnTask() {
        Integer amountOfWorkersOnTask=registeredWorkersNames.size();
        return amountOfWorkersOnTask.toString();
    }
    public Integer getAmountOfWorkersOnTaskAsInteger() {
        Integer amountOfWorkersOnTask=registeredWorkersNames.size();
        return amountOfWorkersOnTask;
    }
    public String getTotalTargetsAmount() {
        return totalTargetsAmount;
    }
    public void setTotalTargetsAmount(String totalTargetsAmount) {
        this.totalTargetsAmount = totalTargetsAmount;
    }
    public String getPriceForTarget() {
        return priceForTarget;
    }
    synchronized public void setPriceForTarget(String priceForTarget) {
        this.priceForTarget = priceForTarget;
    }
    public MISSION_STATUS getMissionStatus() {
        return missionStatus;
    }
    //synchronized public void setMissionStatus(MISSION_STATUS missionStatus) {this.missionStatus = missionStatus;}

}
