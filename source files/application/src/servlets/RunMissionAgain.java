package servlets;

import com.google.gson.Gson;
import constants.Constants;
import input.task.TaskInputDto;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.Mediator;
import mission.*;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class RunMissionAgain extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String oldMissionName = request.getParameter(Constants.MISSION_NAME);
        String runMissionAgainType = request.getParameter(Constants.RUN_MISSION_AGAIN_TYPE);
        boolean runTaskAgainWithIncrement=false;
        if(runMissionAgainType.equals("RunTaskAgainWithIncrement")){
            runTaskAgainWithIncrement=true;
        }
        Gson gson = new Gson();
        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        Map<String, Mission> missionMap = missionManager.getMissionsMap();
        Mission oldMission= missionMap.get(oldMissionName);
        String graphName= oldMission.getGraphName();
        AdminTotalInfoForMissionWithOutParams adminTotalInfoForMissionWithOutParams= oldMission.getCopyOfDtoFromTheAdmin();
        TaskInputDto taskInput=oldMission.getCopyOfParametersFromAdminDto();
        taskInput.setScratch(false);
        Mediator med=oldMission.getMed();
        Integer newGenerationNumber= oldMission.addAndGetGenerationCounter();
        String newMissionName=oldMissionName+newGenerationNumber.toString();
        med.createGraphForTask(taskInput,adminTotalInfoForMissionWithOutParams,runTaskAgainWithIncrement,newMissionName);//copyMaps..we have it as string
        Mission newMission=new Mission(oldMission,newMissionName);
        updateMissionBeforeAddition(med,adminTotalInfoForMissionWithOutParams,newMission,adminTotalInfoForMissionWithOutParams.getTaskType());
        missionManager.addMission(newMissionName,newMission);
        GraphNameAndNewMissionNameDto res=new GraphNameAndNewMissionNameDto(graphName,newMission.getMissionName());
        String json = gson.toJson(res);
        out.println(json);
        out.flush();
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
    }
}
