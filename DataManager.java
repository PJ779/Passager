package application.data;

import application.model.Account;
import application.model.User;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataManager {
    private static final String FILE_EXTENSION = ".ser";
    private static final String DATA_PATH = "data";

    private static final String USER_DATA_PATH = "users";
    private static final String ACCOUNT_DATA_PATH = "accounts";

    private static void ensureParentDirectoryExists(String path) throws IOException {
        File parentDir = Paths.get(path).getParent().toFile();
        if (parentDir.exists()) {
            return;
        }
        boolean success = parentDir.mkdirs();
        if (!success) {
            throw new IOException("Could not create parent directory");
        }
    }

    private static void writeObjectToFile(String path, Serializable object) throws IOException {
        ensureParentDirectoryExists(path);
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    private static Object readObjectFromFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return object;
    }

    public static boolean userExists(String loginName) {
        File file = new File(Paths.get(DATA_PATH, USER_DATA_PATH, loginName + FILE_EXTENSION).toString());
        return file.exists();
    }

    public static User readUserByLoginName(String loginName) throws IOException, ClassNotFoundException {
        return (User) readObjectFromFile(
                Paths.get(DATA_PATH, USER_DATA_PATH, loginName + FILE_EXTENSION).toString());
    }

    public static void saveUser(User user) throws IOException {
        writeObjectToFile(
                Paths.get(DATA_PATH, USER_DATA_PATH, user.getLoginName() + FILE_EXTENSION).toString(), user);
        ensureParentDirectoryExists(
                Paths.get(DATA_PATH, ACCOUNT_DATA_PATH, user.getLoginName(), ".").toString());
    }

    public static ArrayList<Account> readAccounts(String loginName) throws IOException, ClassNotFoundException {
        File[] files = new File(Paths.get(DATA_PATH, ACCOUNT_DATA_PATH, loginName).toString()).listFiles();
        if (files == null) {
            throw new IOException("User " + loginName + " does not exist");
        }
        ArrayList<Account> accounts = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                accounts.add((Account) readObjectFromFile(file.getPath()));
            }
        }
        return accounts;
    }

    public static void saveAccount(String loginName, Account account) throws IOException {
        writeObjectToFile(Paths.get(DATA_PATH, ACCOUNT_DATA_PATH, loginName, account.getId() + FILE_EXTENSION)
                .toString(), account);
    }

    public static void deleteAccount(String loginName, String accountId) throws IOException {
        File file = new File(
                Paths.get(DATA_PATH, ACCOUNT_DATA_PATH, loginName, accountId + FILE_EXTENSION).toString());
        if (file.exists()) {
            boolean result = file.delete();
            if (!result) {
                throw new IOException("Could not delete account");
            }
        }
    }
}
