package it.ludovico.server.service;

import it.ludovico.server.handler.ClientHandler;
import it.ludovico.server.model.ServerModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerService {
    private static final int PORT = 8888;
    private static final int MAX_CLIENTS = 3;

    private final ServerModel server;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private ExecutorService clientThreadPool;

    public ServerService(ServerModel model) {
        this.server = model;
    }

    public void start() {
        if(server.getRunning()) {
            server.addLog("Server is already running");
            return;
        }
        try {
            serverSocket = new ServerSocket(PORT);
            clientThreadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            server.setRunning(true);

            serverThread = new Thread(this::runServer);
            serverThread.setDaemon(true);
            serverThread.start();
            server.addLog("Server started at port " + PORT);

        } catch (IOException e) {
            server.addLog("Could not listen on port: " + PORT);
        }
    }

    public void runServer() {
        while(server.getRunning()) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                ClientHandler clientHandler = new ClientHandler(
                    clientSocket, 
                    server::addLog, 
                    server
                );

                clientThreadPool.submit(clientHandler);
            } catch (IOException e) {
                if (server.getRunning()) {
                    server.addLog("Could not accept client connection: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        if (!server.getRunning()) {
            server.addLog("Server is not running");
            return;
        }

        server.setRunning(false);
        server.addLog("Stopping server...");

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            server.addLog("Error closing server socket: " + e.getMessage());
        }

        if (clientThreadPool != null) {
            clientThreadPool.shutdown();
            try {
                if (!clientThreadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    server.addLog("Forcing shutdown of client connections...");
                    clientThreadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                server.addLog("Interrupted during shutdown");
                clientThreadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (serverThread != null) {
            try {
                serverThread.join(5000);
            } catch (InterruptedException e) {
                server.addLog("Interrupted waiting for server thread to finish");
                Thread.currentThread().interrupt();
            }
        }

        server.addLog("Server stopped");
    }
}
