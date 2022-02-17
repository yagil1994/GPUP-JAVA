package servlets;

import bonus.ChatAndVersionDto;
import chat.ChatManager;
import bonus.SingleChatEntry;
import com.google.gson.Gson;
import constants.Constants;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class UpdateChat extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ChatManager chatManager = ServletUtils.getChatManager(request.getServletContext());
        int chatVersionSentFromUser = ServletUtils.getIntParameter(request, Constants.CHAT_VERSION_PARAMETER);
        int chatManagerVersion = 0;
        List<SingleChatEntry> chatEntries=null;
        synchronized (request.getServletContext()) {
            chatManagerVersion = chatManager.getVersion();
            chatEntries = chatManager.getChatEntries(chatVersionSentFromUser);
        }

        ChatAndVersionDto cav = new ChatAndVersionDto(chatEntries, chatManagerVersion);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(cav);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

}
