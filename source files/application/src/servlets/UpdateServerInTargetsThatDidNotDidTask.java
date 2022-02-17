package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mission.Mission;
import mission.TargetNameAndExtraInfoDto;
import output.UpdateTargetStatusDuringTaskDto;
import utils.ServletUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UpdateServerInTargetsThatDidNotDidTask extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();
        BufferedReader streamReader;
        StringBuilder responseStrBuilder;
        String jsonAllTheTargetsThatDidNotRun=null;
        Map<String,TargetNameAndExtraInfoDto[]> allTheTargetsThatDidNotRunMap=null;
        Gson gson;
        for (Part part : parts) {
            InputStream in= part.getInputStream();
            streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if(part.getName().equals("jsonAllTheTargetsThatDidNotRun")){
                jsonAllTheTargetsThatDidNotRun= responseStrBuilder.toString();
                gson=new Gson();
                Type type = new TypeToken< Map<String,TargetNameAndExtraInfoDto[]>>() { }.getType();
                allTheTargetsThatDidNotRunMap = gson.fromJson(jsonAllTheTargetsThatDidNotRun, type);
            }
        }
        Map<String,Mission> missionMap= ServletUtils.getMissionManager(getServletContext()).getMissionsMap();
        for(Map.Entry<String,TargetNameAndExtraInfoDto[]> t:allTheTargetsThatDidNotRunMap.entrySet()){
            List<TargetNameAndExtraInfoDto> tmp= new ArrayList<>(Arrays.asList(t.getValue()));
            missionMap.get(t.getKey()).updateTargetsThatDidNotRun(tmp);
        }
    }
}
