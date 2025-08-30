package it.ludovico.shared.model;

/**
 * Protocol command constants for client-server communication in the JavaFX Mail System.
 * 
 * <p>This class defines the complete set of commands used in the stateless HTTP-like
 * communication protocol between the mail client and server. Each command represents
 * a specific operation that can be performed on the mail system.</p>
 * 
 * <h2>Protocol Design</h2>
 * <p>The communication follows a request-response pattern where:</p>
 * <ul>
 *   <li>Client opens a new socket connection for each operation</li>
 *   <li>Client sends a command string followed by any required parameters</li>
 *   <li>Server processes the command and returns a response</li>
 *   <li>Connection is closed immediately after the response</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Client side
 * output.writeObject(Commands.LOGIN);
 * output.writeObject(userEmail);
 * 
 * // Server side
 * String command = (String) input.readObject();
 * switch (command) {
 *     case Commands.LOGIN:
 *         handleLogin(input, output);
 *         break;
 * }
 * }</pre>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 */
public final class Commands {
    
    /**
     * Command for user authentication and initial mailbox retrieval.
     * 
     * <p><strong>Client sends:</strong> User email address (String)</p>
     * <p><strong>Server responds:</strong> List&lt;Email&gt; if authentication successful, null otherwise</p>
     * 
     * <p>This command validates the user email against registered accounts and returns
     * the complete mailbox if the account exists. This is the entry point for all
     * client sessions.</p>
     */
    public static final String LOGIN = "LOGIN";
    
    /**
     * Command for sending a new email message.
     * 
     * <p><strong>Client sends:</strong> Email object containing sender, recipients, subject, and body</p>
     * <p><strong>Server responds:</strong> Boolean indicating success/failure</p>
     * 
     * <p>The server validates recipient addresses against registered accounts before
     * delivery. The email is delivered to all valid recipients' inboxes and persisted
     * to the file system.</p>
     */
    public static final String SEND_EMAIL = "SEND_EMAIL";
    
    /**
     * Command for retrieving new (undelivered) emails for a user.
     * 
     * <p><strong>Client sends:</strong> User email address (String)</p>
     * <p><strong>Server responds:</strong> List&lt;Email&gt; containing only new messages</p>
     * 
     * <p>This command implements incremental email synchronization. The server tracks
     * delivery status per user to ensure emails are only delivered once. After successful
     * delivery, emails are marked as delivered for the requesting user.</p>
     */
    public static final String FETCH_NEW_EMAIL = "FETCH_NEW_EMAIL";
    
    /**
     * Command for validating email address existence.
     * 
     * <p><strong>Client sends:</strong> Email address to validate (String)</p>
     * <p><strong>Server responds:</strong> Boolean indicating if address exists</p>
     * 
     * <p>Used by the client during email composition to verify recipient addresses
     * before attempting to send. Only registered accounts are considered valid.</p>
     */
    public static final String CHECK_EMAIL_EXISTS = "CHECK_EMAIL_EXISTS";
    
    /**
     * Command for permanently removing an email from a user's inbox.
     * 
     * <p><strong>Client sends:</strong> User email address (String), Email ID (String)</p>
     * <p><strong>Server responds:</strong> Boolean indicating success/failure</p>
     * 
     * <p>The email is permanently removed from the user's mailbox and the change
     * is persisted to the file system. This operation cannot be undone.</p>
     */
    public static final String DELETE_EMAIL = "DELETE_EMAIL";
    
    /**
     * Command for replying to an existing email message.
     * 
     * <p><strong>Client sends:</strong> Original email ID (String), Reply email object</p>
     * <p><strong>Server responds:</strong> Boolean indicating success/failure</p>
     * 
     * <p>Creates a new email message addressed to the original sender. The reply
     * is processed as a normal email send operation with automatic recipient
     * validation and delivery.</p>
     */
    public static final String REPLY_EMAIL = "REPLY_EMAIL";
    
    /**
     * Command for replying to all recipients of an existing email message.
     * 
     * <p><strong>Client sends:</strong> Original email ID (String), Reply-all email object</p>
     * <p><strong>Server responds:</strong> Boolean indicating success/failure</p>
     * 
     * <p>Creates a new email message addressed to the original sender and all
     * original recipients (excluding the current user). Recipients are validated
     * and the message is delivered to all valid addresses.</p>
     */
    public static final String REPLY_ALL_EMAIL = "REPLY_ALL_EMAIL";
    
    /**
     * Command for forwarding an existing email message to new recipients.
     * 
     * <p><strong>Client sends:</strong> Original email ID (String), Forward email object</p>
     * <p><strong>Server responds:</strong> Boolean indicating success/failure</p>
     * 
     * <p>Creates a new email message with the original content forwarded to
     * new recipients specified by the user. All recipient addresses are validated
     * before delivery.</p>
     */
    public static final String FORWARD_EMAIL = "FORWARD_EMAIL";
    
    /**
     * Command for graceful client disconnection (currently unused).
     * 
     * <p><strong>Client sends:</strong> No additional data</p>
     * <p><strong>Server responds:</strong> No response expected</p>
     * 
     * <p><em>Note:</em> This command is defined but not implemented in the current
     * version. The stateless protocol design makes explicit disconnection unnecessary
     * as connections are closed after each operation.</p>
     */
    public static final String DISCONNECT = "DISCONNECT";
    
    /**
     * Command for server connectivity testing and status monitoring.
     * 
     * <p><strong>Client sends:</strong> No additional data</p>
     * <p><strong>Server responds:</strong> "PONG" string</p>
     * 
     * <p>Used by the client's heartbeat mechanism to monitor server availability.
     * This lightweight command provides a quick way to test connectivity without
     * affecting user data or performing expensive operations.</p>
     */
    public static final String PING = "PING";
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static constants.
     */
    private Commands() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
}