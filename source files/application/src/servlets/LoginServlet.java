package servlets;

import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsername(request);//chek if the user exists- returns null if not
        String roleFromSession = request.getParameter(Constants.ROLE);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());// return the user manager (one instance for all)

        if (usernameFromSession == null) { //user is not logged in yet
            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getOutputStream().print(errorMessage);
                    }
                    else {
                        //add the new user to the users list
                        userManager.addUser(usernameFromParameter,roleFromSession);
                        request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        }
        else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_OK);// todo what to do when two users are trying to log in with the same name
        }
    }

}
