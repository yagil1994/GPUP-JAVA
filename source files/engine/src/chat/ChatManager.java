package chat;

import bonus.SingleChatEntry;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {

    private final List<SingleChatEntry> chatDataList;

    public ChatManager() {
        chatDataList = new ArrayList<>();
    }

    public synchronized void addChatString(String message, String username) {
        chatDataList.add(new SingleChatEntry(message, username));
    }

    public synchronized List<SingleChatEntry> getChatEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > chatDataList.size()) {
            fromIndex = 0;
        }
        return chatDataList.subList(fromIndex, chatDataList.size());
    }

   synchronized public int getVersion() {
        return chatDataList.size();
    }
}