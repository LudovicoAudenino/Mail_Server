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

/**
 * JavaFX controller for the mail server user interface.
 * 
 * <p>This controller manages the server's graphical user interface, providing
 * real-time monitoring and control capabilities for the mail server. It handles
 * server startup/shutdown, displays server status, shows processed request counts,
 * and provides live log monitoring with automatic scrolling.</p>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li><strong>Server Control:</strong> Start/stop server functionality with visual feedback</li>
 *   <li><strong>Status Monitoring:</strong> Real-time display of server running status</li>
 *   <li><strong>Request Tracking:</strong> Shows total number of processed client requests</li>
 *   <li><strong>Live Logging:</strong> Auto-updating log display with scroll-to-bottom behavior</li>
 *   <li><strong>Dynamic UI:</strong> Button text and colors change based on server state</li>
 * </ul>
 * 
 * <h2>UI Bindings</h2>
 * <p>The controller uses JavaFX property bindings to automatically update UI elements
 * when the server state changes. This ensures the interface always reflects the current
 * server status without manual updates.</p>
 * 
 * @author JavaFX Mail System Team
 * @version 1.0
 * @since 1.0
 * @see ServerModel
 * @see ServerService
 */
public class ServerController implements Initializable {
    
    /**
     * Label displaying the current server status (RUNNING/STOPPED).
     * Color changes dynamically based on server state.
     */
    @FXML
    private Label serverStatusLabel;

    /**
     * Button for starting/stopping the server.
     * Text and styling change based on current server state.
     */
    @FXML
    private Button toggleServerButton;
    
    /**
     * Button for clearing the server logs display.
     */
    @FXML
    private Button clearLogsButton;
    
    /**
     * Text area displaying real-time server log messages.
     * Automatically scrolls to show the latest log entries.
     */
    @FXML
    private TextArea logsTextArea;
    
    /**
     * The server model containing business logic and state.
     * Provides observable properties for UI binding.
     */
    private ServerModel serverModel;
    
    /**
     * The server service managing network connections and client handling.
     */
    private ServerService serverService;

    /**
     * Initializes the controller and sets up UI bindings.
     * 
     * <p>This method is automatically called by JavaFX after loading the FXML file.
     * It creates the server model and service instances, then configures all property
     * bindings for dynamic UI updates.</p>
     * 
     * <p>Initialization process:
     * <ol>
     *   <li>Creates ServerModel and ServerService instances</li>
     *   <li>Sets up property bindings for dynamic UI updates</li>
     *   <li>Configures log display with auto-scrolling behavior</li>
     * </ol></p>
     * 
     * @param url the location used to resolve relative paths (unused)
     * @param resourceBundle the resources used to localize the root object (unused)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverModel = new ServerModel();
        serverService = new ServerService(serverModel);
        
        setupBindings();
        setupLogsBinding();
    }

    /**
     * Configures JavaFX property bindings for dynamic UI updates.
     * 
     * <p>This method sets up bidirectional bindings between the server model's
     * observable properties and the UI components. This ensures that changes in
     * server state are immediately reflected in the user interface without
     * manual intervention.</p>
     * 
     * <h3>Configured Bindings</h3>
     * <ul>
     *   <li><strong>Status Label:</strong> Text shows "RUNNING" (green) or "STOPPED" (red)</li>
     *   <li><strong>Toggle Button:</strong> Text shows "STOP SERVER" or "START SERVER"</li>
     *   <li><strong>Button Styling:</strong> Background color changes based on server state</li>
     * </ul>
     */
    private void setupBindings() {
        serverStatusLabel.textProperty().bind(
            Bindings.createStringBinding(() -> 
                serverModel.runningProperty().get() ? "RUNNING" : "STOPPED",
                serverModel.runningProperty()
            )
        );
        
        serverStatusLabel.textFillProperty().bind(
            Bindings.createObjectBinding(() -> 
                serverModel.runningProperty().get() ? Color.web("#4caf50") : Color.web("#d32f2f"),
                serverModel.runningProperty()
            )
        );

        
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

    /**
     * Configures the logs display with automatic scrolling and real-time updates.
     * 
     * <p>This method sets up a listener on the server model's observable log list
     * to automatically update the logs text area whenever new log messages are added.
     * The display automatically scrolls to show the latest log entries, ensuring
     * users can always see the most recent server activity.</p>
     * 
     * <p>The listener rebuilds the entire log display text whenever the log list
     * changes and positions the caret at the end for automatic scrolling.</p>
     */
    private void setupLogsBinding() {
        serverModel.getLogs().addListener((javafx.collections.ListChangeListener<String>) change -> {
            StringBuilder logText = new StringBuilder();
            for (String log : serverModel.getLogs()) {
                logText.append(log).append("\n");
            }
            logsTextArea.setText(logText.toString());
            
            logsTextArea.positionCaret(logsTextArea.getText().length());
        });
    }

    /**
     * Toggles the server between running and stopped states.
     * 
     * <p>This method is called when the user clicks the toggle server button.
     * It checks the current server state and performs the opposite action:
     * starting the server if it's stopped, or stopping it if it's running.</p>
     * 
     * <p>The UI automatically updates to reflect the new server state through
     * the configured property bindings.</p>
     */
    @FXML
    private void toggleServer() {
        if (serverModel.getRunning()) {
            serverService.stop();
        } else {
            serverService.start();
        }
    }

    /**
     * Clears all log messages from the display and model.
     * 
     * <p>This method is called when the user clicks the clear logs button.
     * It removes all log entries from both the server model's log list and
     * the UI text area. The operation is scheduled on the JavaFX Application
     * Thread to ensure proper UI thread safety.</p>
     */
    @FXML
    private void clearLogs() {
        Platform.runLater(() -> {
            serverModel.getLogs().clear();
            logsTextArea.clear();
        });
    }
}

