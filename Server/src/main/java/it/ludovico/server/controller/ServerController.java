package it.ludovico.server.controller;

import it.ludovico.server.model.ServerModel;
import it.ludovico.server.service.ServerService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    
    @FXML
    private Label serverStatusLabel;
    
    @FXML
    private Label requestsProcessedLabel;
    
    @FXML
    private Button toggleServerButton;
    
    @FXML
    private Button clearLogsButton;
    
    @FXML
    private TextArea logsTextArea;
    
    private ServerModel serverModel;
    private ServerService serverService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverModel = new ServerModel();
        serverService = new ServerService(serverModel);
        
        setupBindings();
        setupLogsBinding();
    }

    private void setupBindings() {
        // Bind server status label
        serverStatusLabel.textProperty().bind(
            Bindings.createStringBinding(() -> 
                serverModel.runningProperty().get() ? "RUNNING" : "STOPPED",
                serverModel.runningProperty()
            )
        );
        
        // Bind server status color
        serverStatusLabel.textFillProperty().bind(
            Bindings.createObjectBinding(() -> 
                serverModel.runningProperty().get() ? Color.web("#4caf50") : Color.web("#d32f2f"),
                serverModel.runningProperty()
            )
        );
        
        // Bind requests processed count
        requestsProcessedLabel.textProperty().bind(
            Bindings.createStringBinding(() -> 
                String.valueOf(serverModel.requestsProcessedProperty().get()),
                serverModel.requestsProcessedProperty()
            )
        );
        
        // Bind toggle button text and style
        toggleServerButton.textProperty().bind(
            Bindings.createStringBinding(() -> 
                serverModel.runningProperty().get() ? "STOP SERVER" : "START SERVER",
                serverModel.runningProperty()
            )
        );
        
        toggleServerButton.styleProperty().bind(
            Bindings.createStringBinding(() -> 
                serverModel.runningProperty().get() ? 
                    "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25;" :
                    "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25;",
                serverModel.runningProperty()
            )
        );
    }

    private void setupLogsBinding() {
        // Bind logs TextArea to the ObservableList
        serverModel.getLogs().addListener((javafx.collections.ListChangeListener<String>) change -> {
            Platform.runLater(() -> {
                StringBuilder logText = new StringBuilder();
                for (String log : serverModel.getLogs()) {
                    logText.append(log).append("\n");
                }
                logsTextArea.setText(logText.toString());
                
                // Auto scroll to bottom
                logsTextArea.setScrollTop(Double.MAX_VALUE);
            });
        });
    }

    @FXML
    private void toggleServer() {
        if (serverModel.getRunning()) {
            serverService.stop();
        } else {
            serverService.start();
        }
    }

    @FXML
    private void clearLogs() {
        Platform.runLater(() -> {
            serverModel.getLogs().clear();
            logsTextArea.clear();
        });
    }
}

