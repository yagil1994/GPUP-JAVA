package servlets;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;


public class StartAppServlet implements ServletContextListener {

    @Override public void contextInitialized(ServletContextEvent servletContextEvent) {
       String workingDirectory = "c:\\gpup-working-dir";
        File f = new File(workingDirectory);
        if (!f.exists()) {
            boolean isFolderCreated = f.mkdir();
            if (!isFolderCreated) {
                throw new SecurityException("The computer had a problem with creating a folder in the current path:\n");
            }
        }
    }

}
