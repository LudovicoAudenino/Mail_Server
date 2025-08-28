package it.ludovico.client.controller;

import it.ludovico.client.HelloApplication;
import it.ludovico.client.model.ClientModel;
import it.ludovico.shared.model.Email;
import it.ludovico.client.service.ClientService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.util.List;

public class NavigationController {
    private static Stage primaryStage;
    private static Scene mainScene;
    private static ClientModel currentClientModel;
    private static ClientService currentClientService;
    
    
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        mainScene = new Scene(new javafx.scene.layout.StackPane(), 600, 400);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
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
        mainScene.setRoot(fxmlLoader.load());
        primaryStage.setTitle("Mail Client - Login");
    }
    
    public static void showMailboxScene(ClientModel clientModel, ClientService clientService) throws IOException {
        setCurrentClient(clientModel, clientService);
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mailbox-view.fxml"));
        mainScene.setRoot(fxmlLoader.load());
        
        MailboxController controller = fxmlLoader.getController();
        controller.initializeWithClient(clientModel, clientService);
        
        primaryStage.setTitle("Mail Client - Mailbox (" + clientModel.getUser() + ")");
    }
    
    public static void showComposeEmailScene() throws IOException {
        showComposeEmailScene(null, null, null, null, null);
    }
    
    public static void showComposeEmailScene(List<String> to, String subject, String body, String originalEmailId, String action) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("compose-email-view.fxml"));
        mainScene.setRoot(fxmlLoader.load());
        
        ComposeEmailController controller = fxmlLoader.getController();
        if (currentClientModel != null && currentClientService != null) {
            controller.initializeWithClient(currentClientModel, currentClientService);
            
            if (to != null || subject != null || body != null) {
                controller.precomposeEmail(to, subject, body, originalEmailId, action);
            }
        }
        
        String title = "Mail Client - Compose Email";
        if (action != null) {
            switch (action) {
                case "REPLY": title = "Mail Client - Reply"; break;
                case "REPLY_ALL": title = "Mail Client - Reply All"; break;
                case "FORWARD": title = "Mail Client - Forward"; break;
            }
        }
        
        primaryStage.setTitle(title);
    }
}