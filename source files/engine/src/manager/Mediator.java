package manager;

import java.time.Duration;
import data.structure.Graph;
import data.structure.Target;
import generated.GPUPDescriptor;
import input.task.TaskInputDto;
import mission.AdminTotalInfoForMissionWithOutParams;
import output.*;
import task.impl.AbstractTaskManager;
import task.impl.CompilationTask;
import task.impl.SimulationTask;
import xml.TargetNotExist;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Mediator implements Serializable {
    public enum TaskType {
        SIMULATION, COMPILATION
    }
    private Graph OriginalTargetsGraph;
    private final Map<String,Graph> graphsForTaskOnlyMap;//{key=mission name,val=graphForTaskOnl}
    private final Map<String,AbstractTaskManager> taskManagerMap;//{key=mission name,val=AbstractTaskManager for this mission}
    private String uploadedBy;

    public Mediator() {
        OriginalTargetsGraph = null;
        uploadedBy=null;
        graphsForTaskOnlyMap=new HashMap<>();
        taskManagerMap=new HashMap<>();
    }
    public String getOriginalGraphName(){return OriginalTargetsGraph.getName();}
     //public void updateGraphForTaskOnlyToNullAfterLoadXml(){graphForTaskOnly=null;}

    public AbstractTaskManager getTaskManager(String missionName){return taskManagerMap.get(missionName);}

    final public Map<String,String> getPricePerTargetInSpecificTaskTypeMapFromGraph(){
        Map<String,String> res=new HashMap<>();
       String priceInSimulation= getPricePerTargetInSimulation();
       String priceInCompilation= getPricePerTargetInCompilation();
       if(priceInSimulation==null){
           priceInSimulation=" ";
       }
       if(priceInCompilation==null){
           priceInCompilation=" ";
       }
       res.put("Simulation",priceInSimulation);
       res.put("Compilation",priceInCompilation);
       return res;
    }

    final public GraphInfoDto getGraphInfoDto() {
        Integer targetsAmount = OriginalTargetsGraph.getTargetsAmount();
        Integer rootsAmount = OriginalTargetsGraph.getRootsAmount();
        Integer middlesAmount = OriginalTargetsGraph.getMiddlesAmount();
        Integer leavesAmount = OriginalTargetsGraph.getLeavesAmount();
        Integer independentsAmount = OriginalTargetsGraph.getIndependentAmount();
        return new GraphInfoDto(targetsAmount, rootsAmount, middlesAmount, leavesAmount, independentsAmount);
    }

    final public String getPricePerTargetInSimulation(){
        if(OriginalTargetsGraph.getPricePerTargetInSpecificTaskTypeMap().containsKey(TaskType.SIMULATION)) {
            return OriginalTargetsGraph.getPricePerTargetInSpecificTaskTypeMap().get(TaskType.SIMULATION).toString();
        }
        return null;
   }

    public String getPricePerTargetInCompilation(){
        if(OriginalTargetsGraph.getPricePerTargetInSpecificTaskTypeMap().containsKey(TaskType.COMPILATION)) {
            return OriginalTargetsGraph.getPricePerTargetInSpecificTaskTypeMap().get(TaskType.COMPILATION).toString();
        }
        return null;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    //public TargetListViewDetails getTargetListViewDetailsFromAbstractTask(String targetName) {
     //   return taskManager.getTargetListViewDetails(targetName);
   // }

    final public AllDependenciesOfTargetDto getAllDependenciesOfTargetDtoFromGraph(String targetName) {
        return OriginalTargetsGraph.getAllDependenciesOfTargetDto(targetName);
    }

    final public TargetInfoDto getTargetInfoDto(String name) {
        Target t = OriginalTargetsGraph.findTargetByName(name);
        if (t == null) {
            return null;
        }
        String targetName = t.getName(), targetExtraInfo = t.getExtraInfo(), targetStatus = t.getStatus();
        List<String> outTargetListNames = t.getOutTargetList().stream().map(Target::getName).collect(Collectors.toList());
        List<String> inTargetListNames = t.getInTargetList().stream().map(Target::getName).collect(Collectors.toList());
        return new TargetInfoDto(targetName, outTargetListNames, inTargetListNames, targetExtraInfo, targetStatus);
    }

    final public Set<TargetInfoForTableViewDto> getAllTargetsInfoForTableViewDtoFromGraph() {
        return OriginalTargetsGraph.getAllTargetsInfoForTableView();
    }

    final public Set<String> getAllTargetsNamesInSetFromGraph() {return OriginalTargetsGraph.getAllTargetsNamesInSet();}

    final public Set<String> getAllTargetsNamesInSetFromGraphTaskOnly(String lastTaskName) {
           return getGraphForTaskOnly(lastTaskName).getAllTargetsNamesInSet();
    }

    final public AllPathsOfTwoTargetsDto findBindBetweenTwoTargets(final String str1, final String str2) {
        Target t1 = OriginalTargetsGraph.findTargetByName(str1);
        Target t2 = OriginalTargetsGraph.findTargetByName(str2);
        return new AllPathsOfTwoTargetsDto(OriginalTargetsGraph.getAllPaths(t1, t2));
    }

    public Graph getGraphForTaskOnly(String missionName){return graphsForTaskOnlyMap.get(missionName);}//returns null if not exist

    public void createGraphForTask(TaskInputDto taskInput, AdminTotalInfoForMissionWithOutParams a,boolean runTaskAgainWithIncrement,String newMissionName) {
        Boolean isScratch = taskInput.getIsScratch();
        String missionName = a.getMissionName();
        String[] targetsToRun = taskInput.getTargetsToRun();
        Set<String> setTargetsToRun = new HashSet<>(Arrays.asList(targetsToRun));
        Mediator.TaskType type = TaskType.SIMULATION;
        String newMissionNameCreated=null;
        if (a.getTaskType().equals("Compilation")) {
            type = TaskType.COMPILATION;
        }
        switch (type) {
            case SIMULATION: {
                if (isScratch) {
                    graphsForTaskOnlyMap.put(missionName, new Graph(setTargetsToRun, OriginalTargetsGraph));
                   AbstractTaskManager newTaskManagerForNewMission=new SimulationTask(getGraphForTaskOnly(missionName), taskInput);
                    taskManagerMap.put(missionName,newTaskManagerForNewMission);
                }
                else {
                    String nameOfLastMissionOnThisGraph=missionName;
                    if(graphsForTaskOnlyMap.size()-1==0){
                        nameOfLastMissionOnThisGraph=missionName;
                    }
                    Graph graphFromLastRun = getGraphForTaskOnly(nameOfLastMissionOnThisGraph);
                    AbstractTaskManager taskManagerFromLastRun=getTaskManager(nameOfLastMissionOnThisGraph);
                    graphsForTaskOnlyMap.put(newMissionName, new Graph(setTargetsToRun, graphFromLastRun));
                    AbstractTaskManager newTaskManagerForNewMission=new SimulationTask(getGraphForTaskOnly(newMissionName),taskInput,taskManagerFromLastRun,runTaskAgainWithIncrement);
                    taskManagerMap.put(newMissionName,newTaskManagerForNewMission);
                }
                break;
            }
            case COMPILATION: {
                if (isScratch) {
                    graphsForTaskOnlyMap.put(missionName, new Graph(setTargetsToRun, OriginalTargetsGraph));
                    AbstractTaskManager newTaskManagerForNewMission=new CompilationTask(getGraphForTaskOnly(missionName), taskInput);
                    taskManagerMap.put(missionName,newTaskManagerForNewMission);
                }
                else {
                    String nameOfLastMissionOnThisGraph=missionName;
                    Graph graphFromLastRun = getGraphForTaskOnly(nameOfLastMissionOnThisGraph);
                    AbstractTaskManager taskManagerFromLastRun=getTaskManager(nameOfLastMissionOnThisGraph);
                    graphsForTaskOnlyMap.put(newMissionName, new Graph(setTargetsToRun, graphFromLastRun));
                    AbstractTaskManager newTaskManagerForNewMission=new CompilationTask(getGraphForTaskOnly(newMissionName),taskInput,taskManagerFromLastRun,runTaskAgainWithIncrement);
                    taskManagerMap.put(newMissionName,newTaskManagerForNewMission);
                }
                break;
            }
        }
    }

    public List<String> doTaskReport(String missionFolder,String missionName,Instant startTime, Mediator.TaskType taskType) {
        FileWriter summaryTaskReport = null;
        List<String> taskReportStringList=new ArrayList<>();
        try {
            String fileName = missionFolder + "\\" + "report.log";
            summaryTaskReport = new FileWriter(fileName);
            if (taskManagerMap.get(missionName).getAmountOfRunningTargets() == 0) {
                taskReportStringList.add("All the targets already finished in previous task!");
                summaryTaskReport.write("All the targets all ready finished in previous task!");
            } else {
                Instant endTime = Instant.now();
                Duration res = Duration.between(startTime, endTime);
                String totalTaskTime = taskManagerMap.get(missionName).getFullTimeFromDuration(res);
                String articleTotalTime = "The total time the task has taken is: " + totalTaskTime + "\n";
                taskReportStringList.add(articleTotalTime);
                summaryTaskReport.write(articleTotalTime);

                Map<Target, AbstractTaskManager.RunningResult> targetsRunningResultMapFromTask = getTargetsRunningResultMapFromTask(missionName);
                List<String> successfulls = new ArrayList<>(), warnings = new ArrayList<>(), failures = new ArrayList<>(), skipped = new ArrayList<>(), dontChecks = new ArrayList<>();
                boolean isIncrementalTask = false;
                for (Map.Entry<Target, AbstractTaskManager.RunningResult> t : targetsRunningResultMapFromTask.entrySet()) {
                    if (t.getValue().equals(AbstractTaskManager.RunningResult.SUCCESS)) {
                        successfulls.add(t.getKey().getName());
                    } else if (t.getValue().equals(AbstractTaskManager.RunningResult.WARNING)) {
                        warnings.add(t.getKey().getName());
                    } else if (t.getValue().equals(AbstractTaskManager.RunningResult.FAILURE)) {
                        failures.add(t.getKey().getName());
                    } else if (t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED)) {
                        skipped.add(t.getKey().getName());
                    } else {//DONT_CHEK
                        dontChecks.add(t.getKey().getName());
                        isIncrementalTask = true;
                    }
                }
                String incrementalMessage = "", uniteAll = "";
                if (isIncrementalTask) {
                    incrementalMessage = "In this task there are targets that have already been processed successfully before.\n\n" +
                            "From previous tasks: " + dontChecks.size() + " | ";
                }
                String successfullsStr = "Successfulls amount: " + successfulls.size() + " | ";
                String warningsStr = "Warnings amount: " + warnings.size() + " | ";
                String failuresStr = "Failures amount: " + failures.size() + " | ";
                String skippedStr = "Skipped amount: " + skipped.size() + "\n\n";
                if (isIncrementalTask) {
                    uniteAll = incrementalMessage + successfullsStr + warningsStr + failuresStr + skippedStr;
                } else {
                    uniteAll = successfullsStr + warningsStr + failuresStr + skippedStr;
                }
                String finalUniteAll = uniteAll;

                taskReportStringList.add(finalUniteAll);
                summaryTaskReport.write(finalUniteAll);
                if (isIncrementalTask && dontChecks.size() > 0) {
                    String introOfPreviousSuccess = "The targets that already succeeded in previous tasks are: ";
                    taskReportStringList.add(introOfPreviousSuccess);
                    summaryTaskReport.write(introOfPreviousSuccess);

                    for (String name : dontChecks) {
                        taskReportStringList.add(name + " ");
                        summaryTaskReport.write(name + " ");
                    }
                    taskReportStringList.add("\n");
                    summaryTaskReport.write("\n");
                }
                if (successfulls.size() > 0) {
                    String successfullsIntroduction = "The successful targets are: ";
                    taskReportStringList.add(successfullsIntroduction);
                    summaryTaskReport.write(successfullsIntroduction);

                    for (String name : successfulls) {
                        taskReportStringList.add(name + " ");
                        summaryTaskReport.write(name + " ");
                    }
                    taskReportStringList.add("\n");
                    summaryTaskReport.write("\n");
                }
                if (warnings.size() > 0) {
                    String warningsIntroduction = "The successful with warning targets are: ";
                    taskReportStringList.add(warningsIntroduction);
                    summaryTaskReport.write(warningsIntroduction);
                    for (String name : warnings) {
                        taskReportStringList.add(name + " ");
                        summaryTaskReport.write(name + " ");
                    }
                    taskReportStringList.add("\n");
                    summaryTaskReport.write("\n");
                }
                if (failures.size() > 0) {
                    String failureIntroduction = "The failures targets are: ";
                    taskReportStringList.add(failureIntroduction);
                    summaryTaskReport.write(failureIntroduction);
                    for (String name : failures) {
                        taskReportStringList.add(name + " ");
                        summaryTaskReport.write(name + " ");
                    }
                    taskReportStringList.add("\n");
                    summaryTaskReport.write("\n");
                }
                if (skipped.size() > 0) {
                    String skippedIntroduction = "The skipped targets are: ";
                    taskReportStringList.add(skippedIntroduction);
                    summaryTaskReport.write(skippedIntroduction);
                    for (String name : skipped) {
                        taskReportStringList.add(name + " ");
                        summaryTaskReport.write(name + " ");
                    }
                    taskReportStringList.add("\n");
                    summaryTaskReport.write("\n");
                }
                taskReportStringList.add("\n");
                summaryTaskReport.write("\n");

                String tName = "", tTaskRes = "", uniteSummary = "", failureReason = "Failure reason: The success rates weren't high enough for this target on the task\n", skippedReason = "";
                if (taskType.equals(TaskType.COMPILATION)) {
                    failureReason = "Failure reason: the compilation process on this target has been failed\n";
                }
                for (Map.Entry<Target, AbstractTaskManager.RunningResult> t : targetsRunningResultMapFromTask.entrySet()) {
                    if (!(t.getValue().equals(AbstractTaskManager.RunningResult.DONT_CHECK))) {
                        tName = "Target Name: " + t.getKey().getName() + "\n";
                        tTaskRes = "Running result: " + t.getValue().toString() + "\n";
                        if (t.getValue().equals(AbstractTaskManager.RunningResult.FAILURE)) {
                            tTaskRes += failureReason;
                        }

                        uniteSummary = tName + tTaskRes;
                        if (!(t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED))) {
                            uniteSummary = uniteSummary + "Running time: " +  getTargetsRunningTimeMapFromTask(missionName).get(t.getKey());
                        }
                        String finalUniteSummary = uniteSummary;
                        if (!(t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED)))
                            finalUniteSummary += "\n\n";
                        String finalUniteSummary1 = finalUniteSummary;
                        taskReportStringList.add(finalUniteSummary1);
                        summaryTaskReport.write(finalUniteSummary1);

                        if (t.getValue().equals(AbstractTaskManager.RunningResult.SKIPPED)) {
                            String cause = "";
                            for (Target u : t.getKey().getOutTargetList()) {
                                if (targetsRunningResultMapFromTask.get(u).equals(AbstractTaskManager.RunningResult.FAILURE) ||
                                        targetsRunningResultMapFromTask.get(u).equals(AbstractTaskManager.RunningResult.SKIPPED)) {
                                    cause = u.getName();
                                    break;
                                }
                            }
                            String targetName = t.getKey().getName();
                            skippedReason = "'" + targetName + "' is skipped because '" + cause + "' has been skipped or failed before" +
                                    " and '" + targetName + "' depends on '" + cause + "'.";
                            String finalSkippedReason = skippedReason + "\n\n";
                            taskReportStringList.add(finalSkippedReason);
                            summaryTaskReport.write(finalSkippedReason);
                        }

                    }
                }
            }
        } catch (IOException ignore) {}
            finally{
        if (summaryTaskReport != null) {
            try {
                summaryTaskReport.close();
            } catch (IOException ignored) {
            }
        }
    }
      return taskReportStringList;
}

    private Map<Target,AbstractTaskManager.RunningResult> getTargetsRunningResultMapFromTask(String missionName) {
        AbstractTaskManager taskManager=taskManagerMap.get(missionName);
        if(taskManager!=null){
            return taskManager.getTargetRunningResultMap();
        }
        return null;
    }

    private Map<Target,String> getTargetsRunningTimeMapFromTask(String missionName) {
        AbstractTaskManager taskManager=taskManagerMap.get(missionName);
        if(taskManager!=null){
            return taskManager.getTargetsRunningTimeMap();
        }
        return null;
    }

    private String getTaskThePath(String missionName) {
        AbstractTaskManager taskManager=taskManagerMap.get(missionName);
        if(taskManager!=null){
           return taskManager.getTaskPath();
        }
       return null;
    }

    private final static String JAXB_XML_GPUP_PACKAGE_NAME = "generated";

    public void loadFile(InputStream inputStream) throws IOException, JAXBException {
        GPUPDescriptor descriptor;
            descriptor = deserializeFrom(inputStream);
        OriginalTargetsGraph = new Graph(descriptor);
    }

    private GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GPUP_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (GPUPDescriptor) u.unmarshal(in);
    }

    public CirclePathForTargetDto checkIfTargetIsInCircle(String targetName) throws TargetNotExist {
        Target t = OriginalTargetsGraph.findTargetByName(targetName);
        if (t == null) {
            throw new TargetNotExist("target does not exist!\nTry enter other target");
        } else {
            return new CirclePathForTargetDto(OriginalTargetsGraph.findCircleFromTarget(t));
        }
    }

}

