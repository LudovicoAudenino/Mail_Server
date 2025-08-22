package it.ludovico.client.model;

import it.ludovico.shared.model.Email;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ClientModel {
    private final String user;
    private final ObservableList<Email> emails;
    private final BooleanProperty connectionStatus;
    private final StringProperty statusMessage;

    public ClientModel(String user) {
        this.user = user;
        this.emails = FXCollections.observableList(new ArrayList<>());
        this.connectionStatus = new SimpleBooleanProperty(false);
        this.statusMessage = new SimpleStringProperty("Disconnected");
    }

    // Getters
    public String getUser() {
        return user;
    }
    
    public ObservableList<Email> getEmails() {
        return emails;
    }
    
    public boolean getConnectionStatus() {
        return connectionStatus.get();
    }
    
    public String getStatusMessage() {
        return statusMessage.get();
    }

    // Setters
    public void setConnectionStatus(boolean status) {
        connectionStatus.set(status);
        statusMessage.set(status ? "Connected" : "Disconnected");
    }
    
    public void setStatusMessage(String message) {
        statusMessage.set(message);
    }

    // JavaFX Properties for binding
    public BooleanProperty connectionStatusProperty() {
        return connectionStatus;
    }
    
    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    // Utility methods
    public void addNewEmails(ObservableList<Email> newEmails) {
        if (newEmails != null && !newEmails.isEmpty()) {
            emails.addAll(newEmails);
        }
    }
    
    public void clearEmails() {
        emails.clear();
    }
    
    public int getEmailCount() {
        return emails.size();
    }
}
