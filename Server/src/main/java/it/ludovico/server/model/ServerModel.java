package it.ludovico.server.model;


import it.ludovico.shared.model.Email;
import it.ludovico.server.repository.MailboxesRepository;
import it.ludovico.server.service.EmailService;
import it.ludovico.server.service.LogService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core business logic implementation for the JavaFX Mail System server.
 * 
 * <p>This class serves as the primary model for the mail server, implementing the
 * {@link EmailService} interface and providing comprehensive email management
 * functionality. It handles user authentication, email storage and retrieval,
 * delivery tracking, and real-time logging for server monitoring.</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Account Management:</strong> Manages registered user accounts with automatic initialization</li>
 *   <li><strong>Email Operations:</strong> Handles sending, retrieving, and deleting emails</li>
 *   <li><strong>Delivery Tracking:</strong> Implements incremental synchronization with per-user delivery status</li>
 *   <li><strong>Real-time Monitoring:</strong> Provides observable properties for UI binding and logging</li>
 *   <li><strong>Thread Safety:</strong> Uses atomic operations for concurrent access</li>
 *   <li><strong>Data Persistence:</strong> Integrates with repository layer for email storage</li>
 * </ul>
 * 
 * <h2>Registered Accounts</h2>
 * <p>The system currently supports three hardcoded accounts:</p>
 * <ul>
 *   <li>ludo@gmail.com</li>
 *   <li>franci@gmail.com</li>
 *   <li>elisa@gmail.com</li>
 * </ul>
 * 
 * <h2>Thread Safety</h2>
 * <p>This class is designed to be thread-safe for concurrent client connections.
 * It uses {@link AtomicBoolean} and {@link AtomicInteger} for state management
 * and JavaFX Platform.runLater() for UI property updates.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see EmailService
 * @see MailboxesRepository
 */
public class ServerModel implements EmailService {

    /**
     * Observable list of server log messages for real-time monitoring.
     * Used by the server UI to display live logging information.
     */
    private final ObservableList<String> logs = FXCollections.observableArrayList();
    
    /**
     * Thread-safe boolean indicating whether the server is currently running.
     * Used for concurrent access control across multiple client threads.
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    /**
     * Thread-safe counter for the total number of requests processed by the server.
     * Incremented atomically for each client request handled.
     */
    private final AtomicInteger requestsProcessed = new AtomicInteger(0);
    
    /**
     * JavaFX property for binding server running status to UI components.
     * Updates are performed on the JavaFX Application Thread.
     */
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);
    
    /**
     * JavaFX property for binding request count to UI components.
     * Updates are performed on the JavaFX Application Thread.
     */
    private final IntegerProperty requestsProcessedProperty = new SimpleIntegerProperty(0);

    /**
     * Repository instance for managing email data persistence.
     * Handles file-based storage and retrieval of mailboxes.
     */
    private final MailboxesRepository mailboxesRepository;

    /**
     * List of registered email accounts that can send and receive emails.
     * These accounts are automatically initialized when the server starts.
     */
    private static final List<String> ACCOUNT_REGISTERED = Arrays.asList(
            "ludo@gmail.com", "franci@gmail.com", "elisa@gmail.com"
    );

    /**
     * Constructs a new ServerModel instance and initializes the mail system.
     * 
     * <p>This constructor performs the following initialization steps:</p>
     * <ol>
     *   <li>Sets up the log handler to capture server log messages</li>
     *   <li>Creates a new MailboxesRepository for email persistence</li>
     *   <li>Initializes all registered accounts in the repository</li>
     * </ol>
     * 
     * <p>The constructor ensures that all required accounts exist in the repository
     * and creates them if they are missing. This guarantees a consistent server state
     * upon startup.</p>
     */
    public ServerModel() {
        LogService.setLogHandler(logs::add);
        this.mailboxesRepository = new MailboxesRepository();
        initializeAccounts();
    }

    /**
     * Validates that all provided email addresses correspond to registered accounts.
     * 
     * <p>This method checks each email address in the provided list against the
     * {@link #ACCOUNT_REGISTERED} list. All addresses must be valid for the method
     * to return true. This is used to validate recipient lists before sending emails.</p>
     * 
     * @param accounts the list of email addresses to validate
     * @return {@code true} if all accounts are registered, {@code false} otherwise
     * @throws NullPointerException if accounts is null
     */
    public boolean checkToAccounts(List<String> accounts) {
        for (String account : accounts) {
            if (!ACCOUNT_REGISTERED.contains(account)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a single email address is registered in the system.
     * 
     * <p>This method verifies whether the provided email address corresponds
     * to a registered account that can send and receive emails. Used for
     * authentication and recipient validation.</p>
     * 
     * @param account the email address to check
     * @return {@code true} if the account is registered, {@code false} otherwise
     */
    public boolean checkRegisteredAccount(String account) {
        return ACCOUNT_REGISTERED.contains(account);
    }

    /**
     * Sends an email to all valid recipients and persists it to storage.
     * 
     * <p>This method implements the core email sending functionality by:
     * <ol>
     *   <li>Validating the email object and all recipient addresses</li>
     *   <li>Adding the email to each recipient's mailbox</li>
     *   <li>Persisting the changes to the file system</li>
     * </ol></p>
     * 
     * <p>The email is only sent if all recipient addresses are valid registered
     * accounts. If any recipient is invalid, the entire send operation fails.</p>
     * 
     * @param email the email object to send; must not be null
     * @return {@code true} if the email was successfully sent and persisted, {@code false} otherwise
     */
    public boolean sendEmail(Email email) {
        if (email != null &&  checkToAccounts(email.getTo())) {
            for (String account : email.getTo()) {
                mailboxesRepository.addEmail(account, email);
            }
            try {
                mailboxesRepository.saveMailBoxes();
                return true;
            } catch (IOException e) {
                LogService.error("Error saving emails: " + e.getMessage());
                return false;
            }

        }
        return false;
    }

    /**
     * Initializes all registered accounts in the repository.
     * 
     * <p>This method ensures that all accounts defined in {@link #ACCOUNT_REGISTERED}
     * exist in the repository. If any account is missing, it creates the account
     * and saves the repository state. This method is called during server startup
     * to guarantee system consistency.</p>
     * 
     * <p>The initialization process:
     * <ol>
     *   <li>Retrieves existing accounts from the repository</li>
     *   <li>Checks each registered account for existence</li>
     *   <li>Creates missing accounts with empty mailboxes</li>
     *   <li>Persists changes if any accounts were created</li>
     * </ol></p>
     */
    public void initializeAccounts() {
        Set<String> existingAccounts = mailboxesRepository.getAccountMails();
        boolean needsSave = false;
        
        for(String account : ACCOUNT_REGISTERED) {
            if (!existingAccounts.contains(account)) {
                mailboxesRepository.addAccount(account);
                needsSave = true;
                LogService.info("Inizializzato account mancante: " + account);
            }
        }
        
        if (needsSave) {
            try {
                mailboxesRepository.saveMailBoxes();
            } catch (IOException e) {
                LogService.error("Error saving during initialization: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves the complete mailbox for a specified user.
     * 
     * <p>This method returns all emails in the user's mailbox, regardless of
     * delivery status. If the user is registered but their mailbox doesn't exist,
     * it creates an empty mailbox and persists it to storage.</p>
     * 
     * <p>Used primarily during initial login to retrieve the user's complete
     * email history for display in the client application.</p>
     * 
     * @param user the email address of the user whose mailbox to retrieve
     * @return a list of all emails in the user's mailbox, or null if user is not registered
     */
    public List<Email> getUserMailbox(String user) {
        List<Email> mailbox = mailboxesRepository.getMailBox(user);
        if (mailbox == null && checkRegisteredAccount(user)) {
            mailboxesRepository.addAccount(user);
            try {
                mailboxesRepository.saveMailBoxes();
                LogService.info("Created missing mailbox for: " + user);
                return new ArrayList<>();
            } catch (IOException e) {
                LogService.error("Error creating mailbox for " + user + ": " + e.getMessage());
                return null;
            }
        }
        return mailbox;
    }

    /**
     * Retrieves only new (undelivered) emails for a specified user.
     * 
     * <p>This method implements incremental email synchronization by filtering
     * the user's mailbox to return only emails that have not been delivered
     * to the user's client yet. This prevents duplicate email delivery during
     * subsequent synchronization requests.</p>
     * 
     * <p>The delivery status is tracked per email using the {@link Email#isDeliveredTo(String)}
     * method. After emails are successfully delivered, they should be marked as delivered
     * using {@link #markEmailsAsDelivered(String, List)}.</p>
     * 
     * @param user the email address of the user requesting new emails
     * @return a list of undelivered emails, or null if the user is not registered
     */
    public List<Email> getNewEmails(String user) {
        List<Email> mailbox = getUserMailbox(user);
        if (mailbox == null) {
            return null;
        }
        return mailbox.stream()
                .filter(email -> !email.isDeliveredTo(user))
                .toList();
    }

    /**
     * Marks a list of emails as delivered to a specific user.
     * 
     * <p>This method is called after successfully delivering emails to a client
     * to prevent duplicate deliveries in future synchronization requests. Each
     * email in the list is marked as delivered to the specified user, and the
     * changes are persisted to storage.</p>
     * 
     * <p>This is a critical part of the incremental synchronization system that
     * ensures emails are only delivered once to each client.</p>
     * 
     * @param user the email address of the user who received the emails
     * @param emails the list of emails to mark as delivered
     */
    public void markEmailsAsDelivered(String user, List<Email> emails) {
        for (Email email : emails) {
            email.markDeliveredTo(user);
        }
        try {
            mailboxesRepository.saveMailBoxes();
        } catch (IOException e) {
            LogService.error("Error saving after marking emails as delivered: " + e.getMessage());
        }
    }

    /**
     * Returns the observable list of server log messages.
     * 
     * <p>This list contains real-time log messages from the server and is used
     * by the server UI for displaying live logging information. The list is
     * automatically updated when log messages are generated throughout the server.</p>
     * 
     * @return the observable list of log messages for UI binding
     */
    public ObservableList<String> getLogs() {
        return logs;
    }

    /**
     * Returns the current running status of the server.
     * 
     * <p>This method provides thread-safe access to the server's running state
     * using the underlying {@link AtomicBoolean}. Used by other components to
     * check if the server is currently active.</p>
     * 
     * @return {@code true} if the server is running, {@code false} otherwise
     */
    public Boolean getRunning() {
        return running.get();
    }

    /**
     * Returns the atomic counter for processed requests.
     * 
     * <p>This method provides direct access to the {@link AtomicInteger} that
     * tracks the total number of client requests processed by the server.
     * Used for monitoring server activity and performance metrics.</p>
     * 
     * @return the atomic integer representing total requests processed
     */
    public AtomicInteger getRequestsProcessed() {
        return requestsProcessed;
    }

    /**
     * Atomically increments the processed requests counter and updates UI properties.
     * 
     * <p>This method is called by the server service each time a client request
     * is processed. It safely increments the atomic counter and schedules a UI
     * property update on the JavaFX Application Thread.</p>
     * 
     * <p>The method ensures thread-safe operation while maintaining proper UI
     * data binding for real-time monitoring displays.</p>
     */
    public void incrementRequestsProcessed() {
        int count = requestsProcessed.incrementAndGet();
        Platform.runLater(() -> requestsProcessedProperty.set(count));
    }

    /**
     * Sets the server running status and updates UI properties.
     * 
     * <p>This method updates both the atomic boolean state and the JavaFX
     * property for UI binding. The UI property update is scheduled on the
     * JavaFX Application Thread to ensure proper thread safety.</p>
     * 
     * @param running the new running status for the server
     */
    public void setRunning(boolean running) {
        this.running.set(running);
        Platform.runLater(() -> runningProperty.set(running));
    }

    /**
     * Returns the JavaFX boolean property for server running status.
     * 
     * <p>This property is used for binding the server's running state to UI
     * components. Updates are automatically synchronized with the underlying
     * atomic boolean state.</p>
     * 
     * @return the boolean property representing server running status
     */
    public BooleanProperty runningProperty() {
        return runningProperty;
    }
    
    /**
     * Returns the JavaFX integer property for processed requests count.
     * 
     * <p>This property is used for binding the processed requests counter to UI
     * components for real-time monitoring. Updates are automatically synchronized
     * with the underlying atomic integer.</p>
     * 
     * @return the integer property representing total processed requests
     */
    public IntegerProperty requestsProcessedProperty() {
        return requestsProcessedProperty;
    }

    /**
     * Permanently deletes an email from a user's mailbox.
     * 
     * <p>This method locates and removes a specific email from the user's mailbox
     * based on the email's unique ID. The deletion is persisted to storage and
     * cannot be undone. If the email is not found or the user is not registered,
     * the operation fails.</p>
     * 
     * <p>The deletion process:
     * <ol>
     *   <li>Retrieves the user's mailbox</li>
     *   <li>Searches for the email by ID</li>
     *   <li>Removes the email from the repository</li>
     *   <li>Persists the changes to storage</li>
     * </ol></p>
     * 
     * @param user the email address of the user who owns the mailbox
     * @param emailId the unique ID of the email to delete
     * @return {@code true} if the email was successfully deleted, {@code false} otherwise
     */
    public boolean deleteEmail(String user, String emailId) {
        List<Email> mailbox = getUserMailbox(user);
        if (mailbox == null) {
            return false;
        }
        
        Email emailToDelete = null;
        for (Email email : mailbox) {
            if (email.getId().toString().equals(emailId)) {
                emailToDelete = email;
                break;
            }
        }
        
        if (emailToDelete != null) {
            mailboxesRepository.deleteEmail(user, emailToDelete);
            try {
                mailboxesRepository.saveMailBoxes();
                LogService.info("Email deleted for user " + user + ": " + emailId);
                return true;
            } catch (IOException e) {
                LogService.error("Error saving after deleting email: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Processes a reply email by sending it as a new email message.
     * 
     * <p>This method implements the reply functionality by delegating to the
     * standard {@link #sendEmail(Email)} method. The reply email should already
     * be properly formatted with the correct recipient (original sender) and
     * subject line by the client before being sent to this method.</p>
     * 
     * @param originalEmailId the ID of the original email being replied to (currently unused)
     * @param replyEmail the reply email object with sender, recipient, subject, and content
     * @return {@code true} if the reply was successfully sent, {@code false} otherwise
     */
    @Override
    public boolean replyToEmail(String originalEmailId, Email replyEmail) {
        return sendEmail(replyEmail);
    }

    /**
     * Processes a reply-all email by sending it as a new email message.
     * 
     * <p>This method implements the reply-all functionality by delegating to the
     * standard {@link #sendEmail(Email)} method. The reply-all email should already
     * be properly formatted with all original recipients (excluding the current user)
     * and the appropriate subject line by the client before being sent to this method.</p>
     * 
     * @param originalEmailId the ID of the original email being replied to (currently unused)
     * @param replyEmail the reply-all email object with sender, recipients, subject, and content
     * @return {@code true} if the reply-all was successfully sent, {@code false} otherwise
     */
    @Override
    public boolean replyAllToEmail(String originalEmailId, Email replyEmail) {
        return sendEmail(replyEmail);
    }

    /**
     * Processes a forwarded email by sending it as a new email message.
     * 
     * <p>This method implements the forward functionality by delegating to the
     * standard {@link #sendEmail(Email)} method. The forwarded email should already
     * be properly formatted with the new recipients, updated subject line, and
     * the original email content included by the client before being sent to this method.</p>
     * 
     * @param originalEmailId the ID of the original email being forwarded (currently unused)
     * @param forwardEmail the forward email object with sender, recipients, subject, and content
     * @return {@code true} if the forward was successfully sent, {@code false} otherwise
     */
    @Override
    public boolean forwardEmail(String originalEmailId, Email forwardEmail) {
        return sendEmail(forwardEmail);
    }

}