module it.ludovico.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.ludovico.client to javafx.fxml;
    exports it.ludovico.client;
}