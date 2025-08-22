package it.ludovico.client.controller;

import it.ludovico.client.HelloApplication;
import it.ludovico.client.model.ClientModel;
import it.ludovico.shared.model.Email;
import it.ludovico.client.service.ClientService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationController {
    private static Stage primaryStage;
    private static ClientModel currentClientModel;
    private static ClientService currentClientService;
    
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
    
    public static void setCurrentClient(ClientModel model, ClientService service) {
        currentClientModel = model;
        currentClientService = service;
    }
    
    public static ClientModel getCurrentClientModel() {
        return currentClientModel;
    }
    
    public static ClientService getCurrentClientService() {
        return currentClientService;
    }
    
    public static void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        primaryStage.setTitle("Mail Client - Login");
        primaryStage.setScene(scene);
    }
    
    public static void showMailboxScene(ClientModel clientModel, ClientService clientService) throws IOException {
        // Store current client info
        setCurrentClient(clientModel, clientService);
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mailbox-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        
        MailboxController controller = fxmlLoader.getController();
        controller.initializeWithClient(clientModel, clientService);
        
        primaryStage.setTitle("Mail Client - Mailbox (" + clientModel.getUser() + ")");
        primaryStage.setScene(scene);
    }
    
    public static void showComposeEmailScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("compose-email-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        
        ComposeEmailController controller = fxmlLoader.getController();
        if (currentClientModel != null && currentClientService != null) {
            controller.initializeWithClient(currentClientModel, currentClientService);
        }
        
        primaryStage.setTitle("Mail Client - Compose Email");
        primaryStage.setScene(scene);
    }
}