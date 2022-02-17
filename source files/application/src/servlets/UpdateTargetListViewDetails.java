package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import mission.MissionManager;
import mission.TargetListViewDetails;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class UpdateTargetListViewDetails extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        MissionManager missionsManager = ServletUtils.getMissionManager((getServletContext()));
        Map<String, Mission> missionsMap = missionsManager.getMissionsMap();
        String missionName = request.getParameter(Constants.MISSION_NAME);
        String targetName=request.getParameter(Constants.TARGET_NAME);
        try (
                PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Mission mission = missionsMap.get(missionName);
            TargetListViewDetails details =mission.getTargetListViewDetails(targetName);
            String json = gson.toJson(details);
            out.println(json);
            out.flush();
        }
    }
}
