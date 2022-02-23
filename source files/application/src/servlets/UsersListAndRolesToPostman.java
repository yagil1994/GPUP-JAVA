package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;
import java.io.IOException;
import java.util.Map;

public class UsersListAndRolesToPostman extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Map<String, String> usersList = userManager.getUsers();
        for (Map.Entry<String, String> user : usersList.entrySet()) {
            response.getWriter().print("Username:" + user.getKey() + " " + " role:" + user.getValue() + "\n");
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
