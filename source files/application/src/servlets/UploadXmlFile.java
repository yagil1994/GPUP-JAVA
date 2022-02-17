package servlets;

import constants.Constants;
import data.structure.Graph;
import generated.GPUPDescriptor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import manager.Mediator;
import manager.MediatorsManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;

import java.util.*;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadXmlFile extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part xmlFile = request.getParts().stream().findFirst().get();
            try {
                checkIfXmlFileIsValidAndIfValidAddGraph(xmlFile.getInputStream(),xmlFile.getName());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            catch (Exception e){
              response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               response.getWriter().println(e.getMessage());
               response.getWriter().flush();
            }
     }

    private void checkIfXmlFileIsValidAndIfValidAddGraph(InputStream inputStream, String userName) throws Exception {
        Mediator med = new Mediator();
        med.setUploadedBy(userName);
        med.loadFile(inputStream);
        MediatorsManager mediatorsManager = ServletUtils.getMediatorsManager(getServletContext());
        String graphName = med.getOriginalGraphName();
        if (mediatorsManager.isGraphExists(graphName)) {
            throw new Exception("The graph name already exists");
        }
       // med.updateGraphForTaskOnlyToNullAfterLoadXml(); //todo see how to change the way we use it, now we have map of mission its diffrent
        mediatorsManager.addGraph(graphName, med);
    }
}
