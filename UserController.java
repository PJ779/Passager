package application.controller;

import application.data.DataManager;
import application.model.Account;
import application.model.User;
import application.utils.PasswordUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserController {
    public static class NotLoggedInException extends Exception {
        public NotLoggedInException(String message) {
            super(message);
        }
    }

    private static final UserController singleInstance = new UserController();

    private User loggedInUser;
    private SortedMap<String, Account> loggedInUserAccounts;

    private UserController() {
    }

    public static UserController getInstance() {
        return singleInstance;
    }

    private void checkLoggedIn() throws NotLoggedInException {
        if (loggedInUser == null) {
            throw new NotLoggedInException("Not logged in");
        }
    }

    public void login(String loginName, String masterPassword) throws NotLoggedInException {
        try {
            User user = DataManager.readUserByLoginName(loginName);

            // check password
            if (!PasswordUtils.decryptPassword(user.getMasterPassword()).equals(masterPassword)) {
                throw new NotLoggedInException("Wrong password");
            }
            loggedInUser = user;

        } catch (FileNotFoundException e) {
            logout();
            throw new NotLoggedInException("User not found");
        } catch (ClassNotFoundException | IOException e) {
            logout();
            throw new NotLoggedInException("Failed to read user data: " + e.getMessage());
        }

        try {
            loggedInUserAccounts = DataManager.readAccounts(loggedInUser.getLoginName()).stream().collect(
                    Collectors.toMap(Account::getId, x -> x, (u, v) -> {
                        throw new IllegalStateException(String.format("Duplicate key %s", u));
                    }, TreeMap::new));

        } catch (IOException | ClassNotFoundException e) {
            logout();
            throw new NotLoggedInException("Failed to read accounts: " + e.getMessage());
        }
    }

    public void logout() {
        loggedInUser = null;
        loggedInUserAccounts = null;
    }

//    public boolean isLoggedIn() {
//        return loggedInUser != null;
//    }
//
//    public User getLoggedInUser() throws NotLoggedInException {
//        checkLoggedIn();
//        return loggedInUser;
//    }

    public Collection<Account> getLoggedInUserAccounts() throws NotLoggedInException {
        checkLoggedIn();
        return loggedInUserAccounts.values();
    }

    public void saveAccount(Account account) throws NotLoggedInException, IOException {
        checkLoggedIn();
        DataManager.saveAccount(loggedInUser.getLoginName(), account);
        loggedInUserAccounts.put(account.getId(), account);
    }

    public void deleteAccount(Account account) throws NotLoggedInException, IOException {
        checkLoggedIn();
        DataManager.deleteAccount(loggedInUser.getLoginName(), account.getId());
        loggedInUserAccounts.remove(account.getId());
    }
}
