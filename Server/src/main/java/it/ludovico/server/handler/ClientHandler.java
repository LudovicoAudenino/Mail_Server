package it.ludovico.server.handler;

import it.ludovico.shared.model.Email;
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

    public ClientHandler(Socket socket, Consumer<String> logger, EmailService emailService) {
        this.socket = socket;
        this.logger = logger;
        this.emailService = emailService;
    }



    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
            
            logger.accept("Client connected for single operation");

            String command = (String) input.readObject();
            
            switch (command) {
                case "LOGIN":
                    handleLogin(input, output);
                    break;
                case "SEND_EMAIL":
                    handleSendEmail(input, output);
                    break;
                case "FETCH_NEW_EMAIL":
                    handleFetchNewEmail(input, output);
                    break;
                default:
                    logger.accept("Unknown command received: " + command);
                    output.writeObject(false);
            }
            logger.accept("Operation completed, closing connection");
            
        } catch (IOException | ClassNotFoundException e) {
            logger.accept("Error processing client request: " + e.getMessage());
    }
    }


    private void handleLogin(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String email = (String) input.readObject();

        if(emailService.checkRegisteredAccount(email)) {
            logger.accept("Login successful for user: " + email);
            List<Email> emails = emailService.getUserMailbox(email);
            output.writeObject(emails);
        } else {
            logger.accept("Login failed for user: " + email);
            output.writeObject(null);
        }
    }

    private void handleSendEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        Email email = (Email) input.readObject();

        boolean success = emailService.sendEmail(email);
        output.writeObject(success);
        
        if(success) {
            logger.accept("Email successfully sent from " + email.getFrom() + " to " + email.getTo());
        } else {
            logger.accept("Email could not be sent from " + email.getFrom());
        }
    }

    private void handleFetchNewEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String userEmail = (String) input.readObject(); // Ora il client deve passare l'email ogni volta
        
        if (emailService.checkRegisteredAccount(userEmail)) {
            List<Email> newEmails = emailService.getNewEmails(userEmail);
            output.writeObject(newEmails);
            
            // Marca le email come consegnate dopo averle inviate al client
            if (!newEmails.isEmpty()) {
                emailService.markEmailsAsDelivered(userEmail, newEmails);
            }
            
            logger.accept("Fetched " + newEmails.size() + " new emails for user: " + userEmail);
        } else {
            logger.accept("Fetch request for non-existent user: " + userEmail);
            output.writeObject(null);
        }
    }

}
