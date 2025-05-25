package my_chatapplication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CustomChatApp extends JFrame {

    private String username;
    int totalMessagesToSend = 0;
    int messagesSentCount = 0;

    String recipientNumber = null; // store recipient
    final List<Message> messageHistory = new ArrayList<>();

    // UI components
    JTextArea chatArea;
    JTextArea inputArea;
    private JButton sendButton;
    private JLabel statusLabel;

    // Message ID counter
    private int messageIdCounter = 1;

    public CustomChatApp(String username) {
        this.username = username;
        initializeUI();

        // Prompt for recipient before starting main loop
        recipientNumber = promptForRecipient();
        if (recipientNumber == null) {
            JOptionPane.showMessageDialog(this, "Recipient not set. Exiting application.");
            dispose();
            return;
        }

        // Enable message input now that recipient is set
        enableInput();

        // Start main loop
        mainLoop();
    }

    private void initializeUI() {
        setTitle("QuickChat");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        JLabel headerLabel = new JLabel("QuickChat", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setPreferredSize(new Dimension(780, 350));
        mainPanel.add(chatScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputArea = new JTextArea(3, 50);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 153, 76));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> handleSend());

        statusLabel = new JLabel("Waiting for recipient...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void mainLoop() {
        new Thread(() -> {
            appendChat("Welcome to QuickChat, " + username + "!");

            boolean continueLoop = true;

            while (continueLoop) {
                int option = showMainOptions();

                switch (option) {
                    case 1:
                        // Send messages flow
                        boolean setLimit = promptForMessageCount();
                        if (setLimit) {
                            messagesSentCount = 0;
                            enableInput();
                            for (int i = 0; i < totalMessagesToSend; i++) {
                                String messageText = waitForMessageInput();
                                if (messageText == null) break;

                                // show options: send, save, discard
                                int choice = JOptionPane.showOptionDialog(this,
                                    "Message: " + messageText,
                                    "Message Options",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    new String[]{"Send", "Save for Later", "Discard"},
                                    "Send");

                                String messageId = generateMessageId();
                                String messageHash = generateMessageHash(messageId, ++messageIdCounter, messageText);
                                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                                int messageNumber = ++messagesSentCount;

                                if (choice == 0) { // Send
                                    String messageDetails = formatMessageDetails(timestamp, recipientNumber, messageText, messageId, messageHash, messageNumber);
                                    appendChat(messageDetails);
                                    messageHistory.add(new Message(messageText, MessageStatus.SENT));
                                } else if (choice == 1) { // Save for later
                                    saveMessageForLater(messageText);
                                    messageHistory.add(new Message(messageText, MessageStatus.NOT_SENT));
                                } else {
                                    // Discard
                                    // do nothing
                                }
                                // Update status label
                                SwingUtilities.invokeLater(() -> {
                                    statusLabel.setText(messagesSentCount + " of " + totalMessagesToSend + " messages sent.");
                                });
                            }
                            disableInput();
                            appendChat("You have finished sending messages.");
                        }
                        break;
                    case 2:
                        showSentMessages();
                        break;
                    case 3:
                        appendChat("Session ended. Thank you!");
                        disableInput();
                        // Show summary of messages sent
                        JOptionPane.showMessageDialog(this, "You sent a total of " + messagesSentCount + " message(s).", "Summary", JOptionPane.INFORMATION_MESSAGE);
                        continueLoop = false;
                        break;
                }
            }
        }).start();
    }

    private String promptForRecipient() {
        String[] countryCodes = {"+27"};
        JComboBox<String> codeComboBox = new JComboBox<>(countryCodes);
        JTextField numberField = new JTextField(9);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("Country Code:"));
        panel.add(codeComboBox);
        panel.add(new JLabel("Number:"));
        panel.add(numberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Enter Recipient Number", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedCode = (String) codeComboBox.getSelectedItem();
            String number = numberField.getText().trim();
            if (!number.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Please enter exactly 9 digits.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return selectedCode + number;
        }
        return null;
    }

    private int showMainOptions() {
        String menu = "Select an option:\n" +
                "1. Send Messages\n" +
                "2. Show Sent Messages\n" +
                "3. Quit";
        String input = JOptionPane.showInputDialog(this, menu, "Main Menu", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return 3;
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice >= 1 && choice <= 3) return choice;
        } catch (NumberFormatException ignored) {}
        return 3;
    }

    private boolean promptForMessageCount() {
        while (true) {
            String input = JOptionPane.showInputDialog(this, "Enter the number of messages you want to send:", "Message Count", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return false;
            try {
                int count = Integer.parseInt(input.trim());
                if (count > 0) {
                    totalMessagesToSend = count;
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void handleSend() {
        if (recipientNumber == null) {
            JOptionPane.showMessageDialog(this, "Recipient not set.");
            return;
        }
        String messageText = inputArea.getText().trim();
        if (messageText.isEmpty() || messageText.length() > 250) {
            JOptionPane.showMessageDialog(this, "Enter a message up to 250 characters.");
            return;
        }

        // Check if message limit reached
        if (messagesSentCount >= totalMessagesToSend && totalMessagesToSend != 0) {
            JOptionPane.showMessageDialog(this, "You have reached your message limit.");
            disableInput();
            return;
        }

        int choice = JOptionPane.showOptionDialog(this,
                "Choose an action:",
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Send", "Save for Later", "Discard"},
                "Send");

        if (choice == 0) { // Send
            String messageId = generateMessageId();
            String messageHash = generateMessageHash(messageId, ++messageIdCounter, messageText);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String messageDetails = formatMessageDetails(timestamp, recipientNumber, messageText, messageId, messageHash, ++messagesSentCount);
            appendChat(messageDetails);
            messageHistory.add(new Message(messageText, MessageStatus.SENT));
            // Update message count and label
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(messagesSentCount + " of " + totalMessagesToSend);
            });
        } else if (choice == 1) { // Save for later
            saveMessageForLater(messageText);
            messageHistory.add(new Message(messageText, MessageStatus.NOT_SENT));
        } else {
            // Discard - do nothing
        }
        inputArea.setText(""); // Clear input after handling
    }

    String generateMessageId() {
        long timestamp = System.currentTimeMillis();
        String tsStr = String.valueOf(timestamp);
        String idPart = tsStr.substring(tsStr.length() - 7) + String.format("%03d", messageIdCounter);
        return idPart.length() >= 10 ? idPart.substring(0, 10) : idPart;
    }

    String generateMessageHash(String messageId, int messageNumber, String message) {
        String firstTwoDigits = messageId.length() >= 2 ? messageId.substring(0, 2) : messageId;
        String[] words = message.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
        String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord;
        return (firstTwoDigits + ":" + messageNumber + ":" + firstWord + ":" + lastWord).toUpperCase();
    }

    private String formatMessageDetails(String timestamp, String recipient, String message, String messageId, String messageHash, int messageNumber) {
        String statusIcon = "\u2714"; // gray tick for "not sent"
        return "=== MESSAGE #" + messageNumber + " ===\n" +
               "[" + timestamp + "] To: " + recipient + "\n" +
               "Content: " + message + "\n" +
               "Message ID: " + messageId + "\n" +
               "Hash: " + messageHash + "\n" +
               "Status: " + statusIcon + "\n\n";
    }

    private void showSentMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message msg : messageHistory) {
            String statusIcon = msg.status == MessageStatus.SENT ? "\u2714\u2714" : "\u2714"; // 2 ticks or 1 tick
            String statusColor = msg.status == MessageStatus.SENT ? "Light Blue" : "Gray";
            sb.append("Content: ").append(msg.text).append("\n");
            sb.append("Status: ").append(statusIcon).append(" (").append(statusColor).append(")\n\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Sent Messages", JOptionPane.INFORMATION_MESSAGE);
    }

    enum MessageStatus {
        NOT_SENT, SENT
    }

    class Message {
        String text;
        MessageStatus status;
        Message(String text, MessageStatus status) {
            this.text = text;
            this.status = status;
        }
    }

    private void saveMessageForLater(String messageText) {
        try {
            String messageId = generateMessageId();
            String messageHash = generateMessageHash(messageId, ++messageIdCounter, messageText);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

            JSONObject msgObj = new JSONObject();
            msgObj.put("recipient", recipientNumber);
            msgObj.put("message", messageText);
            msgObj.put("messageId", messageId);
            msgObj.put("hash", messageHash);
            msgObj.put("status", "saved");
            msgObj.put("timestamp", timestamp);

            // Read existing saved messages
            JSONArray savedMessagesArray = new JSONArray();
            File file = new File("saved_messages.json");
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    Object obj = new JSONParser().parse(reader);
                    if (obj != null) {
                        savedMessagesArray = (JSONArray) obj;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Add new message
            savedMessagesArray.add(msgObj);

            // Save back to file
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(savedMessagesArray.toJSONString());
            }

            JOptionPane.showMessageDialog(this, "Message saved for later.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save message.");
        }
    }

    private void handleMessageOptions(String messageText) {
        // Not used in this version, placeholder if needed
        appendChat(messageText);
    }

    private void appendChat(String message) {
        SwingUtilities.invokeLater(() -> chatArea.append(message + "\n"));
    }

    private void enableInput() {
        SwingUtilities.invokeLater(() -> {
            inputArea.setEditable(true);
            sendButton.setEnabled(true);
            inputArea.requestFocusInWindow();
            // Initialize status label with "0 of Y"
            statusLabel.setText("0 of " + totalMessagesToSend);
        });
    }

    private void disableInput() {
        SwingUtilities.invokeLater(() -> {
            inputArea.setEditable(false);
            sendButton.setEnabled(false);
            statusLabel.setText("Input disabled.");
        });
    }

    private String waitForMessageInput() {
        final Object lock = new Object();
        final String[] msgHolder = {null};
        ActionListener sendListener = e -> {
            synchronized (lock) {
                msgHolder[0] = inputArea.getText().trim();
                lock.notify();
            }
        };
        KeyListener keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    synchronized (lock) {
                        msgHolder[0] = inputArea.getText().trim();
                        lock.notify();
                    }
                    e.consume();
                }
            }
        };
        SwingUtilities.invokeLater(() -> {
            sendButton.addActionListener(sendListener);
            inputArea.addKeyListener(keyListener);
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {}
        }
        SwingUtilities.invokeLater(() -> {
            for (ActionListener al : sendButton.getActionListeners()) sendButton.removeActionListener(al);
            inputArea.removeKeyListener(keyListener);
        });
        String msg = msgHolder[0];
        if (msg.isEmpty()) return null;
        return msg;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = "Guest";
            new CustomChatApp(username).setVisible(true);
        });
    }
}