package application.controller.ui;

import java.io.IOException;

import application.controller.ApplicationController;
import application.data.DataManager;
import application.model.User;
import application.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ResetMasterPasswordSceneController implements ISceneController {
	
	@FXML private TextField username;
	@FXML private TextField securityQuestion;
	@FXML private TextField securityAnswer;
	@FXML private TextField password;
	@FXML private TextField confirmPassword;
	
	@FXML private Label usernameLabel;
	@FXML private Label securityQuestionLabel;
	@FXML private Label securityAnswerLabel;
	@FXML private Label passwordLabel;
	@FXML private Label confirmPasswordLabel;
	
	@FXML private Button confirmQuestionButton;
	@FXML private Button buttonReset;
	
	private User userToReset;

	
	@Override
	public void onSceneEnter() {
		//Sets visibility of items so only those needed for step 1 are present.
		username.setEditable(true);
		securityQuestion.setVisible(false);
		securityAnswer.setVisible(false);
		password.setVisible(false);
		confirmPassword.setVisible(false);
		securityQuestionLabel.setVisible(false);
		securityAnswerLabel.setVisible(false);
		passwordLabel.setVisible(false);
		confirmPasswordLabel.setVisible(false);
		confirmQuestionButton.setVisible(false);
		buttonReset.setVisible(false);
		
		//Resets all the items that may carry over data from previous use.
		username.clear();
		securityQuestion.clear();
		securityAnswer.clear();
		password.clear();
		confirmPassword.clear();
	}
	
	// To cancel process and return to the login scene.
    @FXML protected void onBackButtonClicked() {
        ApplicationController.getInstance().setScene(ApplicationController.Scene.LOGIN);
    }
	
	// To complete step 1. Checks if user input a valid username for an existing user.
	@FXML protected void onConfirmUsernameButtonClicked() {
		if(DataManager.userExists(username.getText().trim())) {
			try {
				userToReset = DataManager.readUserByLoginName(username.getText().trim());
				revealSecurityQuestion(); //Reveal nodes to complete next step.
			} catch (ClassNotFoundException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
      		Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The user does not exist");
            alert.show();
            username.clear();
		}
	}
    
    // To complete step 2. Checks if user provided the correct answer to the security question.
    @FXML protected void onConfirmQuestionButtonClicked() {
    	if(securityAnswer.getText().equals(userToReset.getAnswer())) {
    		revealPasswordEntry(); //Reveal nodes to complete next step.
    	}
    	else {
      		Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect Answer");
            alert.show();
            onSceneEnter();
    	}
    }
    
    // To complete step 3 (final step). Changes the user's password to the new value.
    @FXML protected void onResetPasswordButtonClicked() {
    	// If the user correctly confirms the new password, override the old user with the new one.
    	if(password.getText().equals(confirmPassword.getText())) {
    		userToReset.setMasterPassword(PasswordUtils.encryptPassword(password.getText()));
    		try {
				DataManager.saveUser(userToReset);
			} catch (IOException e) {
				e.printStackTrace();
			}
    		ApplicationController.getInstance().setScene(ApplicationController.Scene.LOGIN);
    	}
    	// Otherwise, inform the user they failed and allow them to retry password.
    	else {
      		Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The password fields did not match. Please enter a new password.");
            alert.show();
            password.clear();
            confirmPassword.clear();
    	}
    }
  
    // Reveals the information and fields needed to complete step 2.
    private void revealSecurityQuestion() {
		securityQuestion.setVisible(true);
		securityAnswer.setVisible(true);
		securityQuestionLabel.setVisible(true);
		securityAnswerLabel.setVisible(true);
		securityQuestion.setText(userToReset.getQuestion());
		securityQuestion.setEditable(false);
		confirmQuestionButton.setVisible(true);
    }
    
    // Reveals the information and fields needed to complete step 3 (final step).
    private void revealPasswordEntry() {
    	passwordLabel.setVisible(true);
		confirmPasswordLabel.setVisible(true);
		confirmQuestionButton.setVisible(true);
		buttonReset.setVisible(true);
		password.setVisible(true);
		confirmPassword.setVisible(true);
    }
}
