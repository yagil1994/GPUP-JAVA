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
import java.io.PrintWriter;
import java.util.Map;

public class TellServerThatWorkerUnregisterMission extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.setContentType("application/json");
       MissionManager missionsManager = ServletUtils.getMissionManager((getServletContext()));
       Map<String, Mission> missionsMap = missionsManager.getMissionsMap();
       String missionName=request.getParameter(Constants.MISSION_NAME);
        String workerName=request.getParameter(Constants.USERNAME);
        PrintWriter out = response.getWriter();
        if(!missionsManager.isMissionExist(missionName)){
            out.println("mission name does not exist!");
            return;
        }
        if(!ServletUtils.getUserManager(getServletContext()).isUserExists(workerName)){
            out.println("user name does not exist!");
            return;
        }
        if(!missionsManager.getMissionsMap().get(missionName).isWorkerRegistered(workerName)){
            out.println("worker is not registered to the mission!");
            return;
        }
       Mission mission=missionsMap.get(missionName);
       mission.removeWorkerNameFromTheMission(workerName);
       out.println("unregistered worker from the mission successfully");
    }
}
