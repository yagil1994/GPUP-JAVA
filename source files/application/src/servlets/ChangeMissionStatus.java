package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.Mediator;
import manager.MediatorsManager;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class ChangeMissionStatus extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            String missionName=request.getParameter(Constants.MISSION_NAME);
            String actionToDoDuringTask=request.getParameter(Constants.ACTION_TO_DO_DURING_MISSION);
            MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
            Map<String, Mission> missionMap = missionManager.getMissionsMap();
            Mission mission=missionMap.get(missionName);
            Mission.MISSION_STATUS newStatusMission=Mission.MISSION_STATUS.PLAYING;
            if(actionToDoDuringTask.equals("FINISHED")){
                newStatusMission= Mission.MISSION_STATUS.FINISHED;
            }
            else if(actionToDoDuringTask.equals("PAUSED")){
                newStatusMission= Mission.MISSION_STATUS.PAUSED;
            }
            else if(actionToDoDuringTask.equals("RESUMED")){
                newStatusMission= Mission.MISSION_STATUS.RESUMED;
            }
            else if(actionToDoDuringTask.equals("STOPPED")){
                newStatusMission= Mission.MISSION_STATUS.STOPPED;
            }
            mission.updateMissionStatus(newStatusMission);
        }
    }

}
