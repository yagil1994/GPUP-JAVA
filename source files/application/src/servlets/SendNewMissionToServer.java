package servlets;

import com.google.gson.Gson;
import constants.Constants;
import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import manager.Mediator;
import manager.MediatorsManager;
import mission.AdminTotalInfoForMissionWithOutParams;
import mission.Mission;
import mission.MissionManager;
import utils.ServletUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class SendNewMissionToServer extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();
        String jsonAdminTotalInfoForMissionWithOutParams,jsonTaskInput;
        BufferedReader streamReader;
        StringBuilder responseStrBuilder;
        AdminTotalInfoForMissionWithOutParams adminTotalInfoForMissionWithOutParams=null;
        Mediator.TaskType taskType=Mediator.TaskType.SIMULATION;
        String taskTypeStr="Simulation";
        TaskInputDto taskInput=null;
        Gson gson;

        for (Part part : parts) {
            InputStream in= part.getInputStream();
            streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if(part.getName().equals("jsonAdminTotalInfoForMissionWithOutParams")){
                jsonAdminTotalInfoForMissionWithOutParams= responseStrBuilder.toString();
                 gson=new Gson();
                adminTotalInfoForMissionWithOutParams = gson.fromJson(jsonAdminTotalInfoForMissionWithOutParams, AdminTotalInfoForMissionWithOutParams.class);
                if(adminTotalInfoForMissionWithOutParams.getTaskType().equals("Compilation")){
                    taskType=Mediator.TaskType.COMPILATION;
                    taskTypeStr="Compilation";
                }
            }
            else if(part.getName().equals("jsonTaskInput")){
                jsonTaskInput= responseStrBuilder.toString();
                 gson=new Gson();
                 if(taskType.equals(Mediator.TaskType.SIMULATION)) {
                     taskInput = gson.fromJson(jsonTaskInput, SimulationTaskInputDto.class);
                 }
                 else{//compilation
                     taskInput = gson.fromJson(jsonTaskInput, CompilationTaskInputDto.class);
                 }
            }
        }
        String missionName=adminTotalInfoForMissionWithOutParams.getMissionName();
        MissionManager missionManager=ServletUtils.getMissionManager(getServletContext());
        if(missionManager.isMissionExist(missionName)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        MediatorsManager mediatorsManager= ServletUtils.getMediatorsManager(getServletContext());
        Map<String,Mediator> mediatorMap= mediatorsManager.getMediatorsMap();
        Mediator med=mediatorMap.get(adminTotalInfoForMissionWithOutParams.getGraphName());
        med.createGraphForTask(taskInput,adminTotalInfoForMissionWithOutParams,false,null);
        Mission newMission=new Mission(adminTotalInfoForMissionWithOutParams,taskInput,med);
        updateMissionBeforeAddition(med,adminTotalInfoForMissionWithOutParams,newMission,taskTypeStr);
        missionManager.addMission(adminTotalInfoForMissionWithOutParams.getMissionName(),newMission);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    private void updateMissionBeforeAddition(Mediator med,AdminTotalInfoForMissionWithOutParams adminTotalInfoForMissionWithOutParams,
                                             Mission newMission,String taskTypeStr)
    {
        Integer leavesAmount=med.getGraphForTaskOnly(adminTotalInfoForMissionWithOutParams.getMissionName()).getLeavesAmount();
        Integer independentsAmount=med.getGraphForTaskOnly(adminTotalInfoForMissionWithOutParams.getMissionName()).getIndependentAmount();
        Integer middlesAmount=med.getGraphForTaskOnly(adminTotalInfoForMissionWithOutParams.getMissionName()).getMiddlesAmount();
        Integer rootsAmount=med.getGraphForTaskOnly(adminTotalInfoForMissionWithOutParams.getMissionName()).getRootsAmount();
        Integer totalTargetsAmount=med.getGraphForTaskOnly(adminTotalInfoForMissionWithOutParams.getMissionName()).getTargetsAmount();
        Integer priceForTarget =null;
        if(taskTypeStr.equals("Simulation")){
            priceForTarget=Integer.parseInt(med.getPricePerTargetInSimulation());
        }
        else{
            priceForTarget=Integer.parseInt(med.getPricePerTargetInCompilation());
        }
        int totalTaskPrice =priceForTarget*totalTargetsAmount;
        int amountOfWorkersOnTask =0;
        newMission.setLeavesAmount(leavesAmount.toString());
        newMission.setIndependentsAmount(independentsAmount.toString());
        newMission.setMiddlesAmount(middlesAmount.toString());
        newMission.setRootsAmount(rootsAmount.toString());
        newMission.setTotalTargetsAmount(totalTargetsAmount.toString());
        newMission.setPriceForTarget(priceForTarget.toString());
        newMission.setTotalTaskPrice(Integer.toString(totalTaskPrice));
        //newMission.setAmountOfWorkersOnTask(Integer.toString(amountOfWorkersOnTask));
    }
}

