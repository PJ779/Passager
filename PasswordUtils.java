package application.utils;

import java.security.SecureRandom;
import java.util.ArrayList;

import edu.sjsu.yazdankhah.crypto.util.PassUtil;

public class PasswordUtils {
    static final String SPECIAL_CHARACTERS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    static PassUtil passUtil = new PassUtil();

    static public String encryptPassword(String password) {
        return passUtil.encrypt(password);
    }

    static public String decryptPassword(String password) {
        return passUtil.decrypt(password);
    }

    static public String generatePassword(int minLength, int maxLength, int specialCharCount, int capitalCount) {
        int requiredLength = specialCharCount + capitalCount;
        if (requiredLength > maxLength) {
            throw new IllegalArgumentException(
                    "specialCharCount + capitalCount cannot be greater than the maximum length");
        }

        SecureRandom random = new SecureRandom();
        int length = random.nextInt(maxLength - Math.max(minLength, requiredLength) + 1) + minLength;
        ArrayList<Character> passwordCharacters = new ArrayList<>(length);
        for (int i = 0; i < specialCharCount; i++) {
            passwordCharacters.add(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));
        }
        for (int i = 0; i < capitalCount; i++) {
            passwordCharacters.add((char) (random.nextInt(26) + 'A'));
        }
        for (int i = 0; i < length - requiredLength; i++) {
            passwordCharacters.add((char) (random.nextInt(26) + 'a'));
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int remaining = length - i;
            int randomIndex = random.nextInt(remaining);
            password.append(passwordCharacters.get(randomIndex));
            passwordCharacters.set(randomIndex, passwordCharacters.get(remaining - 1));
        }
        return password.toString();
    }
}
