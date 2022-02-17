package servlets;

import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.Mediator;
import manager.MediatorsManager;
import output.TargetInfoForTableViewDto;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class TargetInfoForTableViewDtoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            MediatorsManager mediatorsManager = ServletUtils.getMediatorsManager(getServletContext());
            Map<String, Mediator> mediatorMap = mediatorsManager.getMediatorsMap();
            String theGraphName=request.getParameter(Constants.GRAPH_NAME);
            Mediator med= mediatorMap.get(theGraphName);
            Set<TargetInfoForTableViewDto> dto= med.getAllTargetsInfoForTableViewDtoFromGraph();
            TargetInfoForTableViewDto[] res=new TargetInfoForTableViewDto[dto.size()];
            int i=0;
            for(TargetInfoForTableViewDto t:dto){
                res[i]=t;
                i++;
            }
            Gson gson = new Gson();
            String json = gson.toJson(res);
            out.println(json);
            out.flush();
        }
    }
}
