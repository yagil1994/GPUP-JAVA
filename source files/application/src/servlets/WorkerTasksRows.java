package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.Mission;
import mission.MissionManager;
import mission.WorkerPendingMissionsTableViewDtoWithOutCheckBox;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class WorkerTasksRows extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String workerName = request.getParameter(Constants.USERNAME);
        Gson gson = new Gson();
        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        Map<String, Mission> missionMap = missionManager.getMissionsMap();
        WorkerPendingMissionsTableViewDtoWithOutCheckBox[] allTasksInfo = new WorkerPendingMissionsTableViewDtoWithOutCheckBox[missionMap.size()];
        int i = 0;
        for (Map.Entry<String, Mission> e : missionMap.entrySet()) {
            allTasksInfo[i] = e.getValue().getWorkerPendingMissionsTableViewDto(workerName);
            i++;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        String json = gson.toJson(allTasksInfo);
        out.println(json);
        out.flush();
    }
}
