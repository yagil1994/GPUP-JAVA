package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import mission.MissionManager;
import mission.TargetNameAndExtraInfoDto;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAvailableTargetsToMissionInPostman extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String missionName = request.getParameter(Constants.MISSION_NAME);
        String workerName = request.getParameter(Constants.USERNAME);
        Integer amountOfAvailableThreadsRightNowInteger = 200;
        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        Map<String, Mission> missionMap = missionManager.getMissionsMap();
        Mission currentMission = missionMap.get(missionName);
        PrintWriter out = response.getWriter();
        if (!missionManager.isMissionExist(missionName)) {
            out.println("mission name does not exist!");
            return;
        }
        if (!ServletUtils.getUserManager(getServletContext()).isUserExists(workerName)) {
            out.println("user name does not exist!");
            return;
        }
        if (!currentMission.isWorkerRegistered(workerName)){
            out.println("worker is not registered to the mission!\n");
            return;
        }
        Map<String, List<TargetNameAndExtraInfoDto>> availableTargetsToWorkOnMap=new HashMap<>();//{key=mission name,value=target name}
        List<TargetNameAndExtraInfoDto> targetsToRunOnTask=new ArrayList<>();
        availableTargetsToWorkOnMap.put(missionName,targetsToRunOnTask);
        if( currentMission.getMissionStatus().equals(Mission.MISSION_STATUS.PLAYING)||
                currentMission.getMissionStatus().equals(Mission.MISSION_STATUS.RESUMED)) {
            List<TargetNameAndExtraInfoDto> targetsAvailableForThisSpecificMission = availableTargetsToWorkOnMap.get(missionName);
            currentMission.getAvailableTargetsToWork(amountOfAvailableThreadsRightNowInteger, targetsAvailableForThisSpecificMission);
            if (availableTargetsToWorkOnMap.get(missionName).isEmpty()) {
                out.println(" there are not available targets to run at this moment!\n");
            } else {
                out.println("the available targets to run are:");
                printAvailableTargetsToRun(out, availableTargetsToWorkOnMap.get(missionName));
            }
        }
        else{
            out.println("it is impossible to get targets at this moment because the mission status os not playing or resumed");
        }
    }
    private void printAvailableTargetsToRun(PrintWriter out, List<TargetNameAndExtraInfoDto> targetsToRunOnTask){
        for(TargetNameAndExtraInfoDto t:targetsToRunOnTask){
            out.println("*****************************");
            out.println("target name:"+t.getTargetName());
            out.println("extra info:"+t.getExtraInfo());
            out.println("*****************************");
        }
    }
}
