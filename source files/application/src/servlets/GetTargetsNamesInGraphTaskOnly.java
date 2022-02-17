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
import java.util.Set;

public class GetTargetsNamesInGraphTaskOnly extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            MediatorsManager mediatorsManager = ServletUtils.getMediatorsManager(getServletContext());
            Map<String, Mediator> mediatorMap = mediatorsManager.getMediatorsMap();
            String theGraphName=request.getParameter(Constants.GRAPH_NAME);
            String lastTaskName=request.getParameter(Constants.LAST_TASK_NAME);
            Mediator med= mediatorMap.get(theGraphName);
            Set<String> r= med.getAllTargetsNamesInSetFromGraphTaskOnly(lastTaskName);
            String[] res=new String[r.size()];
            int i=0;
            for(String s:r){
                res[i]=s;
                i++;
            }
            Gson gson = new Gson();
            String json = gson.toJson(res);
            out.println(json);
            out.flush();
        }
    }

}
