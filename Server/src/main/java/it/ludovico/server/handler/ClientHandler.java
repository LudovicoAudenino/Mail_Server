package it.ludovico.server.handler;

import it.ludovico.server.model.Email;
import it.ludovico.server.service.EmailService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Consumer<String> logger;
    private final EmailService emailService;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String loggedUser;
    private boolean running;

    public ClientHandler(Socket socket, Consumer<String> logger, EmailService emailService) {
        this.socket = socket;
        this.logger = logger;
        this.emailService = emailService;
    }



    public void run() {

    }

    private void setUpStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        logger.accept("Client connected");
    }

    private void handleLogin() throws IOException, ClassNotFoundException {
        String email = (String) input.readObject();

        if(emailService.checkRegisteredAccount(email)) {
            loggedUser = email;
            logger.accept("Logged in user: " + loggedUser);
            List<Email> emails = emailService.getUserMailbox(loggedUser);

            output.writeObject(emails);
        } else {
            logger.accept("User "+ email +" doesn't exist");
        }

    }

    private void handleSendEmail() throws IOException, ClassNotFoundException {
        Email email = (Email) input.readObject();

        if(emailService.sendEmail(email)) {
            logger.accept("Email successfully sent from " + loggedUser);
        } else {
            logger.accept("Email could not be sent from " + loggedUser);
        }
    }

    private void handleFetchNewEmail() throws IOException, ClassNotFoundException {

    }
}
