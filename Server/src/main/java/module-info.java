module it.ludovico.server {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.ludovico.server to javafx.fxml;
    exports it.ludovico.server;
}