package component.supercontroller;

import chat.ChatRoomMainController;
import component.dashboard.WorkerDashboardTabController;
import component.task.TaskTabContentController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import mini.engine.WorkerThreadPoolManager;
import mini.engine.innerEngine;

import java.net.URL;

public class WorkerSuperController {
    private Stage primaryStage;
    private String workerUserName;
    private WorkerThreadPoolManager workerThreadPoolManager;
    private innerEngine inner;
    private Scene chatScene;
    private ChatRoomMainController chatRoomMainController;

    @FXML private ScrollPane workerDashboardTabContent;
    @FXML private WorkerDashboardTabController workerDashboardTabContentController;

    @FXML private ScrollPane taskTabContent;
    @FXML private TaskTabContentController taskTabContentController;

    public WorkerSuperController(){
       workerThreadPoolManager=null;
       workerUserName=null;
       inner=new innerEngine();
   }

    @FXML public void initialize() {
        if (workerDashboardTabContentController != null && taskTabContentController != null) {
            workerDashboardTabContentController.setWorkerSuperController(this);
            taskTabContentController.setWorkerSuperController(this);
        }
        initChat();
    }
    private void initChat() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("chat.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent root = fxmlLoader.load(url.openStream());
            chatRoomMainController = fxmlLoader.getController();
            chatScene = new Scene(root, 700, 600);
            chatRoomMainController.setChatSceneAndUserName(chatScene);
        }
        catch (Exception exception){}
    }
    public void openChat(){
        chatRoomMainController.showChat();
    }
    public Scene getChatScene(){return chatScene;}

    public String getWorkerUserName(){return workerUserName;}
    public Integer getAmountOfTotalThreadsInPool() {return workerThreadPoolManager.getAmountOfTotalThreadsInPool();}
    public Integer getAmountOfBusyThreads() {return workerThreadPoolManager.getAmountOfBusyThreads();}
    public Integer getAmountOfAvailableThreads() {return workerThreadPoolManager.getAmountOfAvailableThreads();}
    public WorkerThreadPoolManager getWorkerThreadPoolManager(){return workerThreadPoolManager;}

    public void pauseMission(String missionName){
        inner.pauseMission(missionName);
    }
    public void resumeMission(String missionName){
        inner.resumeMission(missionName);
    }
    public Boolean isMissionPaused(String missionName){
        return inner.isMissionPaused(missionName);
    }

    public Boolean isMissionExist(String missionName){
        return inner.doesMissionExist(missionName);
    }

    public void setPrimaryStage(Stage primaryStageIn) {
        primaryStage = primaryStageIn;
    }

    public void updateUserNameAndRoleAndThreads(String userName, double threadAmountIn){
        workerUserName = userName;
        workerThreadPoolManager= new WorkerThreadPoolManager((int)threadAmountIn);
        workerDashboardTabContentController.updateWorkerUserNameLabel(workerUserName);
        inner.addWorkerThreadPoolToContainer(workerThreadPoolManager);
        workerDashboardTabContentController.startUsersTableViewRefresher();
        workerDashboardTabContentController.startWorkerPendingMissionsTableViewRefresher(workerUserName);
        taskTabContentController.updateAmountOfTreads((int)threadAmountIn);
        chatRoomMainController.setUserName(userName);
    }

    public void startUpdatingTablesAtTask(){
        taskTabContentController.startProcessTableViewRefresher();
        taskTabContentController.startGeneralInfoTableViewRefresher();
    }

    public innerEngine getInner(){return inner;}
}
