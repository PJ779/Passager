package application.controller.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

import application.controller.ApplicationController;
import application.controller.UserController;
import application.controller.UserController.NotLoggedInException;
import application.model.Account;
import application.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class MainSceneController implements ISceneController, Initializable {
    
	protected Collection<Account> accountList;
	protected ArrayList<Account> accounts;
	protected ArrayList<String> accountSiteNames = new ArrayList<String>();
	protected int currentAccountIndex = -1;
	UserController userController = UserController.getInstance();
	
	
	@FXML
	protected ListView<String> listView;
	@FXML
	protected TextField searchField;
	
	//Right Display features
	@FXML
	protected TextField websiteField;
	@FXML
	protected TextField usernameField;
	@FXML
	protected TextField emailField;
	@FXML
	protected TextField passwordField;
	@FXML
	protected TextField expireField;
	
	protected TextField[] rightDisplay = {websiteField, usernameField, emailField, passwordField, expireField};
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//System.out.println("test");
    	listView.getSelectionModel().selectedItemProperty().addListener(
                (var,oldValue,newValue) -> {
                    currentAccountIndex = listView.getSelectionModel().getSelectedIndex();
                    if (currentAccountIndex != -1) {
                        setSideMenu(accounts.get(currentAccountIndex));
                    }
       	});
	}
	
	@Override
    public void onSceneEnter() {
		//Make all TextFields not editable.
		websiteField.setEditable(false);
		usernameField.setEditable(false);
		emailField.setEditable(false);
		passwordField.setEditable(false);
		expireField.setEditable(false);
	   
	//Clears out the account display (ListView)
	emptyFields();
	searchField.clear();
		
	//Displays the accounts
        updateAccountData();
        	
        //Display items
        displayAccounts(accountSiteNames);
        
        //Check if user has expired passwords.
        notifyExpiredPasswords(userController);
    }
	
	private void emptyFields() {
    	listView.getItems().clear();
    	accountSiteNames.clear();
    	websiteField.clear();
    	usernameField.clear();
    	emailField.clear();
    	passwordField.clear();
    	expireField.clear();
	}
	
	private void updateAccountData(){
    	try {
			accountList = userController.getLoggedInUserAccounts();
		} catch (NotLoggedInException e) {
			e.printStackTrace();
		}
    	accounts = new ArrayList<Account>(accountList);
    	
    	//Arrange account data into more usable lists.
    	for (Account account : accounts) {
            accountSiteNames.add(account.getSiteName());
        }
    	
    	//Displays accounts, adjusted for any criteria in the search field.
    	onSearchFieldUsed();
	}
	
	protected void displayAccounts(ArrayList<String> accounts) {
    	listView.getItems().clear();
		listView.getItems().addAll(accounts);
	}
	
	//Checks the user's accounts. If any have expired passwords, alert the user.
	protected void notifyExpiredPasswords(UserController userController) {
		Date currentDate = new Date(System.currentTimeMillis());
		Date accountExpirationDate = currentDate;
		
		//Check every account to see if it is past its expiration date
		for (Account account : accounts) {
            accountExpirationDate = account.getExpirationDate();
            if (accountExpirationDate != null) {
                if (currentDate.after(accountExpirationDate)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Account: " + account.getSiteName()
                            + " has expired. Please change the password.");
                    alert.show();
                }
            }
        }
	}
	
	protected void setSideMenu(Account account) {
        websiteField.setText(account.getSiteName());
        usernameField.setText(account.getUsername());
        emailField.setText(account.getEmail());
        passwordField.setText(PasswordUtils.decryptPassword(account.getPassword()));
        try {
        	expireField.setText(account.getExpirationDate().toString());
        }
        catch (NullPointerException e) {
        	expireField.setText("");
        	//e.printStackTrace();
        }
	}
	
	@FXML
	protected void onSearchFieldUsed() {
		String searchString = searchField.getText();
		ArrayList<String> filteredAccounts = new ArrayList<String>();
		for(String account : accountSiteNames) {
			if(account.contains(searchString)) {
				filteredAccounts.add(account);
			}
		}
		displayAccounts(filteredAccounts);
	}
	
	@FXML
	protected void onCopyPasswordButtonClicked() {
		if(currentAccountIndex > -1) {
			ClipboardContent content = new ClipboardContent();
			content.putString(PasswordUtils.decryptPassword(accounts.get(currentAccountIndex).getPassword()));
			content.putHtml("<b>Bold</b> text");
			Clipboard.getSystemClipboard().setContent(content);
		}
		else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No account selected.");
			alert.show();
		}
	}
	
	@FXML
	protected void onDeleteButtonClicked() {
		if(currentAccountIndex > -1) {
			try {
				//DataManager.deleteAccount(UserController.getInstance().toString(), accounts.get(currentAccountIndex).getId());
				try {
					UserController.getInstance().deleteAccount(accounts.get(currentAccountIndex));
					accounts.remove(currentAccountIndex);
					currentAccountIndex--;
					emptyFields();
					updateAccountData();
				} catch (NotLoggedInException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No account selected.");
			alert.show();
		}
	}
	
	@FXML
	protected void onEditButtonClicked() {
		if(currentAccountIndex > -1) {
	        ApplicationController applicationController = ApplicationController.getInstance();
	        ((AddAccountSceneController) applicationController.getSceneController(ApplicationController.Scene.ADD_ACCOUNT))
	                .setEditFields(accounts.get(currentAccountIndex));
	        //onDeleteButtonClicked();
	        applicationController.setScene(ApplicationController.Scene.ADD_ACCOUNT);
		}
		else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setContentText("No account selected.");
			alert.show();
		}
	}
	
    @FXML
    protected void onAddAccountButtonClicked() {
        ApplicationController.getInstance().setScene(ApplicationController.Scene.ADD_ACCOUNT);
    }

    @FXML
    protected void onLogoutButtonClicked() {
        UserController.getInstance().logout();
        ApplicationController.getInstance().setScene(ApplicationController.Scene.LOGIN);
    }
}
