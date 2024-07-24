package application.controller.ui;

import java.io.IOException;

import application.controller.ApplicationController;
import application.data.DataManager;
import application.model.User;
import application.utils.PasswordUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupSceneController implements ISceneController {

    @FXML
    private TextField answer;

    @FXML
    private Button confirmButton;

    @FXML
    private PasswordField confirmation;

    @FXML
    private PasswordField password;

    @FXML
    private TextField question;

    @FXML
    private TextField username;

    @FXML
    private Label label;

    @Override
    public void onSceneEnter() {
        // Resets all the items that may carry over data from previous use.
        username.clear();
        password.clear();
        confirmation.clear();
        question.clear();
        answer.clear();
    }

    @FXML
    protected void onBackButtonClicked(ActionEvent event) {
        ApplicationController.getInstance().setScene(ApplicationController.Scene.LOGIN);  // transition into LogIn page
    }

    @FXML
    protected void onSignUpButtonClicked(ActionEvent event) {
        User user = new User();
        user.setLoginName(username.getText());
        user.setMasterPassword(PasswordUtils.encryptPassword(password.getText()));
        user.setSecurityQuestion(question.getText());
        user.setAnswer(answer.getText());

        // checks the status of textfield
        if (!username.getText().trim().isEmpty()
                && !password.getText().isEmpty() && !question.getText().isEmpty() &&
                !answer.getText().isEmpty() && !confirmation.getText().isEmpty()) {

            // checks the equality of two password field and sends alert.
            if (!password.getText().equals(confirmation.getText())) {
                System.out.println("Password doesn't match");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Password doesn't match");
                alert.show();

            } else {

                // checks the existence of the user
                if (!DataManager.userExists(user.getLoginName())) {
                    // Saves the user and transitions to LoginScene.
                    try {
                        DataManager.saveUser(user);
//         					this.username.clear();
//         					this.password.clear();
//         					this.question.clear();
//         					this.confirmation.clear();
//         					this.answer.clear();
                        ApplicationController.getInstance().setScene(ApplicationController.Scene.LOGIN);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                }
                // Alert user the username is already in use.
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Invalid username");
                    alert.show();

                }
            }

        } else {
            // Alert user that they left a field empty.
            System.out.println("Textfield is empty");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Field is empty");
            alert.show();
        }
    }


}
