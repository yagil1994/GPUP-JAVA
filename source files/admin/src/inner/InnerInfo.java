package inner;

import mission.AdminPendingMissionsTableViewDto;
import output.GraphInfoDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InnerInfo {
    private final Map<String, AdminMission> myMissionInfoMap; //missionName,mission

    public InnerInfo() {
        myMissionInfoMap =new HashMap<>();
    }

    public void addAdminMission(String missionName, String graphName, GraphInfoDto dto, Consumer<List<String>> updateTaskReportConsumerIn,String missionStatusIn){
        AdminMission mission=new AdminMission(missionName, graphName,dto.getTargetsAmount().toString(),dto.getIndependentAmount().toString(),
                dto.getLeavesAmount().toString(),dto.getMiddlesAmount().toString(),dto.getRootsAmount().toString(),updateTaskReportConsumerIn,missionStatusIn);
        myMissionInfoMap.put(missionName,mission);
    }
    public void updateMissionsStatus(AdminPendingMissionsTableViewDto[] adminPendingMissionsTableViewDtos){
        for(int i=0;i<adminPendingMissionsTableViewDtos.length;i++){
            String currentMissionNameInDto=adminPendingMissionsTableViewDtos[i].getMissionName();
            String newMissionStatus=adminPendingMissionsTableViewDtos[i].getMissionStatus();
            if(myMissionInfoMap.get(currentMissionNameInDto)!=null){
                myMissionInfoMap.get(currentMissionNameInDto).setMissionStatus(newMissionStatus);
            }
        }
    }

    public String getMissionStatus(String missionName){
       return myMissionInfoMap.get(missionName).getMissionStatus();
    }
    public Map<String, AdminMission> getMyMissionInfoMap() {
        return myMissionInfoMap;
    }
}
