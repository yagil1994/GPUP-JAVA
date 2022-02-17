package chat;
import bonus.ChatAndVersionDto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mission.AdminPendingMissionsTableViewDtoWithoutRadioButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ChatRoomMainController {

    private Stage chatStage;
    private Scene chatScene;
    private String userName;
    private Timer chatTimer;
    private Integer currentChatVersion;
    private Boolean startedChatRefresher;

    @FXML private Label userNameLabel;
    @FXML private TextArea chatDisplayTextArea;
    @FXML private TextField messageTextField;

    public ChatRoomMainController(){
        chatStage = new Stage();
        chatStage.setTitle("Chat");
        chatStage.setMinHeight(600);
        chatStage.setMinWidth(600);
        userName=null;
        currentChatVersion=0;
        startedChatRefresher=false;
    }
    @FXML public void initialize() {}

    public void setChatSceneAndUserName(Scene sceneIn) {
        chatScene=sceneIn;
        chatStage.setScene(chatScene);
        chatStage.setResizable(false);
    }

    public void setUserName(String userNameIn){
        userName=userNameIn;
        userNameLabel.setText(userName);
    }

    @FXML private void SendMessage(){
        String message=messageTextField.getText();
        if(message==null||message.isEmpty()){
            return;  //todo check it
        }
      updateHttpLine(message);
      messageTextField.clear();
    }

    private void updateHttpLine(String message){
        String finalUrl = HttpUrl
                .parse(Constants.SEND_MESSAGE)
                .newBuilder()
                .addQueryParameter("userMessage", message)
                .addQueryParameter("username", userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

     public void showChat(){
        chatStage.show();
        if(!startedChatRefresher) {
            startedChatRefresher=true;
            startUpdateChatRefresher();
        }
     }

    public void startUpdateChatRefresher() {
     chatTimer = new Timer();
     class Helper extends TimerTask {

        public void run() {
            String finalUrl = HttpUrl
                    .parse(Constants.UPDATE_CHAT)
                    .newBuilder()
                    .addQueryParameter("chatVersion", String.valueOf(currentChatVersion))
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                  String rawBody = response.body().string();
                  Gson gson=new Gson();
                  Type type = new TypeToken<ChatAndVersionDto>() { }.getType();
                  ChatAndVersionDto chatLinesWithVersion = gson.fromJson(rawBody,type);
                    synchronized (this) {
                        Platform.runLater(()-> updateChatLines(chatLinesWithVersion));
                    }
                    Objects.requireNonNull(response.body()).close();
                }
            });
        }
    }

    TimerTask updateChat = new Helper();
    chatTimer.schedule(updateChat, Constants.FAST_REFRESH_RATE, Constants.FAST_REFRESH_RATE);
}


    private void updateChatLines(ChatAndVersionDto chatLinesWithVersion) {
        if (chatLinesWithVersion.getVersion() != currentChatVersion) {
            String deltaChatLines = chatLinesWithVersion
                    .getEntries()
                    .stream()
                    .map(singleChatLine -> {
                        long time = singleChatLine.getTime();
                        return String.format(Constants.CHAT_LINE_FORMATTING, time, time, time, singleChatLine.getUsername(), singleChatLine.getChatString());
                    }).collect(Collectors.joining());
            currentChatVersion=chatLinesWithVersion.getVersion();chatDisplayTextArea.appendText(deltaChatLines);
        }
    }
}
