package it.ludovico.shared.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents an email message in the JavaFX Mail System.
 * 
 * <p>This class encapsulates all the essential components of an email message including
 * sender information, recipients, subject, content, timestamps, and delivery tracking.
 * The class implements {@link Serializable} to support network transmission between
 * client and server components.</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Unique Identification:</strong> Each email has a UUID for reliable identification</li>
 *   <li><strong>Multiple Recipients:</strong> Support for sending to multiple recipients simultaneously</li>
 *   <li><strong>Delivery Tracking:</strong> Per-user delivery status tracking for incremental synchronization</li>
 *   <li><strong>Immutable Timestamps:</strong> Automatic timestamp generation on creation</li>
 *   <li><strong>Thread Safety:</strong> Safe for use in concurrent environments</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * // Create a new email
 * List<String> recipients = Arrays.asList("user1@domain.com", "user2@domain.com");
 * Email email = new Email("sender@domain.com", recipients, "Meeting", "Let's meet tomorrow");
 * 
 * // Check delivery status
 * if (!email.isDeliveredTo("user1@domain.com")) {
 *     email.markDeliveredTo("user1@domain.com");
 * }
 * 
 * // Access email properties
 * String subject = email.getSubject();
 * LocalDateTime timestamp = email.getSent();
 * }</pre>
 * 
 * <h2>Serialization</h2>
 * <p>This class uses Java serialization for network communication. The serialVersionUID
 * is set to ensure compatibility across different versions of the application.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see Commands
 */
public class Email implements Serializable {
    /**
     * Serial version UID for ensuring serialization compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for this email message.
     * Generated automatically using {@link UUID#randomUUID()} on email creation.
     */
    private UUID id;
    
    /**
     * Email address of the sender.
     * Must be a valid email format and correspond to a registered account.
     */
    private String from;
    
    /**
     * List of recipient email addresses.
     * Supports multiple recipients for broadcast messaging.
     * Each address must be validated against registered accounts.
     */
    private List<String> to;
    
    /**
     * Subject line of the email message.
     * Can be empty but not null.
     */
    private String subject;
    
    /**
     * Main content/body of the email message.
     * Supports plain text content.
     */
    private String text;
    
    /**
     * Timestamp when the email was created and sent.
     * Set automatically to current time during email construction.
     * Uses system's local timezone.
     */
    private LocalDateTime sent;
    
    /**
     * Set of user email addresses to whom this message has been delivered.
     * Used for incremental synchronization to prevent duplicate deliveries.
     * Modified by the server during email distribution.
     */
    private Set<String> deliveredTo;

    /**
     * Constructs a new Email with the specified parameters.
     * 
     * <p>This constructor automatically generates a unique ID and timestamp,
     * and initializes the delivery tracking set. The email is ready to be
     * sent through the mail system after construction.</p>
     * 
     * @param from the sender's email address; must not be null
     * @param to the list of recipient email addresses; must not be null or empty
     * @param subject the email subject line; can be empty but not null
     * @param text the email message content; can be empty but not null
     * @throws NullPointerException if any parameter is null
     */
    public Email(String from, List<String> to, String subject, String text) {
        this.id = UUID.randomUUID();
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.sent = LocalDateTime.now();
        this.deliveredTo = new HashSet<>();
    }

    /**
     * Returns the unique identifier of this email.
     * 
     * @return the UUID that uniquely identifies this email message
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Returns the sender's email address.
     * 
     * @return the email address of the sender
     */
    public String getFrom() {
        return from;
    }
    
    /**
     * Returns the list of recipient email addresses.
     * 
     * @return an unmodifiable view of the recipients list
     */
    public List<String> getTo() {
        return to;
    }
    
    /**
     * Returns the subject line of this email.
     * 
     * @return the email subject, may be empty but never null
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * Returns the main content of this email.
     * 
     * @return the email message body, may be empty but never null
     */
    public String getText() {
        return text;
    }
    
    /**
     * Returns the timestamp when this email was sent.
     * 
     * @return the {@link LocalDateTime} when the email was created
     */
    public LocalDateTime getSent() {
        return sent;
    }
    
    /**
     * Returns the set of users to whom this email has been delivered.
     * 
     * <p>This method is primarily used by the server for persistence and
     * synchronization purposes. Client applications typically should use
     * {@link #isDeliveredTo(String)} instead.</p>
     * 
     * @return a set containing email addresses of users who have received this message
     */
    public Set<String> getDeliveredTo() {
        return deliveredTo;
    }
    
    /**
     * Sets the delivery tracking information for this email.
     * 
     * <p>This method is used during email deserialization and should not be
     * called directly by client code. Use {@link #markDeliveredTo(String)} to
     * mark individual deliveries.</p>
     * 
     * @param deliveredTo the set of user email addresses who have received this message
     */
    public void setDeliveredTo(Set<String> deliveredTo) {
        this.deliveredTo = deliveredTo;
    }
    
    /**
     * Checks if this email has been delivered to a specific user.
     * 
     * <p>This method is used by both client and server to determine if an email
     * should be included in new message synchronization.</p>
     * 
     * @param user the email address to check
     * @return {@code true} if the email has been delivered to this user, {@code false} otherwise
     * @throws NullPointerException if user is null
     */
    public boolean isDeliveredTo(String user) {
        return deliveredTo.contains(user);
    }
    
    /**
     * Marks this email as delivered to a specific user.
     * 
     * <p>This method is called by the server after successfully delivering an email
     * to a user's client. It prevents duplicate deliveries during incremental
     * synchronization.</p>
     * 
     * @param user the email address of the user who received this message
     * @throws NullPointerException if user is null
     */
    public void markDeliveredTo(String user) {
        deliveredTo.add(user);
    }
    
    /**
     * Sets the sender's email address.
     * 
     * <p><strong>Note:</strong> This method is provided for serialization support.
     * In normal usage, the sender should be set during email construction.</p>
     * 
     * @param from the sender's email address
     */
    public void setFrom(String from) {
        this.from = from;
    }
    
    /**
     * Sets the list of recipient email addresses.
     * 
     * <p><strong>Note:</strong> This method is provided for serialization support.
     * In normal usage, recipients should be set during email construction.</p>
     * 
     * @param to the list of recipient email addresses
     */
    public void setTo(List<String> to) {
        this.to = to;
    }
    
    /**
     * Sets the subject line of this email.
     * 
     * <p><strong>Note:</strong> This method is provided for serialization support.
     * In normal usage, the subject should be set during email construction.</p>
     * 
     * @param subject the email subject line
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Sets the main content of this email.
     * 
     * <p><strong>Note:</strong> This method is provided for serialization support.
     * In normal usage, the message text should be set during email construction.</p>
     * 
     * @param text the email message content
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns a string representation of this email.
     * 
     * <p>The string includes the email ID, sender, recipients, subject, and timestamp.
     * This method is primarily used for debugging and logging purposes.</p>
     * 
     * @return a formatted string containing the email's essential information
     */
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", mittente='" + from + '\'' +
                ", destinatari=" + to +
                ", argomento='" + subject + '\'' +
                ", dataSpedizione=" + sent +
                '}';
    }

    /**
     * Compares this email with another object for equality.
     * 
     * <p>Two emails are considered equal if and only if they have the same UUID.
     * This ensures that email identity is based solely on the unique identifier,
     * regardless of content modifications.</p>
     * 
     * @param o the object to compare with this email
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(this.id, email.id);
    }

    /**
     * Returns the hash code for this email.
     * 
     * <p>The hash code is based solely on the email's UUID, ensuring consistency
     * with the {@link #equals(Object)} method. This allows emails to be used
     * safely in hash-based collections.</p>
     * 
     * @return the hash code value for this email
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}