    package it.ludovico.server.controller;

    import it.ludovico.server.model.ServerModel;
    import javafx.fxml.FXML;
    import javafx.scene.control.Button;
    import javafx.scene.control.TextArea;

    import java.io.IOException;
    import java.net.Socket;

    public class ServerController {
        private volatile boolean serverRunning = false;
        private ServerModel serverModel;
        private Thread serverThread;
        @FXML
        private Button turnOn;
        @FXML
        private Button turnOff;
        @FXML
        private TextArea statusLog;

        @FXML
        public void serverOn() {
            if(!serverRunning) {
                serverThread = new Thread(() -> {
                    try {
                        serverModel = new ServerModel();
                        serverRunning = true;
                        statusLog.appendText("Server started and waiting for connection... \n");
                    } catch (IOException e) {
                        statusLog.appendText("ERROR: " + e.getMessage());
                        e.printStackTrace();
                    }

                });
                serverThread.start();
            } else {
                statusLog.appendText("Server is already running \n");
            }
        }

        @FXML
        public void serverOff() {
            if(serverRunning) {

            }
        }
    }