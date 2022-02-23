package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mission.*;
import utils.ServletUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RegisterWorkerToTaskFromPostman extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String missionName=request.getParameter(Constants.MISSION_NAME);
        String workerName = request.getParameter(Constants.USERNAME);
        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        PrintWriter out = response.getWriter();
        if(!missionManager.isMissionExist(missionName)){
            out.println("mission name does not exist!");
            return;
        }
        if(!ServletUtils.getUserManager(getServletContext()).isUserExists(workerName)){
            out.println("user name does not exist!");
            return;
        }
        List<MissionNameAndDto> DTOSForExecutionList=new ArrayList<>();
        Mission currentMission= missionManager.getMissionsMap().get(missionName);
        currentMission.registerNewWorkerToTheMission(workerName);
        DTOSForExecutionList.add(currentMission.getDtoPackForTargetExecution());

        MissionNameAndDto[] DTOSForExecutionArr=new MissionNameAndDto[DTOSForExecutionList.size()];
        int i=0;
        for(MissionNameAndDto missionDto:DTOSForExecutionList){
            DTOSForExecutionArr[i]=missionDto;
            printGeneralDetailsAboutTheMission(DTOSForExecutionArr[i],out);
            i++;
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
    private void printGeneralDetailsAboutTheMission(MissionNameAndDto dto, PrintWriter out){
        out.println("succeeded registration to the mission:"+dto.getMissionName()+"\n");
        String missionType=dto.getMissionType();
         out.println("mission name: "+dto.getMissionName()+"\n");
         out.println("mission type: "+missionType+"\n");
         if(missionType.equals("Simulation")){
             SimulationForWorkerDto simulationForWorkerDto= dto.getSimulationForWorkerDto();
             out.println("mission folder: "+simulationForWorkerDto.getMissionFolder()+"\n");
             out.println("process time: "+simulationForWorkerDto.getProcessTime()+"\n");
             out.println("probability of success: "+simulationForWorkerDto.getProbabilityOfSuccess()+"\n");
             out.println("probability of warning: "+simulationForWorkerDto.getProbabilityOfWarning()+"\n");
         }
         else{
             CompilationForWorkerDto compilationTaskInputDto=dto.getCompilationTaskInputDto();
             out.println("mission folder: "+compilationTaskInputDto.getMissionFolder()+"\n");
             out.println("source java files folder: "+compilationTaskInputDto.getSrc()+"\n");
             out.println("destination compiled files folder: "+compilationTaskInputDto.getDest()+"\n");
         }
    }
}


