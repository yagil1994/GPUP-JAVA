package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.AdminPendingMissionsTableViewDtoWithoutRadioButton;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AdminTasksRows extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
            Map<String, Mission> missionMap = missionManager.getMissionsMap();
            AdminPendingMissionsTableViewDtoWithoutRadioButton[] allTasksInfo = new AdminPendingMissionsTableViewDtoWithoutRadioButton[missionMap.size()];
            int i = 0;
            for (Map.Entry<String, Mission> e : missionMap.entrySet()) {
                allTasksInfo[i]=e.getValue().getAdminPendingMissionsTableViewDto();
                i++;
            }
            String json = gson.toJson(allTasksInfo);
            out.println(json);
            out.flush();
        }
    }
}
