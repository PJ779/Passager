package application.controller.ui;

import application.controller.ApplicationController;
import application.utils.PasswordUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;


public class PasswordGeneratorController implements ISceneController {
    @FXML
    private TextField genPassword;

    @FXML
    private TextField maxLength;

    @FXML
    private TextField minLength;

    @FXML
    private TextField specialCharacter;

    @FXML
    private TextField upperCaseLetter;

    @Override
    public void onSceneEnter() {
        minLength.clear();
        maxLength.clear();
        specialCharacter.clear();
        upperCaseLetter.clear();
        genPassword.clear();
    }


    @FXML
    void onCancelButtonClicked(ActionEvent event) {
        ApplicationController applicationController = ApplicationController.getInstance();
        ((AddAccountSceneController) applicationController.getSceneController(ApplicationController.Scene.ADD_ACCOUNT))
                .setPasswordInput(null);
        applicationController.setScene(ApplicationController.Scene.ADD_ACCOUNT);
    }


    @FXML
    protected void onGenerateButtonClicked(ActionEvent event) {
    	int min = 0;
    	int max = 0;
    	int upperCase = 0;
    	int special = 0;
    	try {
            min = Integer.parseInt(minLength.getText());
            max = Integer.parseInt(maxLength.getText());
            upperCase = Integer.parseInt(upperCaseLetter.getText());
            special = Integer.parseInt(specialCharacter.getText());
            
            if (min > max) {
                //throw new NumberFormatException();
           		Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("The password's mimimum length cannot be greater than its maximum length.");
                alert.show();
            }
            else if(upperCase + special > min) {
           		Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("The password cannot have more uppercase and special characters than its minimum size.");
                alert.show();
            }
            else if (!minLength.getText().isEmpty() && !maxLength.getText().isEmpty() && !upperCaseLetter.getText().isEmpty() && !specialCharacter.getText().isEmpty()) {
                try {
                    String password = PasswordUtils.generatePassword(min, max, special, upperCase);
                    genPassword.setText(password);
                } catch (IllegalArgumentException e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Field is empty");
                alert.show();
            }
    	}
    	catch(Exception e) {
    		Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The password criteria must only contain numbers.");
            alert.show();
    	}
    }


    @FXML
    protected void onSaveButtonClicked(ActionEvent event) {
        if (genPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please generate a password first");
            alert.show();
            return;
        }

        ApplicationController applicationController = ApplicationController.getInstance();
        ((AddAccountSceneController) applicationController.getSceneController(ApplicationController.Scene.ADD_ACCOUNT))
                .setPasswordInput(genPassword.getText());
        applicationController.setScene(ApplicationController.Scene.ADD_ACCOUNT);
    }
}
