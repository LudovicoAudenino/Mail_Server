package it.ludovico.server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloController {

    private ExecutorService threadPool = Executors.newFixedThreadPool(3);
    private ServerSocket serverSocket;

    @FXML
    private TextArea statusLog;

    @FXML
    public void initialize() {

    }
}