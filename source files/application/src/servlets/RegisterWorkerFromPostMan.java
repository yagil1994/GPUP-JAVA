package servlets;

import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

public class RegisterWorkerFromPostMan extends HttpServlet {

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String threadsAmount = req.getParameter(Constants.THREADS_AMOUNT);
        Integer threadsAmountInteger=null;
        try {
             threadsAmountInteger = Integer.parseInt(threadsAmount);
        }
     catch (NumberFormatException nfe) {
        String error = "Error ! one of the arguments is not a valid number";
        resp.setStatus(800);
         resp.getOutputStream().print(error);
         return;
    }
         if(!(threadsAmountInteger >= 1 && threadsAmountInteger <= 5)){
             resp.setStatus(801);
             resp.getOutputStream().print("invalid amount of threads");
             return;
         }
        String usernameFromSession = SessionUtils.getUsername(req);//chek if the user exists- returns null if not
        UserManager userManager = ServletUtils.getUserManager(getServletContext());// return the user manager (one instance for all)
        if (usernameFromSession == null) { //user is not logged in yet
            String usernameFromParameter = req.getParameter(Constants.USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.getOutputStream().print(errorMessage);
                    }
                    else {
                        //add the new user to the users list
                        userManager.addUser(usernameFromParameter,"worker");
                        req.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getOutputStream().print("Succeeded to register as "+req.getParameter(Constants.USERNAME)+" !");
                    }
                }
            }
        }
        else {
            //user is already logged in
            resp.setStatus(HttpServletResponse.SC_OK);
        } 
    }
}

