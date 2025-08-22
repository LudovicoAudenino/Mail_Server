module it.ludovico.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens it.ludovico.server to javafx.fxml;
    opens it.ludovico.server.controller to javafx.fxml;
    
    exports it.ludovico.server;
    exports it.ludovico.server.controller;
    exports it.ludovico.server.model;
    exports it.ludovico.server.service;
    exports it.ludovico.server.handler;
    exports it.ludovico.server.repository;
}