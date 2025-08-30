package it.ludovico.client.controller;

import it.ludovico.client.model.ClientModel;
import it.ludovico.client.service.AlertService;
import it.ludovico.shared.model.Email;
import it.ludovico.client.service.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MailboxController implements Initializable {
    @FXML
    private Label userEmailLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private TableView<Email> emailTable;
    
    @FXML
    private TableColumn<Email, String> fromColumn;
    
    @FXML
    private TableColumn<Email, String> subjectColumn;
    
    @FXML
    private TableColumn<Email, LocalDateTime> dateColumn;
    
    @FXML
    private TextArea emailContentArea;
    
    @FXML
    private Label newEmailCountLabel;
    
    @FXML
    private Label serverStatusLabel;
    
    @FXML
    private Button composeButton;

    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button replyButton;
    
    @FXML
    private Button replyAllButton;
    
    @FXML
    private Button forwardButton;
    
    @FXML
    private Button logoutButton;
    
    private ClientModel clientModel;
    private ClientService clientService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFrom()));
        subjectColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubject()));
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSent()));

        dateColumn.setCellFactory(column -> new TableCell<Email, LocalDateTime>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });

        emailTable.setRowFactory(tv -> {
            TableRow<Email> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldEmail, newEmail) -> {
                if (newEmail == null) {
                    row.setStyle("");
                } else if (!newEmail.isDeliveredTo(clientModel.getUser())) {
                    row.setStyle("-fx-background-color: #E3F2FD; -fx-font-weight: bold;");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });

        emailTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayEmailContent(newSelection);
                
                if (!newSelection.isDeliveredTo(clientModel.getUser())) {
                    newSelection.markDeliveredTo(clientModel.getUser());
                    emailTable.refresh();
                    updateNewEmailCount();
                }
            }
        });
    }
    
    public void initializeWithClient(ClientModel model, ClientService service) {
        this.clientModel = model;
        this.clientService = service;

        userEmailLabel.setText("User: " + clientModel.getUser());
        emailTable.setItems(clientModel.getEmails());
        deleteButton.disableProperty().bind(emailTable.getSelectionModel().selectedItemProperty().isNull());
        replyButton.disableProperty().bind(emailTable.getSelectionModel().selectedItemProperty().isNull());
        replyAllButton.disableProperty().bind(emailTable.getSelectionModel().selectedItemProperty().isNull());
        forwardButton.disableProperty().bind(emailTable.getSelectionModel().selectedItemProperty().isNull());
        

        if (clientService != null) {
            clientService.setOnEmailCountChange(this::updateNewEmailCount);
            clientService.setOnServerStatusChange(this::updateServerStatus);
            clientService.startAutoRefresh();
            clientService.startHeartbeat();
        }
        
        updateNewEmailCount();
        updateServerStatus();
    }
    
    
    private void displayEmailContent(Email email) {
        StringBuilder content = new StringBuilder();
        content.append("From: ").append(email.getFrom()).append("\n");
        content.append("To: ").append(String.join(", ", email.getTo())).append("\n");
        content.append("Subject: ").append(email.getSubject()).append("\n");
        content.append("Date: ").append(email.getSent().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        content.append("Message:\n").append(email.getText());
        
        emailContentArea.setText(content.toString());
    }
    
    @FXML
    protected void handleCompose() {
        try {
            NavigationController.showComposeEmailScene();
        } catch (IOException e) {
            AlertService.showError("Navigation Error", "Could not open compose window: " + e.getMessage());
        }
    }

    @FXML
    protected void handleDelete() {
        Email selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail != null && clientService != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Email");
            confirmation.setHeaderText("Delete Email");
            confirmation.setContentText("Are you sure you want to delete this email from " + selectedEmail.getFrom() + "?");
            
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    clientService.deleteEmail(selectedEmail.getId().toString());
                }
            });
        }
    }
    
    @FXML
    protected void handleLogout() {
        if (clientService != null) {
            clientService.stopAutoRefresh();
        }
        try {
            NavigationController.showLoginScene();
        } catch (IOException e) {
            AlertService.showError("Navigation Error", "Could not return to login: " + e.getMessage());
        }
    }
    
    @FXML
    protected void handleReply() {
        Email selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            List<String> replyTo = Arrays.asList(selectedEmail.getFrom());
            String replySubject = selectedEmail.getSubject().startsWith("Re: ") ? 
                selectedEmail.getSubject() : "Re: " + selectedEmail.getSubject();
            String replyBody = "\n\n--- Original Message ---\n" + 
                "From: " + selectedEmail.getFrom() + "\n" +
                "Date: " + selectedEmail.getSent().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                "Subject: " + selectedEmail.getSubject() + "\n\n" +
                selectedEmail.getText();

            try {
                NavigationController.showComposeEmailScene(replyTo, replySubject, replyBody, selectedEmail.getId().toString(), "REPLY");
            } catch (IOException e) {
                AlertService.showError("Navigation Error", "Could not open compose window: " + e.getMessage());
            }
        }
    }
    
    @FXML
    protected void handleReplyAll() {
        Email selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            List<String> replyTo = new ArrayList<>();
            replyTo.add(selectedEmail.getFrom());
            for (String recipient : selectedEmail.getTo()) {
                if (!recipient.equals(clientModel.getUser()) && !replyTo.contains(recipient)) {
                    replyTo.add(recipient);
                }
            }
            
            String replySubject = selectedEmail.getSubject().startsWith("Re: ") ? 
                selectedEmail.getSubject() : "Re: " + selectedEmail.getSubject();
            String replyBody = "\n\n--- Original Message ---\n" + 
                "From: " + selectedEmail.getFrom() + "\n" +
                "Date: " + selectedEmail.getSent().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                "Subject: " + selectedEmail.getSubject() + "\n\n" +
                selectedEmail.getText();
                
            try {
                NavigationController.showComposeEmailScene(replyTo, replySubject, replyBody, selectedEmail.getId().toString(), "REPLY_ALL");
            } catch (IOException e) {
                AlertService.showError("Navigation Error", "Could not open compose window: " + e.getMessage());
            }
        }
    }
    
    @FXML
    protected void handleForward() {
        Email selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            String forwardSubject = selectedEmail.getSubject().startsWith("Fwd: ") ? 
                selectedEmail.getSubject() : "Fwd: " + selectedEmail.getSubject();
            String forwardBody = "\n\n--- Forwarded Message ---\n" + 
                "From: " + selectedEmail.getFrom() + "\n" +
                "To: " + String.join(", ", selectedEmail.getTo()) + "\n" +
                "Date: " + selectedEmail.getSent().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                "Subject: " + selectedEmail.getSubject() + "\n\n" +
                selectedEmail.getText();
                
            try {
                NavigationController.showComposeEmailScene(new ArrayList<>(), forwardSubject, forwardBody, selectedEmail.getId().toString(), "FORWARD");
            } catch (IOException e) {
                AlertService.showError("Navigation Error", "Could not open compose window: " + e.getMessage());
            }
        }
    }
    
    private void updateNewEmailCount() {
        if (clientModel != null && newEmailCountLabel != null) {
            long unreadCount = clientModel.getEmails().stream()
                .filter(email -> !email.isDeliveredTo(clientModel.getUser()))
                .count();
            
            if (unreadCount > 0) {
                newEmailCountLabel.setText("(" + unreadCount + " new)");
            } else {
                newEmailCountLabel.setText("");
            }
        }
    }
    
    private void updateServerStatus() {
        if (clientService != null && serverStatusLabel != null) {
            boolean serverOnline = clientService.isServerOnline();
            if (serverOnline) {
                serverStatusLabel.setText("Server: Online");
                serverStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0 0 0 20; -fx-text-fill: #4CAF50;");
            } else {
                serverStatusLabel.setText("Server: Offline");
                serverStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0 0 0 20; -fx-text-fill: #f44336;");
            }
        }
    }
    
}