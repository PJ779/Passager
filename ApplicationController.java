package application.controller;

import application.controller.ui.ISceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationController {
    private static final String APPLICATION_TITLE = "Passager";

    private static final ApplicationController singleInstance = new ApplicationController();

    public enum Scene {
        LOGIN("/application/view/LoginScene.fxml"),
        MAIN("/application/view/MainScene.fxml"),
        SIGNUP("/application/view/SignupScene.fxml"),
        RESET_MASTER_PASSWORD("/application/view/ResetMasterPasswordScene.fxml"),
        ADD_ACCOUNT("/application/view/AddAccountScene.fxml"),
        PASSWORD_GENERATOR("/application/view/PasswordGeneratorPopUpScene.fxml");

        private final javafx.scene.Scene fxScene;
        private final ISceneController controller;

        Scene(String fxmlPath) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                fxScene = new javafx.scene.Scene(loader.load());
                controller = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Stage stage;

    public static class PassagerApplication extends Application {
        @Override
        public void start(Stage stage) {
            ApplicationController applicationController = ApplicationController.getInstance();
            applicationController.stage = stage;
            applicationController.startApplication();
        }
    }

    private ApplicationController() {
    }

    public static ApplicationController getInstance() {
        return singleInstance;
    }

    private void startApplication() {
        stage.setTitle(APPLICATION_TITLE);
        setScene(Scene.LOGIN);
        stage.show();
    }

    public void run() {
        Application.launch(PassagerApplication.class);
    }

    public void setScene(Scene scene) {
        scene.controller.onSceneEnter();
        stage.setScene(scene.fxScene);
    }

    public ISceneController getSceneController(Scene scene) {
        return scene.controller;
    }
}
