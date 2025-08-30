package it.ludovico.server.service;

import it.ludovico.shared.model.Email;

import java.util.List;

/**
 * Service interface defining the core email operations for the JavaFX Mail System.
 * 
 * <p>This interface abstracts the email business logic operations, providing a clean
 * contract for email management functionality. It defines methods for sending emails,
 * managing user accounts, handling mailbox operations, and supporting email actions
 * like reply, reply-all, and forward.</p>
 * 
 * <h2>Core Operations</h2>
 * <ul>
 *   <li><strong>Email Transmission:</strong> Send, reply, reply-all, and forward operations</li>
 *   <li><strong>Account Management:</strong> User registration and validation</li>
 *   <li><strong>Mailbox Operations:</strong> Retrieve, delete, and synchronize user emails</li>
 *   <li><strong>Delivery Tracking:</strong> Mark emails as delivered to prevent duplicates</li>
 * </ul>
 * 
 * <h2>Implementation Notes</h2>
 * <p>Implementations of this interface should ensure:</p>
 * <ul>
 *   <li>Thread safety for concurrent client access</li>
 *   <li>Proper error handling and logging</li>
 *   <li>Data persistence across server restarts</li>
 *   <li>Validation of user accounts and email addresses</li>
 * </ul>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see ServerModel
 * @see Email
 */
public interface EmailService {
    
    /**
     * Sends an email to all valid recipients.
     * 
     * @param email the email object to send
     * @return {@code true} if successfully sent, {@code false} otherwise
     */
    boolean sendEmail(Email email);
    
    /**
     * Validates that all provided email addresses are registered accounts.
     * 
     * @param accounts list of email addresses to validate
     * @return {@code true} if all accounts are valid, {@code false} otherwise
     */
    boolean checkToAccounts(List<String> accounts);
    
    /**
     * Checks if a single email address is a registered account.
     * 
     * @param account the email address to validate
     * @return {@code true} if the account is registered, {@code false} otherwise
     */
    boolean checkRegisteredAccount(String account);
    
    /**
     * Retrieves the complete mailbox for a user.
     * 
     * @param user the user's email address
     * @return list of all emails in the user's mailbox, or null if user invalid
     */
    List<Email> getUserMailbox(String user);
    
    /**
     * Retrieves only new (undelivered) emails for a user.
     * 
     * @param user the user's email address  
     * @return list of undelivered emails, or null if user invalid
     */
    List<Email> getNewEmails(String user);
    
    /**
     * Marks emails as delivered to a specific user.
     * 
     * @param user the user who received the emails
     * @param emails the emails to mark as delivered
     */
    void markEmailsAsDelivered(String user, List<Email> emails);
    
    /**
     * Permanently deletes an email from a user's mailbox.
     * 
     * @param user the user's email address
     * @param emailId the unique ID of the email to delete
     * @return {@code true} if successfully deleted, {@code false} otherwise
     */
    boolean deleteEmail(String user, String emailId);
    
    /**
     * Processes a reply to an existing email.
     * 
     * @param originalEmailId the ID of the original email being replied to
     * @param replyEmail the reply email to send
     * @return {@code true} if successfully sent, {@code false} otherwise
     */
    boolean replyToEmail(String originalEmailId, Email replyEmail);
    
    /**
     * Processes a reply-all to an existing email.
     * 
     * @param originalEmailId the ID of the original email being replied to
     * @param replyEmail the reply-all email to send
     * @return {@code true} if successfully sent, {@code false} otherwise
     */
    boolean replyAllToEmail(String originalEmailId, Email replyEmail);
    
    /**
     * Processes forwarding of an existing email.
     * 
     * @param originalEmailId the ID of the original email being forwarded
     * @param forwardEmail the forward email to send
     * @return {@code true} if successfully sent, {@code false} otherwise
     */
    boolean forwardEmail(String originalEmailId, Email forwardEmail);
}
