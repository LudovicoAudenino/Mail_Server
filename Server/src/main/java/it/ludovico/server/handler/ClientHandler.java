package it.ludovico.server.handler;

import it.ludovico.shared.model.Email;
import it.ludovico.shared.model.Commands;
import it.ludovico.server.service.EmailService;
import it.ludovico.server.service.LogService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Handles individual client connections and processes mail system commands.
 * 
 * <p>This class implements the server-side protocol handler for the JavaFX Mail System.
 * Each instance runs in its own thread and processes a single client request following
 * the stateless HTTP-like communication pattern. The handler receives commands from
 * clients, delegates business logic to the {@link EmailService}, and returns appropriate
 * responses.</p>
 * 
 * <h2>Protocol Implementation</h2>
 * <p>The handler supports the complete set of mail system operations:</p>
 * <ul>
 *   <li><strong>Authentication:</strong> LOGIN command with user validation</li>
 *   <li><strong>Email Operations:</strong> SEND_EMAIL, REPLY_EMAIL, REPLY_ALL_EMAIL, FORWARD_EMAIL</li>
 *   <li><strong>Mailbox Management:</strong> FETCH_NEW_EMAIL, DELETE_EMAIL</li>
 *   <li><strong>Validation:</strong> CHECK_EMAIL_EXISTS for address verification</li>
 *   <li><strong>Monitoring:</strong> PING command for connectivity testing</li>
 * </ul>
 * 
 * <h2>Connection Lifecycle</h2>
 * <pre>{@code
 * 1. Client connects to server
 * 2. Server creates ClientHandler instance
 * 3. Handler reads command from client
 * 4. Handler processes command via EmailService
 * 5. Handler sends response to client
 * 6. Connection is closed automatically
 * }</pre>
 * 
 * <h2>Error Handling</h2>
 * <p>The handler provides comprehensive error handling for network issues, 
 * deserialization problems, and business logic errors. All errors are logged
 * appropriately and proper responses are sent to clients when possible.</p>
 * 
 * <h2>Thread Safety</h2>
 * <p>Each ClientHandler instance is designed to run in its own thread and
 * handles exactly one client connection. The handler itself is not thread-safe
 * but relies on the thread-safe {@link EmailService} implementation for
 * concurrent operations.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see EmailService
 * @see Commands
 */
public class ClientHandler implements Runnable {
    /**
     * The socket connection to the client.
     * Used for reading commands and sending responses.
     */
    private final Socket socket;
    
    /**
     * The email service instance for processing mail operations.
     * Provides thread-safe business logic implementation.
     */
    private final EmailService emailService;

    /**
     * Constructs a new ClientHandler for the specified client connection.
     * 
     * <p>This constructor prepares the handler to process commands from a single
     * client. The actual command processing begins when {@link #run()} is called.</p>
     * 
     * @param socket the client socket connection; must not be null
     * @param emailService the email service for processing commands; must not be null
     * @throws NullPointerException if either parameter is null
     */
    public ClientHandler(Socket socket, EmailService emailService) {
        this.socket = socket;
        this.emailService = emailService;
    }



    /**
     * Processes a single client request and sends the appropriate response.
     * 
     * <p>This method implements the main request-response cycle of the mail server.
     * It reads a command from the client, dispatches it to the appropriate handler
     * method, and ensures proper resource cleanup. The connection is automatically
     * closed after processing regardless of success or failure.</p>
     * 
     * <h3>Processing Flow</h3>
     * <ol>
     *   <li>Establish input/output streams with the client</li>
     *   <li>Read the command string from the client</li>
     *   <li>Dispatch the command to the appropriate handler method</li>
     *   <li>Send response back to the client</li>
     *   <li>Close the connection (automatic via try-with-resources)</li>
     * </ol>
     * 
     * <p>All operations are logged appropriately for monitoring and debugging purposes.</p>
     * 
     * @throws RuntimeException if critical errors occur during processing
     */
    @Override
    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            String command = (String) input.readObject();
            
            switch (command) {
                case Commands.LOGIN:
                    handleLogin(input, output);
                    break;
                case Commands.SEND_EMAIL:
                    handleSendEmail(input, output);
                    break;
                case Commands.FETCH_NEW_EMAIL:
                    handleFetchNewEmail(input, output);
                    break;
                case Commands.CHECK_EMAIL_EXISTS:
                    handleCheckEmailExists(input, output);
                    break;
                case Commands.DELETE_EMAIL:
                    handleDeleteEmail(input, output);
                    break;
                case Commands.REPLY_EMAIL:
                    handleReplyEmail(input, output);
                    break;
                case Commands.REPLY_ALL_EMAIL:
                    handleReplyAllEmail(input, output);
                    break;
                case Commands.FORWARD_EMAIL:
                    handleForwardEmail(input, output);
                    break;
                case Commands.PING:
                    output.writeObject("PONG");
                    break;
                default:
                    LogService.warn("Unknown command received: " + command);
                    output.writeObject(false);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            LogService.error("Error processing client request: " + e.getMessage());
    }
    }


    /**
     * Handles user authentication and initial mailbox retrieval.
     * 
     * <p>This method processes LOGIN commands by validating the user's email address
     * against the list of registered accounts. If the user is valid, their complete
     * mailbox is retrieved and sent to the client. This serves as the entry point
     * for all client sessions.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> User email address (String)</li>
     *   <li><strong>Output:</strong> List&lt;Email&gt; if successful, null if authentication fails</li>
     * </ul>
     * 
     * @param input the input stream to read the user email from
     * @param output the output stream to send the response to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleLogin(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String email = (String) input.readObject();

        if(emailService.checkRegisteredAccount(email)) {
            LogService.info("Login successful for user: " + email);
            List<Email> emails = emailService.getUserMailbox(email);
            output.writeObject(emails);
        } else {
            LogService.warn("Login failed for user: " + email);
            output.writeObject(null);
        }
    }

    /**
     * Handles email sending operations.
     * 
     * <p>This method processes SEND_EMAIL commands by validating recipient addresses
     * and delivering the email to all valid recipients' inboxes. The email is
     * persisted to the file system and delivery tracking is initialized.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> Email object with sender, recipients, subject, and content</li>
     *   <li><strong>Output:</strong> Boolean indicating success/failure</li>
     * </ul>
     * 
     * @param input the input stream to read the email object from
     * @param output the output stream to send the response to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleSendEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        Email email = (Email) input.readObject();

        boolean success = emailService.sendEmail(email);
        output.writeObject(success);
        
        if(success) {
            LogService.info("Email successfully sent from " + email.getFrom() + " to " + email.getTo());
        } else {
            LogService.warn("Email could not be sent from " + email.getFrom());
        }
    }

    /**
     * Handles incremental email synchronization requests.
     * 
     * <p>This method processes FETCH_NEW_EMAIL commands by retrieving only emails
     * that have not been previously delivered to the requesting user. After successful
     * delivery, emails are marked as delivered to prevent duplicate synchronization
     * in future requests.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> User email address (String)</li>
     *   <li><strong>Output:</strong> List&lt;Email&gt; containing only new messages, null if user invalid</li>
     * </ul>
     * 
     * @param input the input stream to read the user email from
     * @param output the output stream to send the new emails to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleFetchNewEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String userEmail = (String) input.readObject();
        
        if (emailService.checkRegisteredAccount(userEmail)) {
            List<Email> newEmails = emailService.getNewEmails(userEmail);
            output.writeObject(newEmails);
            
            if (!newEmails.isEmpty()) {
                emailService.markEmailsAsDelivered(userEmail, newEmails);
            }
            
            LogService.info("Fetched " + newEmails.size() + " new emails for user: " + userEmail);
        } else {
            LogService.warn("Fetch request for non-existent user: " + userEmail);
            output.writeObject(null);
        }
    }

    /**
     * Handles email address validation requests.
     * 
     * <p>This method processes CHECK_EMAIL_EXISTS commands by verifying whether
     * a given email address corresponds to a registered account in the system.
     * Used by clients during email composition to validate recipient addresses.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> Email address to validate (String)</li>
     *   <li><strong>Output:</strong> Boolean indicating if the address exists</li>
     * </ul>
     * 
     * @param input the input stream to read the email address from
     * @param output the output stream to send the validation result to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleCheckEmailExists(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String email = (String) input.readObject();
        
        boolean exists = emailService.checkRegisteredAccount(email);
        output.writeObject(exists);
        
        LogService.debug("Email existence check for " + email + ": " + (exists ? "exists" : "not found"));
    }

    /**
     * Handles email deletion requests.
     * 
     * <p>This method processes DELETE_EMAIL commands by permanently removing
     * a specific email from a user's mailbox. The email is identified by its
     * unique ID and the change is persisted to the file system.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> User email address (String), Email ID (String)</li>
     *   <li><strong>Output:</strong> Boolean indicating success/failure</li>
     * </ul>
     * 
     * @param input the input stream to read the user email and email ID from
     * @param output the output stream to send the deletion result to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleDeleteEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String userEmail = (String) input.readObject();
        String emailId = (String) input.readObject();
        
        if (emailService.checkRegisteredAccount(userEmail)) {
            boolean deleted = emailService.deleteEmail(userEmail, emailId);
            output.writeObject(deleted);
            
            LogService.info("Delete email request for user " + userEmail + ", email ID " + emailId + ": " + (deleted ? "success" : "failed"));
        } else {
            LogService.warn("Delete request for non-existent user: " + userEmail);
            output.writeObject(false);
        }
    }

    /**
     * Handles email reply operations.
     * 
     * <p>This method processes REPLY_EMAIL commands by creating a new email
     * message addressed to the original sender. The reply is processed as a
     * normal email send operation with automatic recipient validation and delivery.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> Original email ID (String), Reply email object</li>
     *   <li><strong>Output:</strong> Boolean indicating success/failure</li>
     * </ul>
     * 
     * @param input the input stream to read the original email ID and reply email from
     * @param output the output stream to send the result to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleReplyEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String originalEmailId = (String) input.readObject();
        Email replyEmail = (Email) input.readObject();
        
        boolean success = emailService.replyToEmail(originalEmailId, replyEmail);
        output.writeObject(success);
        
        if (success) {
            LogService.info("Reply email sent from " + replyEmail.getFrom() + " to " + replyEmail.getTo());
        } else {
            LogService.warn("Reply email could not be sent from " + replyEmail.getFrom());
        }
    }

    /**
     * Handles reply-all email operations.
     * 
     * <p>This method processes REPLY_ALL_EMAIL commands by creating a new email
     * message addressed to the original sender and all original recipients
     * (excluding the current user). The reply-all is processed as a normal email
     * send operation with automatic recipient validation and delivery to all valid addresses.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> Original email ID (String), Reply-all email object</li>
     *   <li><strong>Output:</strong> Boolean indicating success/failure</li>
     * </ul>
     * 
     * @param input the input stream to read the original email ID and reply-all email from
     * @param output the output stream to send the result to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleReplyAllEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String originalEmailId = (String) input.readObject();
        Email replyAllEmail = (Email) input.readObject();
        
        boolean success = emailService.replyAllToEmail(originalEmailId, replyAllEmail);
        output.writeObject(success);
        
        if (success) {
            LogService.info("Reply-all email sent from " + replyAllEmail.getFrom() + " to " + replyAllEmail.getTo());
        } else {
            LogService.warn("Reply-all email could not be sent from " + replyAllEmail.getFrom());
        }
    }

    /**
     * Handles email forwarding operations.
     * 
     * <p>This method processes FORWARD_EMAIL commands by creating a new email
     * message with the original email content forwarded to new recipients
     * specified by the user. The forwarded email includes the original message
     * content and is processed as a normal email send operation with automatic
     * recipient validation and delivery.</p>
     * 
     * <h3>Protocol Details</h3>
     * <ul>
     *   <li><strong>Input:</strong> Original email ID (String), Forward email object</li>
     *   <li><strong>Output:</strong> Boolean indicating success/failure</li>
     * </ul>
     * 
     * @param input the input stream to read the original email ID and forward email from
     * @param output the output stream to send the result to
     * @throws IOException if network communication fails
     * @throws ClassNotFoundException if object deserialization fails
     */
    private void handleForwardEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String originalEmailId = (String) input.readObject();
        Email forwardEmail = (Email) input.readObject();
        
        boolean success = emailService.forwardEmail(originalEmailId, forwardEmail);
        output.writeObject(success);
        
        if (success) {
            LogService.info("Forward email sent from " + forwardEmail.getFrom() + " to " + forwardEmail.getTo());
        } else {
            LogService.warn("Forward email could not be sent from " + forwardEmail.getFrom());
        }
    }

}
