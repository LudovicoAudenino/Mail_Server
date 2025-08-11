package it.ludovico.server.controller;

import it.ludovico.server.model.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class ServerController {
    private Server server;
    private Thread serverThread;
    private volatile boolean running;
    @FXML
    private Button turnOn;
    @FXML
    private Button turnOff;
    @FXML
    private TextArea statusLog;


    @FXML
    public void startServer() {
        if(!running) {
            try {
                server = new Server();
                running = true;
                turnOff.setDisable(!running);
                new Thread(() -> {
                    try {
                        server.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Platform.runLater(() -> {
                            statusLog.appendText("Server thread terminated \n");
                            turnOn.setDisable(running);
                            turnOff.setDisable(!running);
                        });
                    }
                }).start();
                statusLog.appendText("Server listening... \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLog.appendText("Server is already running. \n");
        }
    }

    @FXML
    public void stopServer() {
        if(running) {
            running = false;
            try {
                server.stop();
                statusLog.appendText("Server stopped successfully\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            statusLog.appendText("Server is not running\n");
        }
    }
}

