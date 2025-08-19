package it.ludovico.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.Socket;

public class HelloController {
    @FXML
    private Label statusLog;

    @FXML
    protected void connectClick() {
        try (Socket socket = new Socket("localhost", 8090);){
            statusLog.setText("Connected");
        } catch (IOException e) {
            statusLog.setText("Connection failed");
            System.err.println(e.getMessage());
        }
    }
}