package my_chatapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class RegisterForm extends JFrame implements ActionListener {
    // UI components
    JLabel lblTitle, lblUser, lblPass, lblFirstName, lblLastName, lblCell, lblLogin;
    JTextField txtUser, txtCell, txtFirstName, txtLastName;
    JPasswordField txtPass, txtConfirmPass;
    JCheckBox chkShow;
    JButton btnRegister, btnReset, btnExit;
    // Static HashMaps to hold user data
    public static HashMap<String, String> userCredentials = new HashMap<>();
    public static HashMap<String, String[]> userNames = new HashMap<>();

    public static void initializeData() {
        // Load existing users from file
        String filename = "user_info.txt";
        File file = new File(filename);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    String firstName = parts[2];
                    String lastName = parts[3];
                    userCredentials.put(username, hashedPassword);
                    userNames.put(username, new String[]{firstName, lastName});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RegisterForm() {
        setTitle("Register Form");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.BLUE);
        leftPanel.setPreferredSize(new Dimension(150, 450));
        leftPanel.setLayout(new BorderLayout());

        JLabel lblChatApp = new JLabel("CHAT APP", JLabel.CENTER);
        lblChatApp.setFont(new Font("Arial", Font.BOLD, 20));
        lblChatApp.setForeground(Color.WHITE);
        leftPanel.add(lblChatApp, BorderLayout.CENTER);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);

        lblTitle = new JLabel("User Registration");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(110, 10, 200, 30);
        rightPanel.add(lblTitle);

        lblFirstName = new JLabel("First Name:");
        lblFirstName.setBounds(30, 60, 100, 25);
        rightPanel.add(lblFirstName);

        txtFirstName = new JTextField();
        txtFirstName.setBounds(150, 60, 200, 25);
        rightPanel.add(txtFirstName);

        lblLastName = new JLabel("Last Name:");
        lblLastName.setBounds(30, 100, 100, 25);
        rightPanel.add(lblLastName);

        txtLastName = new JTextField();
        txtLastName.setBounds(150, 100, 200, 25);
        rightPanel.add(txtLastName);

        lblUser = new JLabel("Username:");
        lblUser.setBounds(30, 140, 100, 25);
        rightPanel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(150, 140, 200, 25);
        rightPanel.add(txtUser);

        lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 180, 100, 25);
        rightPanel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(150, 180, 200, 25);
        rightPanel.add(txtPass);

        JLabel lblConfirmPass = new JLabel("Confirm Password:");
        lblConfirmPass.setBounds(30, 220, 120, 25);
        rightPanel.add(lblConfirmPass);

        txtConfirmPass = new JPasswordField();
        txtConfirmPass.setBounds(150, 220, 200, 25);
        rightPanel.add(txtConfirmPass);

        chkShow = new JCheckBox("Show Password");
        chkShow.setBounds(150, 250, 150, 20);
        chkShow.addActionListener(this);
        rightPanel.add(chkShow);

        lblCell = new JLabel("Cellphone:");
        lblCell.setBounds(30, 280, 100, 25);
        rightPanel.add(lblCell);

        // Country code combo box
        JComboBox<String> comboCountryCode = new JComboBox<>(new String[]{
            "+27", "+1", "+44", "+61", "+91", "+33", "+49", "+81", "+55", "+34"
        });
        comboCountryCode.setBounds(150, 280, 60, 25);
        rightPanel.add(comboCountryCode);

        txtCell = new JTextField();
        txtCell.setBounds(220, 280, 130, 25);
        rightPanel.add(txtCell);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(40, 320, 100, 30);
        btnRegister.addActionListener(this);
        rightPanel.add(btnRegister);

        btnReset = new JButton("Reset");
        btnReset.setBounds(150, 320, 90, 30);
        btnReset.addActionListener(this);
        rightPanel.add(btnReset);

        btnExit = new JButton("Exit");
        btnExit.setBounds(250, 320, 90, 30);
        btnExit.addActionListener(this);
        rightPanel.add(btnExit);

        lblLogin = new JLabel("Already have an account? Login");
        lblLogin.setBounds(90, 360, 250, 30);
        lblLogin.setForeground(Color.BLUE);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginForm();
            }
        });
        rightPanel.add(lblLogin);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chkShow) {
            if (chkShow.isSelected()) {
                txtPass.setEchoChar((char) 0);
                txtConfirmPass.setEchoChar((char) 0);
            } else {
                txtPass.setEchoChar('•');
                txtConfirmPass.setEchoChar('•');
            }
        }

        if (e.getSource() == btnReset) {
            txtUser.setText("");
            txtPass.setText("");
            txtConfirmPass.setText("");
            txtCell.setText("");
            txtFirstName.setText("");
            txtLastName.setText("");
        }

        if (e.getSource() == btnExit) {
            System.exit(0);
        }

        if (e.getSource() == btnRegister) {
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword());
            String confirmPassword = new String(txtConfirmPass.getPassword());
            String phone = txtCell.getText().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }
            if (username.length() < 5 || !username.contains("_")) {
                JOptionPane.showMessageDialog(this, "Username is not correctly formatted, please ensure that the username contains an underscore and is no more than five characters in length");
                return;
            }
            if (phone.length() < 9) {
                JOptionPane.showMessageDialog(this, "Cell phone number incorrectly formatted or does not have international code");
                return;
            }
            if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }

            // Save user
            String hashedPassword = hashPassword(password);
            userCredentials.put(username, hashedPassword);
            userNames.put(username, new String[]{firstName, lastName});
            saveUserToFile(username, hashedPassword, firstName, lastName);
            JOptionPane.showMessageDialog(this, "Username successfully captured");
            JOptionPane.showMessageDialog(this, "Password successfully captured");
            JOptionPane.showMessageDialog(this, "Cell phone number successfully added");
            JOptionPane.showMessageDialog(this, "Registration successful!");        
            dispose();
            new LoginForm();
        }
    }

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

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveUserToFile(String username, String hashedPassword, String firstName, String lastName) {
        String filename = "user_info.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(username + "," + hashedPassword + "," + firstName + "," + lastName);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new RegisterForm());
}

}