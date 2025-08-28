package it.ludovico.client.controller;

import it.ludovico.client.model.ClientModel;
import it.ludovico.client.service.AlertService;
import it.ludovico.client.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ComposeEmailController  {
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
    private String originalEmailId;
    private String action; // REPLY, REPLY_ALL, FORWARD

    
    public void initializeWithClient(ClientModel model, ClientService service) {
        this.clientModel = model;
        this.clientService = service;
        
        fromLabel.setText("From: " + clientModel.getUser());
    }
    
    public void precomposeEmail(List<String> to, String subject, String body, String originalEmailId, String action) {
        this.originalEmailId = originalEmailId;
        this.action = action;
        
        if (to != null && !to.isEmpty()) {
            toField.setText(String.join(", ", to));
        }
        
        if (subject != null) {
            subjectField.setText(subject);
        }
        
        if (body != null) {
            messageArea.setText(body);
            // Posiziona il cursore all'inizio per facilitare la digitazione della risposta
            messageArea.positionCaret(0);
        }
    }
    
    @FXML
    protected void handleSend() {
        List<String> recipients = Arrays.stream(toField.getText().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();
        
        if (recipients.isEmpty()) {
            AlertService.showError("Error", "Please enter recipient email address(es)");
            return;
        }

        for (String mail : recipients) {
            if (!mail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                AlertService.showError("Error", "Invalid email format: " + mail);
                return;
            }

            try {
                boolean exists = clientService.checkEmailExistsConnect(mail);
                if (!exists) {
                    AlertService.showError("Error", "Email address does not exist: " + mail);
                    return;
                }
            } catch (Exception e) {
                AlertService.showError("Error", "Could not verify email address: " + mail);
                return;
            }
        }

        if (subject.isEmpty()) {
            AlertService.showError("Error", "Please enter email subject");
            return;
        }
        
        if (message.isEmpty()) {
            AlertService.showError("Error", "Please enter email message");
            return;
        }

        if (clientService == null) {
            AlertService.showError("Error", "Client service not initialized");
            return;
        }
        
        sendButton.setDisable(true);
        
        // Usa il metodo appropriato basato sull'azione
        if (action != null && originalEmailId != null) {
            switch (action) {
                case "REPLY":
                    clientService.replyToEmail(originalEmailId, recipients, subject, message);
                    break;
                case "REPLY_ALL":
                    clientService.replyAllToEmail(originalEmailId, recipients, subject, message);
                    break;
                case "FORWARD":
                    clientService.forwardEmail(originalEmailId, recipients, subject, message);
                    break;
                default:
                    clientService.sendEmail(recipients, subject, message);
            }
        } else {
            // Email normale
            clientService.sendEmail(recipients, subject, message);
        }
        
        clearFields();
        sendButton.setDisable(false);
        handleCancel();
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
            AlertService.showError("Navigation Error", "Could not return to previous screen: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        toField.clear();
        subjectField.clear();
        messageArea.clear();
    }
}