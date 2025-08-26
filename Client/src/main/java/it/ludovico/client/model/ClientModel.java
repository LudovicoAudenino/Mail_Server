package it.ludovico.client.model;

import it.ludovico.client.service.AlertService;
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

    public ClientModel(String user) {
        this.user = user;
        this.emails = FXCollections.observableList(new ArrayList<>());
        this.connectionStatus = new SimpleBooleanProperty(false);
    }

    public String getUser() {
        return user;
    }
    
    public ObservableList<Email> getEmails() {
        return emails;
    }
    
    public boolean getConnectionStatus() {
        return connectionStatus.get();
    }

    public BooleanProperty connectionStatusProperty() {
        return connectionStatus;
    }
    public void setConnectionStatus(boolean status) {
        connectionStatus.set(status);
    }

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
