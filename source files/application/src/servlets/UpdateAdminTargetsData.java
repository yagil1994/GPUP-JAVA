package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.AdminMissionDataUpdateDto;
import mission.Mission;
import mission.MissionManager;
import output.UpdateTargetStatusDuringTaskDto;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class UpdateAdminTargetsData extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        MissionManager missionsManager = ServletUtils.getMissionManager((getServletContext()));
        Map<String, Mission> missionsMap = missionsManager.getMissionsMap();
        String missionName=request.getParameter(Constants.MISSION_NAME);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            Mission mission=missionsMap.get(missionName);
            mission.requestToUpdateTargetData();
            List<UpdateTargetStatusDuringTaskDto> updateAdminTargetsDataList=mission.createUpdateTargetStatusDuringTaskDtoList();
            String json = gson.toJson(updateAdminTargetsDataList);
            out.println(json);
            out.flush();
        }
    }
}
