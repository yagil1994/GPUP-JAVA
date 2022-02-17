package component.login;
import component.supercontroller.AdminSuperController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;


public class AdminLoginController {

    @FXML private TextField userNameTextField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;

    private final StringProperty errorMessageProperty;
    private AdminSuperController sController;
    private Stage primaryStage;
    private Scene sControllerScene;

    public AdminLoginController() {
        errorMessageProperty = new SimpleStringProperty("");
    }

    @FXML public void initialize() {
        loginErrorLabel.textProperty().bind(errorMessageProperty);
        userNameTextField.clear();
    }

    @FXML private void loginButtonClicked(ActionEvent event) {
        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("role", "admin")
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                }
                else {
                    Platform.runLater(() -> {
                        sController.updateDashBoard(userName);
                        primaryStage.setScene(sControllerScene);
                        primaryStage.show();
                    });
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

    private void loadSuperScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL superScreenUrl = getClass().getResource("/component/supercontroller/AdminSuperController.fxml");
        fxmlLoader.setLocation(superScreenUrl);
        try {
            Parent root = fxmlLoader.load(superScreenUrl.openStream());
            sController=fxmlLoader.getController();
            sController.setPrimaryStage(primaryStage);
            primaryStage.setTitle("G.P.U.P-admin");
            sControllerScene = new Scene(root);
            primaryStage.setMinHeight(300f);
            primaryStage.setMinWidth(400f);
        }
        catch (IOException ignore){ignore.printStackTrace();}
    }
    @FXML  private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }
    @FXML private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

    public void setPrimaryStageAndLoadInTheBackgroundSuperScreen(Stage primaryStageIn){
        primaryStage=primaryStageIn;
        loadSuperScreen();
    }
}
