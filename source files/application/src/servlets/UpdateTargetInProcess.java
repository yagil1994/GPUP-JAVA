package servlets;

import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import utils.ServletUtils;
import java.io.IOException;

public class UpdateTargetInProcess extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String missionName=request.getParameter(Constants.MISSION_NAME);
        String targetName=request.getParameter(Constants.TARGET_NAME);
        Mission mission= ServletUtils.getMissionManager(getServletContext()).getMissionsMap().get(missionName);
        mission.updateThatTargetIsInProcess(targetName);
    }
}
