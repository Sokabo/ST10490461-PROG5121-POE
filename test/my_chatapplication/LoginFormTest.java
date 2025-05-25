package my_chatapplication;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginFormTest {

    @BeforeClass
    public static void setup() {
        // Create a sample user file for testing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_info.txt"))) {
            // Store hashed passwords for test purposes
            writer.write("kyl_1," + hashPassword("Ch&&sec@ke99") + ",FirstName,LastName\n");
            writer.write("use_2,hashedpassword,Jane,Doe\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Load user data into the HashMaps for testing
        LoginForm.loadUserData();
    }

    @Test
    public void testUsernameCorrectlyFormatted() {
        String username = "kyl_1";
        assertTrue("Username is not correctly formatted", username.length() <= 5 && username.contains("_"));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        String username = "kyle!!!!!";
        assertFalse("Username is not correctly formatted", username.length() <= 5 && username.contains("_"));
    }

    @Test
    public void testPasswordMeetsComplexityRequirements() {
        String password = "Ch&&sec@ke99";
        assertTrue("Password is not correctly formatted", isValidPassword(password));
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {
        String password = "password";
        assertFalse("Password does not meet the complexity requirements", isValidPassword(password));
    }

    @Test
    public void testLoginSuccessful() {
        assertTrue("Login should be successful", checkCredentials("kyl_1", "Ch&&sec@ke99"));
    }

    @Test
    public void testLoginFailed() {
        assertFalse("Login should fail", checkCredentials("kyle!!!!!", "wrongpassword"));
    }

    // Helper methods
    private static boolean isValidPassword(String password) {
        boolean hasUpper = false, hasDigit = false, hasSpecial = false;
        if (password.length() < 8 || password.length() > 15) return false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    private static boolean checkCredentials(String username, String password) {
        String hashedInputPassword = hashPassword(password);
        if (LoginForm.userCredentials.containsKey(username)) {
            String storedHashedPassword = LoginForm.userCredentials.get(username);
            return storedHashedPassword.equals(hashedInputPassword);
        }
        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}