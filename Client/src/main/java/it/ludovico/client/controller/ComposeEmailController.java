package it.ludovico.client.controller;

import it.ludovico.client.model.ClientModel;
import it.ludovico.client.service.ClientService;
import it.ludovico.shared.model.Email;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ComposeEmailController implements Initializable {
    @FXML
    private Label fromLabel;
    
    @FXML
    private TextField toField;
    
    @FXML
    private TextField subjectField;
    
    @FXML
    private TextArea messageArea;
    
    @FXML
    private Button sendButton;
    
    @FXML
    private Button cancelButton;
    
    private ClientModel clientModel;
    private ClientService clientService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Basic initialization - from label will be set in initializeWithClient
    }
    
    public void initializeWithClient(ClientModel model, ClientService service) {
        this.clientModel = model;
        this.clientService = service;
        
        fromLabel.setText("From: " + clientModel.getUser());
    }
    
    @FXML
    protected void handleSend() {
        String toText = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (toText.isEmpty()) {
            showAlert("Error", "Please enter recipient email address(es)");
            return;
        }
        
        if (subject.isEmpty()) {
            showAlert("Error", "Please enter email subject");
            return;
        }
        
        if (message.isEmpty()) {
            showAlert("Error", "Please enter email message");
            return;
        }
        
        // Parse recipients (comma-separated)
        List<String> recipients = Arrays.stream(toText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        if (recipients.isEmpty()) {
            showAlert("Error", "Please enter valid recipient email address(es)");
            return;
        }
        
        if (clientService == null) {
            showAlert("Error", "Client service not initialized");
            return;
        }
        
        sendButton.setDisable(true);
        
        // Use async send method
        clientService.sendEmailAsync(toText, subject, message)
            .thenAccept(success -> {
                sendButton.setDisable(false);
                if (success) {
                    showSuccessAlert("Success", "Email sent successfully!");
                    clearFields();
                    handleCancel(); // Return to mailbox
                } else {
                    showAlert("Send Failed", "Could not send email. Please check recipient addresses.");
                }
            })
            .exceptionally(throwable -> {
                sendButton.setDisable(false);
                showAlert("Connection Error", "Could not connect to server: " + throwable.getMessage());
                return null;
            });
    }
    
    @FXML
    protected void handleCancel() {
        try {
            if (clientModel != null && clientService != null) {
                NavigationController.showMailboxScene(clientModel, clientService);
            } else {
                NavigationController.showLoginScene();
            }
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not return to previous screen: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        toField.clear();
        subjectField.clear();
        messageArea.clear();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}