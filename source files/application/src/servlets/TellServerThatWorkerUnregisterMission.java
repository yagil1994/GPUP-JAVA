package servlets;


import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;
import java.io.IOException;
import java.util.Map;

public class TellServerThatWorkerUnregisterMission extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.setContentType("application/json");
       MissionManager missionsManager = ServletUtils.getMissionManager((getServletContext()));
       Map<String, Mission> missionsMap = missionsManager.getMissionsMap();
       String missionName=request.getParameter(Constants.MISSION_NAME);
       String workerName=request.getParameter(Constants.USERNAME);
       Mission mission=missionsMap.get(missionName);
       mission.removeWorkerNameFromTheMission(workerName);
    }
}
