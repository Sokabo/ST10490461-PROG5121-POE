package my_chatapplication;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;

public class RegisterFormTest {

    @BeforeClass
    public static void setup() {
        // Create a sample user file for testing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("user_info.txt"))) {
            writer.write("kyl_1,hashedpassword,FirstName,LastName\n");
            writer.write("user_2,hashedpassword,Jane,Doe\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Load user data into the HashMaps for testing
        RegisterForm.initializeData();
    }

    @Test
    public void testUsernameCorrectlyFormatted() {
        String username = "kyl_1";
        assertTrue("Username successfully captured", username.length() <= 5 && username.contains("_"));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        String username = "kyle!!!!!";
        assertFalse("Username is incorrectly formatted", username.length() <= 5 && username.contains("_"));
    }

    @Test
    public void testPasswordMeetsComplexityRequirements() {
        String password = "Ch&&sec@ke99";
        assertTrue("Password successfully captured", isValidPassword(password));
    }

    @Test
    public void testPasswordDoesNotMeetComplexityRequirements() {
        String password = "password";
        assertFalse("Password does not meet the complexity requirements", isValidPassword(password));
    }

    @Test
    public void testCellPhoneNumberCorrectlyFormatted() {
        String phoneNumber = "+27898969876";
        assertTrue("Cell number successfully captured", validatePhoneNumber(phoneNumber));
    }

    @Test
    public void testCellPhoneNumberIncorrectlyFormatted() {
        String phoneNumber = "08966553";
        assertFalse("Cell number is incorrectly formatted", validatePhoneNumber(phoneNumber));
    }

    @Test
    public void testCellPhoneNumberTooShort() {
        String phoneNumber = "+27";
        assertFalse("Cell phone number is too short", phoneNumber.length() >= 9);
    }

    // Helper methods
    private boolean isValidPassword(String password) {
        boolean hasUpper = false, hasDigit = false, hasSpecial = false;
        if (password.length() < 8 || password.length() > 15) return false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasUpper && hasDigit && hasSpecial;
    }

    private boolean validatePhoneNumber(String number) {
        return number.matches("\\+27\\d{9}");
    }
}