module it.ludovico.serverModel {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.ludovico.serverModel to javafx.fxml;
    exports it.ludovico.serverModel;
    exports it.ludovico.serverModel.controller;
    opens it.ludovico.serverModel.controller to javafx.fxml;
}