package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.AdminMissionDataUpdateDto;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class UpdateAdminMissionData extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        MissionManager missionsManager = ServletUtils.getMissionManager((getServletContext()));
        Map<String, Mission> missionsMap = missionsManager.getMissionsMap();
        String missionName=request.getParameter(Constants.MISSION_NAME);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Mission mission=missionsMap.get(missionName);

            Integer registeredWorkers = mission.getAmountOfWorkersOnTaskAsInteger();
            Long runningTargets = mission.getRunningTargetsAmount();
            Long availableTargets = mission.getAvailableTargetsToRunAmount();
            Set<String> processedTargets=mission.getProcessedTargets();//private Boolean allTargetsSucceed
            Boolean allTargetsSucceed= mission.areAllTargetsSucceed();
            Integer amountOfRunningTargetsOnTask=mission.getAmountOfRunningTargetsOnTask();
            AdminMissionDataUpdateDto dto =new AdminMissionDataUpdateDto(registeredWorkers,runningTargets,availableTargets,processedTargets,allTargetsSucceed,amountOfRunningTargetsOnTask);
            String json = gson.toJson(dto);
            out.println(json);
            out.flush();
        }
    }
}
