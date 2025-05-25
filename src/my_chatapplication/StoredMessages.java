package my_chatapplication;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StoredMessages extends JFrame {
    private JTable messagesTable;
    private DefaultTableModel tableModel;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton sendButton;
    private JButton backButton;

    private List<MessageData> storedMessages = new ArrayList<>();
    private String filename; // file to store messages

    public StoredMessages(String username) {
        setTitle("Stored Messages - " + username);
        setSize(900, 500); // increased width for new column
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        filename = "messages_" + username + ".txt";

        initComponents();

        loadMessagesFromFile();

        setLayout(new BorderLayout());
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initComponents() {
        String[] columnNames = {"Recipient", "Message Preview", "Time Saved", "Message ID", "Message Hash"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        messagesTable = new JTable(tableModel);
        messagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set preferred column widths
        messagesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        messagesTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        messagesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        messagesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        messagesTable.getColumnModel().getColumn(4).setPreferredWidth(250); // for hash

        // Buttons
        viewButton = new JButton("View Message");
        deleteButton = new JButton("Delete");
        sendButton = new JButton("Send Now");
        backButton = new JButton("Back to Chat");

        // Button actions
        viewButton.addActionListener(e -> viewSelectedMessage());
        deleteButton.addActionListener(e -> deleteSelectedMessage());
        sendButton.addActionListener(e -> sendSelectedMessage());
        backButton.addActionListener(e -> dispose());
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(messagesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(viewButton);
        panel.add(deleteButton);
        panel.add(sendButton);
        panel.add(backButton);
        return panel;
    }

    private void loadMessagesFromFile() {
        File file = new File(filename);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format: Recipient:Message Preview:Time Saved:Message ID:Message Hash
                String[] parts = line.split(":", 5);
                if (parts.length == 5) {
                    String recipient = parts[0];
                    String messagePreview = parts[1];
                    String timeSaved = parts[2];
                    String messageId = parts[3];
                    String messageHash = parts[4];
                    MessageData msg = new MessageData(recipient, messagePreview, timeSaved, messageId, messageHash);
                    storedMessages.add(msg);
                    tableModel.addRow(new Object[]{recipient, messagePreview, timeSaved, messageId, messageHash});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMessageToFile(MessageData message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            // Save in format: Recipient:Message Preview:Time Saved:Message ID:Message Hash
            bw.write(message.getRecipient() + ":" + message.getMessage() + ":" + message.getTimeSaved() + ":" + message.getMessageId() + ":" + message.getMessageHash());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewSelectedMessage() {
        int selectedRow = messagesTable.getSelectedRow();
        if (selectedRow >= 0) {
            MessageData message = storedMessages.get(selectedRow);
            JOptionPane.showMessageDialog(this,
                "Recipient: " + message.getRecipient() + "\n\n" +
                "Message: " + message.getMessage() + "\n\n" +
                "Time Saved: " + message.getTimeSaved() + "\n" +
                "Message ID: " + message.getMessageId() + "\n" +
                "Message Hash: " + message.getMessageHash(),
                "Message Details",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a message to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedMessage() {
        int selectedRow = messagesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this message?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                storedMessages.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                rewriteMessagesToFile();
                JOptionPane.showMessageDialog(this, "Message deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a message to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void sendSelectedMessage() {
        int selectedRow = messagesTable.getSelectedRow();
        if (selectedRow >= 0) {
            MessageData message = storedMessages.get(selectedRow);
            JOptionPane.showMessageDialog(this,
                "Message to " + message.getRecipient() + " sent successfully!",
                "Message Sent",
                JOptionPane.INFORMATION_MESSAGE);
            storedMessages.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            rewriteMessagesToFile();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a message to send.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rewriteMessagesToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (MessageData msg : storedMessages) {
                bw.write(msg.getRecipient() + ":" + msg.getMessage() + ":" + msg.getTimeSaved() + ":" + msg.getMessageId() + ":" + msg.getMessageHash());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class for message data
    private class MessageData {
        private String recipient;
        private String message;
        private String timeSaved;
        private String messageId;
        private String messageHash;

        public MessageData(String recipient, String message, String timeSaved, String messageId, String messageHash) {
            this.recipient = recipient;
            this.message = message;
            this.timeSaved = timeSaved;
            this.messageId = messageId;
            this.messageHash = messageHash;
        }
        public String getRecipient() { return recipient; }
        public String getMessage() { return message; }
        public String getTimeSaved() { return timeSaved; }
        public String getMessageId() { return messageId; }
        public String getMessageHash() { return messageHash; }
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StoredMessages("test_user"));
    }
}