package it.ludovico.server.controller;

import it.ludovico.server.service.ClientHandler;
import it.ludovico.server.model.Server;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerController {
    @FXML
    private Button test;

    public void testClick() {
        Server server = new Server();
    }
}

