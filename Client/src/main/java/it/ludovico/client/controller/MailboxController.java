package it.ludovico.client.controller;

import it.ludovico.client.model.ClientModel;
import it.ludovico.shared.model.Email;
import it.ludovico.client.service.ClientService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private Button logoutButton;
    
    private ClientModel clientModel;
    private ClientService clientService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura le colonne della tabella
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sent"));
        
        // Formatta la colonna data
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
        
        // Listener per la selezione email
        emailTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                displayEmailContent(newSelection);
            }
        });
    }
    
    public void initializeWithClient(ClientModel model, ClientService service) {
        this.clientModel = model;
        this.clientService = service;
        
        // Binding con il model
        userEmailLabel.setText("User: " + clientModel.getUser());
        emailTable.setItems(clientModel.getEmails());
        
        // Binding del status se c'è un label status
        if (statusLabel != null) {
            statusLabel.textProperty().bind(clientModel.statusMessageProperty());
        }
        
        // Binding dello stato dei bottoni
        refreshButton.disableProperty().bind(clientModel.connectionStatusProperty().not());
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
            showAlert("Navigation Error", "Could not open compose window: " + e.getMessage());
        }
    }
    
    @FXML
    protected void handleRefresh() {
        if (clientService != null) {
            refreshButton.setDisable(true);
            
            clientService.refreshEmailsAsync()
                .thenAccept(count -> {
                    refreshButton.setDisable(false);
                    if (count > 0) {
                        showAlert("Refresh", "Received " + count + " new emails");
                    }
                })
                .exceptionally(throwable -> {
                    refreshButton.setDisable(false);
                    showAlert("Refresh Error", "Could not fetch emails: " + throwable.getMessage());
                    return null;
                });
        }
    }
    
    @FXML
    protected void handleLogout() {
        try {
            NavigationController.showLoginScene();
        } catch (IOException e) {
            showAlert("Navigation Error", "Could not return to login: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}