package utils;

import chat.ChatManager;
import constants.Constants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import manager.MediatorsManager;
import mission.MissionManager;
import users.UserManager;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String MEDIATORS_MANAGER_ATTRIBUTE_NAME = "mediatorsManager";
    private static final String MISSION_MANAGER_ATTRIBUTE_NAME = "MissionManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
    public static final int INT_PARAMETER_ERROR = Integer.MIN_VALUE;

    private static final Object userManagerLock = new Object();
    private static final Object mediatorsManagerLock = new Object();
    private static final Object missionManagerLock = new Object();
    private static final Object chatManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {
        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static MediatorsManager getMediatorsManager(ServletContext servletContext) {
        synchronized (mediatorsManagerLock) {
            if (servletContext.getAttribute(MEDIATORS_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(MEDIATORS_MANAGER_ATTRIBUTE_NAME, new MediatorsManager());
            }
        }
        return (MediatorsManager) servletContext.getAttribute(MEDIATORS_MANAGER_ATTRIBUTE_NAME);
    }

    public static MissionManager getMissionManager(ServletContext servletContext) {
        synchronized (missionManagerLock) {
            if (servletContext.getAttribute(MISSION_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(MISSION_MANAGER_ATTRIBUTE_NAME, new MissionManager());
            }
        }
        return (MissionManager) servletContext.getAttribute(MISSION_MANAGER_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }
}
