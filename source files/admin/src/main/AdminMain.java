package main;

import component.login.AdminLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AdminMain extends Application {
    @Override public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("AdminMain.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        AdminLoginController adminLoginController = fxmlLoader.getController();
        adminLoginController.setPrimaryStageAndLoadInTheBackgroundSuperScreen(primaryStage);
        primaryStage.setTitle("Admin");
        Scene scene = new Scene(root);
        primaryStage.setMinHeight(300f);
        primaryStage.setMinWidth(400f);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
