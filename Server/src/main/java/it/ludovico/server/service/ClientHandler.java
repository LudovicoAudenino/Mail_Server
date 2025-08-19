package it.ludovico.server.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.net.Socket;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StringProperty logProperty = new SimpleStringProperty();
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        while(true) {
            int counter = 0;
            counter++;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }
    }
}
