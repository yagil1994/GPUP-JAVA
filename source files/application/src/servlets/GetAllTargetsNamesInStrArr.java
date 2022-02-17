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

public class GetAllTargetsNamesInStrArr extends HttpServlet{
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            try (PrintWriter out = response.getWriter()) {
                response.setContentType("application/json");
                MediatorsManager mediatorsManager = ServletUtils.getMediatorsManager(getServletContext());
                Map<String, Mediator> mediatorMap = mediatorsManager.getMediatorsMap();
                String theGraphName=request.getParameter(Constants.GRAPH_NAME);
                Mediator med= mediatorMap.get(theGraphName);
                Set<String> allTargetNamesInSet= med.getAllTargetsNamesInSetFromGraph();
                Gson gson = new Gson();
                String[] res=new String[allTargetNamesInSet.size()];
                int i=0;
                for(String s:allTargetNamesInSet){
                    res[i]=s;
                    i++;
                }
                String json = gson.toJson(res);
                out.println(json);
                out.flush();
            }
        }
}
