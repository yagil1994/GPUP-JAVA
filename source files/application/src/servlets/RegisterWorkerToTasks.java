package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mission.Mission;
import mission.MissionManager;
import mission.MissionNameAndDto;
import utils.ServletUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class RegisterWorkerToTasks extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();
        BufferedReader streamReader;
        StringBuilder responseStrBuilder;
        String workerNameJson, allSelectedTasksNamesInArrJson, workerName = null;
        String[] allSelectedTasksNamesInArr = null;
        Gson gson;

        for (Part part : parts) {
            InputStream in = part.getInputStream();
            streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if (part.getName().equals("jsonAllSelectedTasksNamesInArr")) {
                allSelectedTasksNamesInArrJson = responseStrBuilder.toString();
                gson = new Gson();
                allSelectedTasksNamesInArr = gson.fromJson(allSelectedTasksNamesInArrJson, String[].class);
            } else if (part.getName().equals("username")) {
                workerNameJson = responseStrBuilder.toString();
                gson = new Gson();
                workerName = gson.fromJson(workerNameJson, String.class);
            }
        }
        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        List<MissionNameAndDto> DTOSForExecutionList=new ArrayList<>();
        for (String missionName : allSelectedTasksNamesInArr) {
            Mission currentMission= missionManager.getMissionsMap().get(missionName);
            currentMission.registerNewWorkerToTheMission(workerName);
            DTOSForExecutionList.add(currentMission.getDtoPackForTargetExecution());
        }
        MissionNameAndDto[] DTOSForExecutionArr=new MissionNameAndDto[DTOSForExecutionList.size()];
        int i=0;
        for(MissionNameAndDto missionDto:DTOSForExecutionList){
            DTOSForExecutionArr[i]=missionDto;
            i++;
        }
        gson = new Gson();
        String json = gson.toJson(DTOSForExecutionArr);
        PrintWriter out = response.getWriter();
        out.println(json);
        out.flush();
    }
}
