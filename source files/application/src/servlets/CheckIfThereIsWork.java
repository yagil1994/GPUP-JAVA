package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import manager.Mediator;
import mission.Mission;
import mission.MissionManager;
import mission.MissionNameAndDto;
import mission.TargetNameAndExtraInfoDto;
import utils.ServletUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class CheckIfThereIsWork extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();
        BufferedReader streamReader;
        StringBuilder responseStrBuilder;
        String workerNameJson,allMissionsThatTheWorkerRegisteredArrJson,jsonAllMissionsThatTheWorkerRegisteredJson,
                amountOfAvailableThreadsRightNowJson,workerName=null,amountOfAvailableThreadsRightNow=null;
        String[] allMissionsThatTheWorkerRegisteredArr=null;
        Integer amountOfAvailableThreadsRightNowInteger=null;
        Gson gson;

        for (Part part : parts) {
            InputStream in= part.getInputStream();
            streamReader = new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8));
            responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if(part.getName().equals("jsonAllMissionsThatTheWorkerRegistered")){
                allMissionsThatTheWorkerRegisteredArrJson= responseStrBuilder.toString();
                gson=new Gson();
                allMissionsThatTheWorkerRegisteredArr = gson.fromJson(allMissionsThatTheWorkerRegisteredArrJson, String[].class);
            }
            else if(part.getName().equals("username")){
                workerNameJson= responseStrBuilder.toString();
                gson=new Gson();
                workerName = gson.fromJson(workerNameJson, String.class);
            }
            else if(part.getName().equals("amountOfAvailableThreadsRightNow")){
                amountOfAvailableThreadsRightNowJson= responseStrBuilder.toString();
                gson=new Gson();
                amountOfAvailableThreadsRightNow = gson.fromJson(amountOfAvailableThreadsRightNowJson, String.class);
            }
        }//
        amountOfAvailableThreadsRightNowInteger=Integer.valueOf(amountOfAvailableThreadsRightNow);
        MissionManager missionManager=ServletUtils.getMissionManager(getServletContext());
        Map<String,Mission> missionMap= missionManager.getMissionsMap();// targetName,extraInfo,missionFolder;
        Map<String,List<TargetNameAndExtraInfoDto>> availableTargetsToWorkOnMap=new HashMap<>();//{key=mission name,value=target name}
        initAvailableTargetsToWorkOnMap(availableTargetsToWorkOnMap,allMissionsThatTheWorkerRegisteredArr);
        for(String missionName:allMissionsThatTheWorkerRegisteredArr){
           Mission currentMission =missionMap.get(missionName);
           if(currentMission.isWorkerRegistered(workerName)&&( currentMission.getMissionStatus().equals(Mission.MISSION_STATUS.PLAYING)||
                   currentMission.getMissionStatus().equals(Mission.MISSION_STATUS.RESUMED)))
           {
               List<TargetNameAndExtraInfoDto> targetsAvailableForThisSpecificMission=availableTargetsToWorkOnMap.get(missionName);
               amountOfAvailableThreadsRightNowInteger= currentMission.getAvailableTargetsToWork(amountOfAvailableThreadsRightNowInteger,targetsAvailableForThisSpecificMission);
               if(amountOfAvailableThreadsRightNowInteger<=0){
                   break;
               }
           }
        }
        PrintWriter out = response.getWriter();
        gson = new Gson();
        Map<String,TargetNameAndExtraInfoDto[]> result=convertTheMap(availableTargetsToWorkOnMap);
        String json = gson.toJson(result);
        out.println(json);
        out.flush();
    }
    private Map<String,TargetNameAndExtraInfoDto[]> convertTheMap(Map<String,List<TargetNameAndExtraInfoDto>> convertIt){
        Map<String,TargetNameAndExtraInfoDto[]> res=new HashMap<>();
        for(Map.Entry<String,List<TargetNameAndExtraInfoDto>> e:convertIt.entrySet()){
            List<TargetNameAndExtraInfoDto> tmpList=e.getValue();
            TargetNameAndExtraInfoDto[] tmp=new TargetNameAndExtraInfoDto[tmpList.size()];
            int i=0;
            for(TargetNameAndExtraInfoDto t:tmpList){
                tmp[i]=t;
                i++;
            }
            res.put(e.getKey(),tmp);
        }
        return res;
    }
    private void initAvailableTargetsToWorkOnMap(Map<String,List<TargetNameAndExtraInfoDto>> availableTargetsToWorkOnMap, String[] allMissionsThatTheWorkerRegisteredArr){
        for(String missionName:allMissionsThatTheWorkerRegisteredArr){
            List<TargetNameAndExtraInfoDto> targetsToRunOnTask=new ArrayList<>();
            availableTargetsToWorkOnMap.put(missionName,targetsToRunOnTask);
        }
    }
}
