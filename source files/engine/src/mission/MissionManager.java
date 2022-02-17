package mission;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MissionManager {

    private final Map<String,Mission> missionsMap;//the string is the mission name,and the mission is the mission instance

    public MissionManager(){
        missionsMap=new HashMap<>();
    }

    public synchronized void addMission(String missionName,Mission newMission) {
        missionsMap.put(missionName,newMission);
    }
    synchronized public boolean isMissionExist(String missionName) {return missionsMap.containsKey(missionName);}

    public synchronized Map<String,Mission> getMissionsMap() {
        return Collections.unmodifiableMap(missionsMap);
    }
}
