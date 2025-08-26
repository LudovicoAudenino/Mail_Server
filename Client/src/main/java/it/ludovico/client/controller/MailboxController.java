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
    private Button composeButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button deleteButton;
    
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

        emailTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayEmailContent(newSelection);
            }
        });
    }
    
    public void initializeWithClient(ClientModel model, ClientService service) {
        this.clientModel = model;
        this.clientService = service;

        userEmailLabel.setText("User: " + clientModel.getUser());
        emailTable.setItems(clientModel.getEmails());

        refreshButton.disableProperty().bind(clientModel.connectionStatusProperty().not());
        deleteButton.disableProperty().bind(emailTable.getSelectionModel().selectedItemProperty().isNull());
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
    protected void handleRefresh() {
        if (clientService != null) {
            clientService.refreshEmails();
        }
    }
    
    @FXML
    protected void handleDelete() {
        Email selectedEmail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedEmail != null && clientService != null) {
            // Ask for confirmation
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
        try {
            NavigationController.showLoginScene();
        } catch (IOException e) {
            AlertService.showError("Navigation Error", "Could not return to login: " + e.getMessage());
        }
    }
    
}