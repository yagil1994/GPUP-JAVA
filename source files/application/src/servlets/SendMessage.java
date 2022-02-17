package servlets;

import chat.ChatManager;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;


public class SendMessage extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String username = request.getParameter(Constants.USERNAME);
        String message=request.getParameter(Constants.USER_MESSAGE);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        if (message != null && !message.isEmpty()) {
            synchronized (getServletContext()) {
                chatManager.addChatString(message, username);
            }
        }
    }
}