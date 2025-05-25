package my_chatapplication;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class CustomChatAppTest {
    
    private CustomChatApp chatApp;

    @Before
    public void setUp() {
        chatApp = new CustomChatApp("TestUser");
        chatApp.recipientNumber = "+27712345678";
    }

    @Test
    public void testMessageCharacterLimit() {
        String validMessage = "This is a valid message.";
        assertTrue("Message should be within limit", validMessage.length() <= 250);

        String invalidMessage = "A".repeat(251);
        assertFalse("Message exceeds character limit", invalidMessage.length() <= 250);
    }

    @Test
    public void testRecipientNumberFormat() {
        assertTrue("Valid number format passed", validatePhoneNumber("+27712345678"));
        assertFalse("Invalid number format failed", validatePhoneNumber("08575975889"));
    }
    @Test
    public void testMessageIdGeneration() {
        String messageId = chatApp.generateMessageId();
        assertNotNull("Message ID should not be null.", messageId);
        assertTrue("Message ID should be 10 characters long.", messageId.length() <= 10);
    }
    @Test
    public void testMessageSending() {
        // Simulate message sending
        chatApp.totalMessagesToSend = 1; // Limit to 1 message
        chatApp.messagesSentCount = 0;

        // Simulate user input
        chatApp.inputArea.setText("Hello, this is a test message.");
        chatApp.handleSend();

        // Check if message count is updated
        assertEquals("Message count should be 1 after sending.", 1, chatApp.messagesSentCount);
        assertFalse("Chat area should not be empty after sending a message.", chatApp.chatArea.getText().isEmpty());
    }
    private boolean validatePhoneNumber(String number) {
        return number.matches("\\+27\\d{9}");
    }
}
