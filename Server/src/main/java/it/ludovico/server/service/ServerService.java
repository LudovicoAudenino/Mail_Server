package it.ludovico.server.service;

import it.ludovico.server.handler.ClientHandler;
import it.ludovico.server.model.ServerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerService {
    private static final int PORT = 8888;
    private static final int MAX_CLIENTS = 3;

    private final ServerModel model;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private ExecutorService clientThreadPool;

    public ServerService(ServerModel model) {
        this.model = model;
    }

    public void start() {
        if(model.getRunning()) {
            model.addLog("Server is already running");
            return;
        }
        try {
            serverSocket = new ServerSocket(PORT);
            clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            model.setRunning(true);

            serverThread = new Thread(this::runServer);
            serverThread.setDaemon(true);
            serverThread.start();
            model.addLog("Server started at port " + PORT);

        } catch (IOException e) {
            model.addLog("Could not listen on port: " + PORT);
        }
    }

    public void runServer() {
        while(model.getRunning()) {
            try {
                Socket clientSocket = serverSocket.accept();
                try {
                    ClientHandler clientHandler = new ClientHandler(clientSocket,)
                }
            } catch (IOException e) {
                model.addLog("Could not accept client connection");
            }

        }
    }
}
