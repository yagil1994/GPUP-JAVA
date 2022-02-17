package main;

import component.login.WorkerLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class WorkerMain extends Application{
    @Override public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("WorkerLogin.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        WorkerLoginController workerLoginController = fxmlLoader.getController();
        workerLoginController.setPrimaryStageAndLoadInTheBackgroundSuperScreen(primaryStage);
        primaryStage.setTitle("Worker");
        Scene scene = new Scene(root);
        primaryStage.setMinHeight(300f);
        primaryStage.setMinWidth(400f);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
