package it.ludovico.server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerModel {

    private static final int SOCKET_PORT = 8090;
    private ServerSocket serverSocket;

    public ServerModel() throws IOException {
        serverSocket = new ServerSocket(SOCKET_PORT);
        serverSocket.accept();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
