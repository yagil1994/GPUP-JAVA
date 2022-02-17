package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.Mediator;
import manager.MediatorsManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class AllDependenciesOfTargetDto extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            MediatorsManager mediatorsManager = ServletUtils.getMediatorsManager(getServletContext());
            Map<String, Mediator> mediatorMap = mediatorsManager.getMediatorsMap();
            String theGraphName=request.getParameter(Constants.GRAPH_NAME);
            String theTargetName=request.getParameter(Constants.TARGET_NAME);
            Mediator med= mediatorMap.get(theGraphName);
            output.AllDependenciesOfTargetDto dto= med.getAllDependenciesOfTargetDtoFromGraph(theTargetName);
            //todo we have map inside
            Gson gson = new Gson();
            String json = gson.toJson(dto);
            out.println(json);
            out.flush();
        }
    }

}
