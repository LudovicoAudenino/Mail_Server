package it.ludovico.client.controller;

import it.ludovico.client.model.ClientModel;
import it.ludovico.client.service.ClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    protected void handleLogin() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showAlert("Error", "Please enter your email address");
            return;
        }
        
        loginButton.setDisable(true);

        // Create ClientModel and ClientService
        ClientModel clientModel = new ClientModel(email);
        ClientService clientService = new ClientService(clientModel);
        
        // Use async login
        clientService.loginAsync()
            .thenAccept(success -> {
                Platform.runLater(() -> {
                    if (success) {
                        try {
                            NavigationController.showMailboxScene(clientModel, clientService);
                        } catch (IOException e) {
                            showAlert("Navigation Error", "Could not open mailbox: " + e.getMessage());
                            loginButton.setDisable(false);
                        }
                    } else {
                        showAlert("Login Failed", "User not registered or server error");
                        loginButton.setDisable(false);
                    }
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    showAlert("Connection Error", "Unexpected error: " + throwable.getMessage());
                    loginButton.setDisable(false);
                });
                return null;
            });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}