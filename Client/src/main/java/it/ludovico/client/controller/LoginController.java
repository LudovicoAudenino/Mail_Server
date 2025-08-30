package it.ludovico.client.controller;

import it.ludovico.client.model.ClientModel;
import it.ludovico.client.service.AlertService;
import it.ludovico.client.service.ClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    protected void handleLogin() throws IOException {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            AlertService.showError("Error", "Please enter your email address");
            return;
        }
        
        loginButton.setDisable(true);

        ClientModel clientModel = new ClientModel(email);
        ClientService clientService = new ClientService(clientModel);
        
        clientService.login(() -> {
            try {
                NavigationController.showMailboxScene(clientModel, clientService);
            } catch (IOException e) {
                AlertService.showError("Error", e.getMessage());
            }
            });
        loginButton.setDisable(false);
        }
}