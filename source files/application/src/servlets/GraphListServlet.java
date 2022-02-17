package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.Mediator;
import manager.MediatorsManager;
import output.GraphInfoDto;
import row.AvailableGraphsTableViewRow;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class GraphListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            MediatorsManager mediatorsManager = ServletUtils.getMediatorsManager(getServletContext());
            Map<String,Mediator> mediatorMap = mediatorsManager.getMediatorsMap();
            AvailableGraphsTableViewRow[] allMedsInfo=new AvailableGraphsTableViewRow[mediatorMap.size()];
            int i=0;
            for(Map.Entry<String,Mediator> e:mediatorMap.entrySet()){
                allMedsInfo[i]=getAllMedInfo(e);
                i++;
            }
            String json=gson.toJson(allMedsInfo);
            out.println(json);
            out.flush();
        }
    }
    private AvailableGraphsTableViewRow getAllMedInfo(Map.Entry<String,Mediator> e){
        GraphInfoDto graphInfoDto= e.getValue().getGraphInfoDto();
        String graphNameCol=e.getKey();
        String uploadedByCol=e.getValue().getUploadedBy();
        String leavesCol=graphInfoDto.getLeavesAmount().toString();
        String independentsCol=graphInfoDto.getIndependentAmount().toString();
        String middlesCol=graphInfoDto.getMiddlesAmount().toString();
        String rootsCol=graphInfoDto.getRootsAmount().toString();
        String totalTargetsCol=graphInfoDto.getTargetsAmount().toString();
        String simulationCol=e.getValue().getPricePerTargetInSimulation();
        String compilationCol=e.getValue().getPricePerTargetInCompilation();
        if(simulationCol==null) {
            simulationCol="";
        }
        if(compilationCol==null) {
            compilationCol="";
        }
        return new AvailableGraphsTableViewRow(graphNameCol,uploadedByCol,leavesCol,independentsCol,middlesCol,rootsCol,
                totalTargetsCol,simulationCol, compilationCol);
    }
}
