package application.controller.ui;

import application.controller.ApplicationController;
import application.controller.UserController;
import application.controller.UserController.NotLoggedInException;
import application.data.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginSceneController implements ISceneController {
	
    @FXML
    protected TextField username;

    @FXML
    protected PasswordField password;
    
    @FXML
    protected Label label;
    
    protected String getUsernameEntry() {
    	return username.getText().trim();
    }
    
    protected String getPasswordEntry() {
    	return password.getText().trim();
    }
    
    // Sends the user an alert. The contents of the alert are arguments.
    protected void alert(String mainText) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mainText);
        alert.show();
    }
    
    @Override public void onSceneEnter() {
    	//Resets all the items that may carry over data from previous use.
    	username.clear();
    	password.clear();
    }
	
    @FXML protected void onLoginButtonClicked() throws NotLoggedInException {
    	validateLogin(getUsernameEntry(), getPasswordEntry());
    }
    
    @FXML protected void onCreateAccountButtonClicked() {
    	ApplicationController.getInstance().setScene(ApplicationController.Scene.SIGNUP);
    }
    
    @FXML protected void onForgotMyPasswordButtonClicked() {
    	ApplicationController.getInstance().setScene(ApplicationController.Scene.RESET_MASTER_PASSWORD);
    }
    
    //If the login information is valid, logs in and sends user to Main Scene.
    private void validateLogin(String username, String password){
    	if(DataManager.userExists(username) == true) {
    		try {
    			UserController.getInstance().login(username, password);
				ApplicationController.getInstance().setScene(ApplicationController.Scene.MAIN);
			} catch (NotLoggedInException e) {
				alert("Invalid Password");
			}
    	}
    	else {
    		alert("Invalid Username");
    	}
    }
}
