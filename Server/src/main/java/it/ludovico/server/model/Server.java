package it.ludovico.server.model;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    private static final int SOCKET_PORT = 8090;
    private ServerSocket serverSocket;


    public Server() throws IOException {
        serverSocket = new ServerSocket(SOCKET_PORT);
    }

    public void start() throws IOException {
            try {
                serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Error accepting connection: " + e.getMessage());
            }
    }

    public void stop() throws IOException {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }



}