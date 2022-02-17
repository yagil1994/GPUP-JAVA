package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mission.Mission;
import output.UpdateTargetStatusDuringTaskDto;
import utils.ServletUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UpdateAfterTargetDidTask extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();
        BufferedReader streamReader;
        StringBuilder responseStrBuilder;
        String jsonTargetStatusDuringTaskDto=null;
        UpdateTargetStatusDuringTaskDto dto=null;
        Gson gson;
        for (Part part : parts) {
            InputStream in= part.getInputStream();
            streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if(part.getName().equals("jsonTargetStatusDuringTaskDto")){
                jsonTargetStatusDuringTaskDto= responseStrBuilder.toString();
                gson=new Gson();
                dto = gson.fromJson(jsonTargetStatusDuringTaskDto, UpdateTargetStatusDuringTaskDto.class);
            }
        }
       Mission mission= ServletUtils.getMissionManager(getServletContext()).getMissionsMap().get(dto.getMissionName());
        mission.updateThatTargetRunningStateIsFinishedInTask(dto);
    }
}
