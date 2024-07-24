package application.controller.ui;

import application.controller.ApplicationController;
import application.controller.UserController;
import application.model.Account;
import application.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Date;

public class AddAccountSceneController implements ISceneController {
    @FXML
    private TextField siteNameInput;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField expirationDurationInput;
    @FXML
    private Label statusText;

    private String generatedPassword = null;
    
    private boolean currentlyEditing = false;
    
    private Account editedAccount;

    @Override
    public void onSceneEnter() {
    	siteNameInput.requestFocus();
    	statusText.setText("");
        if (generatedPassword != null) {
            passwordInput.setText(generatedPassword);
            generatedPassword = null;
        } else {
            siteNameInput.setText("");
            usernameInput.setText("");
            emailInput.setText("");
            passwordInput.setText("");
            expirationDurationInput.setText("");
            statusText.setText("");
        }
    }

    @FXML
    protected void onBackButtonClicked() {
    	currentlyEditing = false;
        ApplicationController.getInstance().setScene(ApplicationController.Scene.MAIN);
    }

    @FXML
    protected void onSaveButtonClicked() {
//        StringBuilder errorMessage = new StringBuilder();
        Account account = new Account();
        account.setId("" + System.currentTimeMillis() + System.nanoTime() % 1000000);
        account.setSiteName(siteNameInput.getText());
        account.setUsername(usernameInput.getText());
        account.setEmail(emailInput.getText());
        account.setPassword(PasswordUtils.encryptPassword(passwordInput.getText()));
        account.setCreatedDate(new Date());
        try {
            if (!expirationDurationInput.getText().isEmpty()) {
                account.setExpirationDate(new Date(System.currentTimeMillis()
                        + (long) Integer.parseInt(expirationDurationInput.getText()) * 1000 * 60 * 60 * 24));
            }
        } catch (NumberFormatException e) {
            statusText.setText(expirationDurationInput.getPromptText() + " must be a number");
            return;
        }

        try {
            UserController.getInstance().saveAccount(account);
            if(currentlyEditing == true && editedAccount != null) {
            	UserController.getInstance().deleteAccount(editedAccount);
            	//ApplicationController applicationController = ApplicationController.getInstance();
    	        //((MainSceneController) applicationController.getSceneController(ApplicationController.Scene.MAIN)).onDeleteButtonClicked();
            }
        } catch (UserController.NotLoggedInException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            statusText.setText("Fail to save account: " + e.getMessage());
        }

        ApplicationController.getInstance().setScene(ApplicationController.Scene.MAIN);
    }

    @FXML
    protected void onGeneratePasswordButtonClicked() {
        ApplicationController.getInstance().setScene(ApplicationController.Scene.PASSWORD_GENERATOR);
    }

    public void setPasswordInput(String password) {
        if (password == null) {
            generatedPassword = passwordInput.getText();
        } else {
            generatedPassword = password;
        }
    }
    
    public void setEditFields(Account account) {
    	currentlyEditing = true;
    	editedAccount = account;
    	setPasswordInput(PasswordUtils.decryptPassword(account.getPassword()));
    	siteNameInput.setText(account.getSiteName());
    	usernameInput.setText(account.getUsername());
    	emailInput.setText(account.getEmail());
    	expirationDurationInput.setText("10");
    }
    
}
