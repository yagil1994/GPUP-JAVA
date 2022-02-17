package misc;

import mini.engine.WorkerThreadPoolManager;
import mission.MissionNameAndDto;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MissionNamesContainer {
    private Set<String> missionNamesSet;
    private WorkerThreadPoolManager workerThreadPoolManager;
    private final Map<String, MissionNameAndDto> allMissionsParametersMap;//{key=mission name,value=the dto for the mission}

    public MissionNamesContainer(){
        missionNamesSet=new HashSet<>();
        workerThreadPoolManager=null;
        allMissionsParametersMap= new HashMap<>();
    }

    public Set<String> getMissionNamesSet() {return missionNamesSet;}

    public Map<String, MissionNameAndDto> getAllMissionsParametersMap() {
        return allMissionsParametersMap;
    }

    public void setMissionNamesSet(Set<String> missionNamesSet) {
        this.missionNamesSet = missionNamesSet;
    }
    public void addWorkerThreadPoolToContainer(WorkerThreadPoolManager workerThreadPoolManagerIn){
         workerThreadPoolManager=workerThreadPoolManagerIn;
    }
    public String[] getMissionArr(){
        String[] res=new String[missionNamesSet.size()];
        int i=0;
        for(String s:missionNamesSet){
            res[i]=s;
            i++;
        }
        return res;
    }

    public WorkerThreadPoolManager getWorkerThreadPoolManager() {
        return workerThreadPoolManager;
    }
}
