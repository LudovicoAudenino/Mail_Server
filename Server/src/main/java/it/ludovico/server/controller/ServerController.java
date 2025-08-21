package it.ludovico.server.controller;

import it.ludovico.server.model.ServerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ServerController {
    @FXML
    private Button test;

    public void testClick() {
        ServerModel serverModel = new ServerModel();
    }
}

