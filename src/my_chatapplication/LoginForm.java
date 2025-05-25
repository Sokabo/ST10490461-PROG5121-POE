package my_chatapplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class LoginForm extends JFrame implements ActionListener {
    JTextField txtUser;
    JPasswordField txtPass;
    JCheckBox chkShow;
    JButton btnLogin, btnReset, btnExit;
    JLabel lblRegister;

    // Static HashMaps to hold user data
    public static HashMap<String, String> userCredentials = new HashMap<>();
    public static HashMap<String, String[]> userNames = new HashMap<>();

    // Static method to load user data from file
    public static void loadUserData() {
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

    public LoginForm() {
        setTitle("Login Form");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Left Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0, 102, 204));
        leftPanel.setPreferredSize(new Dimension(150, 300));
        leftPanel.setLayout(new GridBagLayout());

        JLabel lblLogo = new JLabel("CHAT APP");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setForeground(Color.WHITE);
        leftPanel.add(lblLogo);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBackground(Color.PINK);

        JLabel lblTitle = new JLabel("User Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(230, 30, 200, 30);
        rightPanel.add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(150, 90, 100, 25);
        rightPanel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(250, 90, 200, 25);
        rightPanel.add(txtUser);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(150, 130, 100, 25);
        rightPanel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(250, 130, 200, 25);
        rightPanel.add(txtPass);

        chkShow = new JCheckBox("Show Password");
        chkShow.setBounds(250, 160, 150, 20);
        chkShow.setBackground(Color.WHITE);
        chkShow.addActionListener(this);
        rightPanel.add(chkShow);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(150, 200, 90, 30);
        btnLogin.addActionListener(this);
        rightPanel.add(btnLogin);

        btnReset = new JButton("Reset");
        btnReset.setBounds(260, 200, 90, 30);
        btnReset.addActionListener(this);
        rightPanel.add(btnReset);

        btnExit = new JButton("Exit");
        btnExit.setBounds(370, 200, 90, 30);
        btnExit.addActionListener(this);
        rightPanel.add(btnExit);

        lblRegister = new JLabel("<HTML>Don't have an account? <FONT color='#0000FF'><U>Register Account</U></FONT></HTML>");
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegister.setBounds(230, 250, 250, 30);
        lblRegister.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose(); // Close login window
                new RegisterForm(); // Open RegisterForm
            }
        });
        rightPanel.add(lblRegister);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chkShow) {
            txtPass.setEchoChar(chkShow.isSelected() ? (char) 0 : '•');
        } else if (e.getSource() == btnReset) {
            txtUser.setText("");
            txtPass.setText("");
        } else if (e.getSource() == btnExit) {
            System.exit(0);
        } else if (e.getSource() == btnLogin) {
            String username = txtUser.getText().trim();
            String password = String.valueOf(txtPass.getPassword()).trim();

            if (username.length() != 5 || !username.contains("_")) {
                JOptionPane.showMessageDialog(this, "Username is not correctly formatted, please ensure that the username contains an underscore and is no more than five characters in length");
            } else if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character." );
            } else {
                if (checkCredentials("user_info.txt", username, password)) {
                    JOptionPane.showMessageDialog(this, "Username successfully captured");
                    JOptionPane.showMessageDialog(this, "Password successfully captured");
                    JOptionPane.showMessageDialog(this, "Login successful!");
                } if (checkCredentials("user_info.txt", username, password)) {
    // Retrieve user's first and last name
   // After verifying credentials successfully
String[] nameParts = userNames.get(username);
String firstName = nameParts != null ? nameParts[0] : "User";
String lastName = nameParts != null ? nameParts[1] : "";

// Your customized message
JOptionPane.showMessageDialog(this, "Welcome " + firstName + ", " + lastName + " it's great to see you again!");

// Proceed to open chat window
SwingUtilities.invokeLater(() -> {
    dispose(); // close login window
    new CustomChatApp(username).setVisible(true);
});
}
                else {
                    int option = JOptionPane.showOptionDialog(
                        this,
                        "Username or password incorrect. Please try again.",
                        "Login Failed",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"Create Account", "Cancel"},
                        "Create Account"
                    );
                    if (option == JOptionPane.YES_OPTION) {
                        dispose();
                        new RegisterForm();
                    }
                }
            }
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

    private boolean checkCredentials(String filename, String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String hashedInputPassword = hashPassword(password);
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String storedUsername = parts[0];
                    String storedHashedPassword = parts[1];
                    if (storedUsername.equals(username) && storedHashedPassword.equals(hashedInputPassword)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String hashPassword(String password) {
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

    // Entry point
    public static void main(String[] args) {
        loadUserData(); // Load existing users
        SwingUtilities.invokeLater(() -> new LoginForm());
    }
}
