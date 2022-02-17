package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class AskForTaskReport extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        MissionManager missionsManager = ServletUtils.getMissionManager((getServletContext()));
        Map<String, Mission> missionsMap = missionsManager.getMissionsMap();
        String missionName = request.getParameter(Constants.MISSION_NAME);
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Mission mission = missionsMap.get(missionName);
        List<String> taskReportList = mission.getTaskReportStringList();
        String json = gson.toJson(taskReportList);
        out.println(json);
        out.flush();
    }
}
