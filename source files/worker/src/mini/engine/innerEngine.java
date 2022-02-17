package mini.engine;

import mini.engine.row.GeneralInfoTableviewRow;
import mission.WorkerPendingMissionsTableViewDto;

import java.util.*;

public class innerEngine {
    private final Map<String,WorkerMission> registeredMissions;
    private final Map<TargetMission,WorkerTarget> processedTargets;
    private WorkerThreadPoolManager workerThreadPoolManager;

    public innerEngine(){
        registeredMissions=new HashMap<>();
        processedTargets=new HashMap<>();
        workerThreadPoolManager=null;
    }

    public WorkerThreadPoolManager getWorkerThreadPoolManager() {
        return workerThreadPoolManager;
    }
    public void addWorkerThreadPoolToContainer(WorkerThreadPoolManager workerThreadPoolManagerIn){
        workerThreadPoolManager=workerThreadPoolManagerIn;
    }

    public Integer getPriceFromMission(String missionName){
        Integer price=0;
        for (Map.Entry<TargetMission,WorkerTarget> t:processedTargets.entrySet()){
           if(t.getKey().getMissionName().equals(missionName)){
               String p=t.getValue().getPrice();
               price+=Integer.parseInt(p);
           }
        }
        return price;
    }

    public Integer getWorkerProcessedTargetsFromMissionAmount(String missionName){
        Integer counter=0;
        Set<String> alreadyProcessedTargets = registeredMissions.get(missionName).getProcessedTargets();
        for(String s: alreadyProcessedTargets){
            if(processedTargets.containsKey(s)){
                counter++;
            }
        }
        return counter;
    }

    public void pauseMission(String missionName){
        registeredMissions.get(missionName).setPause();
    }
    public void resumeMission(String missionName){
        registeredMissions.get(missionName).setResume();
    }
    public Boolean isMissionPaused(String missionName){
        return registeredMissions.get(missionName).isPause();
    }
//    public void addMission(WorkerPendingMissionsTableViewDto[] dto){
//        for(int i=0;i<dto.length;i++) {
//            if (dto[i].getIsRegistered().equals("Yes")) {
//                if (doesMissionExist(dto[i].getMissionName())) {
//                    registeredMissions.get(dto[i].getMissionName()).setMissionStatus(dto[i].getMissionStatus());
//                    registeredMissions.get(dto[i].getMissionName()).setAmountOfWorkersOnTask(dto[i].getAmountOfWorkersOnTask());
//                    registeredMissions.get(dto[i].getMissionName()).setProcessedTargets(dto[i].getProcessedTargets());
//                } else {
//                    WorkerMission newWorkerMission = new WorkerMission(dto[i].getMissionName(), dto[i].getCreatedBy(),
//                            dto[i].getTaskType(), dto[i].getLeavesAmount(), dto[i].getIndependentsAmount(),
//                            dto[i].getMiddlesAmount(), dto[i].getRootsAmount(), dto[i].getTotalTargetsAmount(), dto[i].getPriceForTarget());
//                    newWorkerMission.setMissionStatus(dto[i].getMissionStatus());
//                    newWorkerMission.setAmountOfWorkersOnTask(dto[i].getAmountOfWorkersOnTask());
//                    newWorkerMission.setProcessedTargets(dto[i].getProcessedTargets());
//                    registeredMissions.put(dto[i].getMissionName(), newWorkerMission);
//                }
//            }
//        }
//}
    public void updateMissions(WorkerPendingMissionsTableViewDto[] dto){
        for(int i=0;i<dto.length;i++) {
            if (dto[i].getIsRegistered().equals("Yes")) {
                registeredMissions.get(dto[i].getMissionName()).updateMembers(dto[i].getCreatedBy(), dto[i].getTaskType(), dto[i].getLeavesAmount(), dto[i].getIndependentsAmount(),
                        dto[i].getMiddlesAmount(), dto[i].getRootsAmount(), dto[i].getTotalTargetsAmount(), dto[i].getPriceForTarget());
                registeredMissions.get(dto[i].getMissionName()).setMissionStatus(dto[i].getMissionStatus());
                registeredMissions.get(dto[i].getMissionName()).setAmountOfWorkersOnTask(dto[i].getAmountOfWorkersOnTask());
                registeredMissions.get(dto[i].getMissionName()).setProcessedTargets(dto[i].getProcessedTargets());
            }
        }

    }
    public void addMission(String missionName, WorkerMission mission){
        registeredMissions.put(missionName, mission);
    }
//    public void addMissions(Set<String> allSelectedTasksNames){
//        for(String s:allSelectedTasksNames)
//        registeredMissions.put(s, new WorkerMission(s));
//    }

    public void addMissions(Set<WorkerPendingMissionsTableViewDto> allSelectedRows){
        for(WorkerPendingMissionsTableViewDto s:allSelectedRows)
            registeredMissions.put(s.getMissionName(), new WorkerMission(s));
    }

    public void removeMission(String missionName){
        registeredMissions.remove(missionName);
    }
    public Boolean doesMissionExist(String missionName){
        return registeredMissions.containsKey(missionName);
    }
    public Map<String, WorkerMission> getRegisteredMissionsMap() {
        return registeredMissions;
    }
    public Set<String> getRegisteredMissionsNamesSet(){
        return registeredMissions.keySet();
    }
    public List<String> getUnpauseRegisteredMissionsNamesList(){
        List<String>res=new ArrayList<>();
        for(Map.Entry<String,WorkerMission> m:registeredMissions.entrySet()){
            if(!m.getValue().isPause()){
               res.add(m.getKey());
            }
        }
        return res;
    }
    public String[] getUnpauseRegisteredMissionArr(){
        List<String> missionNamesSet=getUnpauseRegisteredMissionsNamesList();
        String[] res=new String[missionNamesSet.size()];
        int i=0;
        for(String s:missionNamesSet){
            res[i]=s;
            i++;
        }
        return res;
    }
    public Set<String> getUnfinishedRegisteredMissionsNamesSet(){
        Set<String>res=new HashSet<>();
        for(Map.Entry<String,WorkerMission> m:registeredMissions.entrySet()){
            if(!(m.getValue().getMissionStatus().equals("STOPPED")||m.getValue().getMissionStatus().equals("FINISHED"))){
                res.add(m.getKey());
            }
        }
        return res;
    }

    public String getTotalCredits(){
        Integer credits=0;
        for(WorkerTarget t:processedTargets.values()){
            credits+=Integer.parseInt(t.getPrice());
        }
        return credits.toString();
    }
    public List<String> getLogs(String targetName, String missionName){
        return processedTargets.get(new TargetMission(targetName,missionName)).getLogs();
    }
    public List<GeneralInfoTableviewRow> getGeneralInfoTableviewRowList(){
        List<GeneralInfoTableviewRow>res=new ArrayList<>();
        for(WorkerTarget t:processedTargets.values()){
            res.add(t.createGeneralInfoTableviewRow());
        }
        return res;
    }
    public void addProcessedTarget(String targetName, String missionName, WorkerTarget target){
        target.setStatus("IN_PROCESS");
        target.setPriceForTheTarget(Integer.parseInt(registeredMissions.get(target.getMissionName()).getPriceForTarget()));
        target.setPrice("0");
        processedTargets.put(new TargetMission(targetName,missionName), target);
    }
    public Boolean isTargetExist(String targetName, String missionName){
        return processedTargets.containsKey(new TargetMission(targetName,missionName));
    }
    public Map<TargetMission, WorkerTarget> getProcessedTargets() {return processedTargets;}
    public Set<TargetMission> getProcessedTargetsNamesSet(){
        return processedTargets.keySet();
    }
    public void SetTargetStatus(String targetName, String missionName, String newStatus){
        TargetMission t=new TargetMission(targetName,missionName);
        processedTargets.get(t).setStatus(newStatus);
        if(newStatus.equals("SUCCESS")||newStatus.equals("WARNING")){
            processedTargets.get(t).setPrice(processedTargets.get(t).getPriceForTheTarget().toString());
        }
    }
}
