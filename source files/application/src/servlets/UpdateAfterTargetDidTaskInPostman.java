package servlets;

import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;

public class UpdateAfterTargetDidTaskInPostman extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String missionName=request.getParameter(Constants.MISSION_NAME);
        String targetName=request.getParameter(Constants.TARGET_NAME);
        String runningResult=request.getParameter("runningResult");
        PrintWriter out = response.getWriter();
        if(!(runningResult.equals("WARNING")||runningResult.equals("SUCCESS")||runningResult.equals("FAILURE"))){
            out.println("running result argument has to be: 'WARNING' or 'SUCCESS' or 'FAILURE'");
            return;
        }
        MissionManager missionManager=ServletUtils.getMissionManager(getServletContext());
        Mission mission= ServletUtils.getMissionManager(getServletContext()).getMissionsMap().get(missionName);
        if(!missionManager.isMissionExist(missionName)){
            out.println("mission name does not exist!");
            return;
        }
        mission.updateThatTargetRunningStateIsFinishedInTaskFromPostman(runningResult,targetName);
        out.println("succeeded update the server");
    }
}
