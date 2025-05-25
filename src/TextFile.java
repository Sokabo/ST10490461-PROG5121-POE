import java.io.*;
import javax.swing.JOptionPane;

public class TextFile {

    public static void main(String[] args) {
        String fileName = "user_info.txt"; // corrected file extension
        File userFile = new File(fileName);
        
        // Create the file if it doesn't exist
        try {
            if (userFile.createNewFile()) {
                System.out.println("File created: " + userFile.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
            return; // Exit if file creation fails
        }

        // Prompt user for username and password
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");

        // Write user details to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            writer.write("Username: " + username + " | Password: " + password);
            writer.newLine();
            JOptionPane.showMessageDialog(null, "Your details have been captured");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}