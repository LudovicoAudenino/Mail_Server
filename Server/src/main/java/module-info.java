module it.ludovico.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.ludovico.server to javafx.fxml;
    exports it.ludovico.server;
    exports it.ludovico.server.controller;
    opens it.ludovico.server.controller to javafx.fxml;
}