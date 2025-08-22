module it.ludovico.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.ludovico.client to javafx.fxml;
    opens it.ludovico.client.controller to javafx.fxml;
    
    exports it.ludovico.client;
    exports it.ludovico.client.controller;
    exports it.ludovico.client.service;
    exports it.ludovico.client.model;
}