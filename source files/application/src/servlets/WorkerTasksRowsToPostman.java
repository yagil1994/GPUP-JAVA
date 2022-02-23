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

public class WorkerTasksRowsToPostman extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String workerName = request.getParameter(Constants.USERNAME);
        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        Map<String, Mission> missionMap = missionManager.getMissionsMap();
        WorkerPendingMissionsTableViewDtoWithOutCheckBox[] allTasksInfo = new WorkerPendingMissionsTableViewDtoWithOutCheckBox[missionMap.size()];
        int i = 0;
        if(missionMap.isEmpty()){
            out.print("There aren't any missions\n");
            return;
        }
        for (Map.Entry<String, Mission> e : missionMap.entrySet()) {
            if(i==0){
                response.getWriter().print("The list of the missions to do: \n");
            }
            allTasksInfo[i] = e.getValue().getWorkerPendingMissionsTableViewDto(workerName);
            printMission(allTasksInfo[i],out);
            i++;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
    private void printMission(WorkerPendingMissionsTableViewDtoWithOutCheckBox missionToPrint,PrintWriter out){
        out.print("*******************************************************"+"\n");
        out.print("mission name:"+ missionToPrint.getMissionName()+"\n");
        out.print("created by:"+ missionToPrint.getCreatedBy()+"\n");
        out.print("task type:"+ missionToPrint.getTaskType()+"\n");
        out.print("leaves amount:"+ missionToPrint.getLeavesAmount()+"\n");
        out.print("leaves amount:"+ missionToPrint.getLeavesAmount()+"\n");
        out.print("independents amount:"+ missionToPrint.getIndependentsAmount()+"\n");
        out.print("middles amount:"+ missionToPrint.getMiddlesAmount()+"\n");
        out.print("roots amount:"+ missionToPrint.getRootsAmount()+"\n");
        out.print("total targets amount:"+ missionToPrint.getTotalTargetsAmount()+"\n");
        out.print("price for target:"+ missionToPrint.getPriceForTarget()+"\n");
        out.print("mission status:"+ missionToPrint.getMissionName()+"\n");
        out.print("amount of workers on task:"+ missionToPrint.getAmountOfWorkersOnTask()+"\n");
        out.print("is registered:"+ missionToPrint.getIsRegistered()+"\n");
        out.print("*******************************************************"+"\n");
    }
}
