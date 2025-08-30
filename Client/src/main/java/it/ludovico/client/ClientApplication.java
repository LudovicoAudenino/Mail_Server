package it.ludovico.client;

import it.ludovico.client.controller.NavigationController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        NavigationController.setPrimaryStage(stage);
        NavigationController.showLoginScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}