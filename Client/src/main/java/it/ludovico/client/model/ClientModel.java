package it.ludovico.client.model;

import it.ludovico.client.service.AlertService;
import it.ludovico.client.service.ClientService;
import it.ludovico.shared.model.Email;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Client-side data model for the JavaFX Mail System application.
 * 
 * <p>This class serves as the primary data container and state manager for the mail client.
 * It maintains the user's identity, email collection, and connection status while providing
 * JavaFX observable properties for seamless UI data binding and real-time updates.</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>User Management:</strong> Stores and provides access to the current user's email address</li>
 *   <li><strong>Email Storage:</strong> Observable list of emails for automatic UI synchronization</li>
 *   <li><strong>Connection Status:</strong> Tracks and reports server connection state</li>
 *   <li><strong>JavaFX Integration:</strong> Properties support automatic UI binding and updates</li>
 *   <li><strong>Thread Safety:</strong> Safe for use with JavaFX threading model</li>
 * </ul>
 * 
 * <h2>Observable Properties</h2>
 * <p>The model provides JavaFX properties that automatically notify listeners of changes:
 * <ul>
 *   <li><strong>Connection Status:</strong> BooleanProperty for server connectivity state</li>
 *   <li><strong>Email List:</strong> ObservableList for real-time email collection updates</li>
 * </ul></p>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * ClientModel model = new ClientModel("user@example.com");
 * 
 * // Bind UI elements to model properties
 * statusLabel.textProperty().bind(
 *     Bindings.when(model.connectionStatusProperty())
 *         .then("Connected")
 *         .otherwise("Disconnected")
 * );
 * 
 * // Add emails to the model
 * model.addNewEmails(FXCollections.observableList(newEmails));
 * }</pre>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see Email
 * @see ClientService
 */
public class ClientModel {
    /**
     * The email address of the current user.
     * This is immutable once the model is created and serves as the user's identity.
     */
    private final String user;
    
    /**
     * Observable list of emails belonging to the user.
     * Automatically notifies UI components when emails are added, removed, or modified.
     */
    private final ObservableList<Email> emails;
    
    /**
     * Observable property indicating the connection status to the mail server.
     * True when connected and able to communicate with the server, false otherwise.
     */
    private final BooleanProperty connectionStatus;

    /**
     * Constructs a new ClientModel for the specified user.
     * 
     * <p>Initializes the model with the user's email address and creates empty
     * collections for emails. The connection status is initially set to false,
     * indicating no server connection has been established yet.</p>
     * 
     * @param user the email address of the user; must not be null or empty
     * @throws IllegalArgumentException if user is null or empty
     */
    public ClientModel(String user) {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("User email address cannot be null or empty");
        }
        this.user = user;
        this.emails = FXCollections.observableList(new ArrayList<>());
        this.connectionStatus = new SimpleBooleanProperty(false);
    }

    /**
     * Returns the email address of the current user.
     * 
     * <p>This email address serves as the user's identity in the mail system
     * and is used for authentication and as the sender address for outgoing emails.</p>
     * 
     * @return the user's email address
     */
    public String getUser() {
        return user;
    }
    
    /**
     * Returns the observable list of emails for this user.
     * 
     * <p>This list automatically notifies bound UI components when emails are
     * added, removed, or modified. The list contains all emails currently
     * available in the client's local cache.</p>
     * 
     * @return the observable list of emails
     */
    public ObservableList<Email> getEmails() {
        return emails;
    }
    
    /**
     * Returns the current connection status to the mail server.
     * 
     * <p>This method provides direct access to the current connection state.
     * For UI binding, consider using {@link #connectionStatusProperty()} instead.</p>
     * 
     * @return {@code true} if connected to the server, {@code false} otherwise
     */
    public boolean getConnectionStatus() {
        return connectionStatus.get();
    }

    /**
     * Returns the observable connection status property for UI binding.
     * 
     * <p>This property automatically notifies bound UI components when the
     * connection status changes, enabling real-time updates of connection
     * indicators and other status-dependent UI elements.</p>
     * 
     * @return the observable boolean property for connection status
     */
    public BooleanProperty connectionStatusProperty() {
        return connectionStatus;
    }
    /**
     * Sets the connection status to the mail server.
     * 
     * <p>This method updates the connection status and automatically notifies
     * any UI components bound to the connection status property. Typically called
     * by the {@link ClientService} when connection state changes.</p>
     * 
     * @param status {@code true} if connected to the server, {@code false} otherwise
     */
    public void setConnectionStatus(boolean status) {
        connectionStatus.set(status);
    }

    /**
     * Adds new emails to the user's email collection.
     * 
     * <p>This method appends new emails to the existing collection and automatically
     * notifies bound UI components of the changes. Null or empty collections are
     * ignored to prevent unnecessary updates.</p>
     * 
     * <p>Typically called when receiving new emails from the server during
     * refresh operations or real-time updates.</p>
     * 
     * @param newEmails the collection of new emails to add; null or empty collections are ignored
     */
    public void addNewEmails(ObservableList<Email> newEmails) {
        if (newEmails != null && !newEmails.isEmpty()) {
            emails.addAll(newEmails);
        }
    }
    
    /**
     * Removes all emails from the user's collection.
     * 
     * <p>This method clears the entire email list and automatically notifies
     * bound UI components. Typically used during logout or when refreshing
     * the complete mailbox from the server.</p>
     */
    public void clearEmails() {
        emails.clear();
    }
    
    /**
     * Returns the total number of emails in the user's collection.
     * 
     * <p>This method provides a convenient way to get the current email count
     * without directly accessing the emails list. Useful for displaying
     * message counts in the UI.</p>
     * 
     * @return the number of emails currently in the collection
     */
    public int getEmailCount() {
        return emails.size();
    }

}
