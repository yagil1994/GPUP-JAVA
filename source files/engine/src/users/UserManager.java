package users;

import java.util.*;

public class UserManager {
    private final Map<String,String> usersMap;
    public UserManager() {
        usersMap = new HashMap<>();
    }
    public synchronized void addUser(String username, String roleFromSession) {
        usersMap.put(username,roleFromSession);
    }
    public synchronized void removeUser(String username) {
        usersMap.remove(username);
    }
    public synchronized Map<String,String> getUsers() {
        return Collections.unmodifiableMap(usersMap);
    }
    public boolean isUserExists(String username) {return usersMap.containsKey(username);}
}
