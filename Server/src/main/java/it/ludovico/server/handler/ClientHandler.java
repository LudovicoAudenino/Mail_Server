package it.ludovico.server.handler;

import it.ludovico.shared.model.Email;
import it.ludovico.server.service.EmailService;
import it.ludovico.server.service.LogService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final EmailService emailService;

    public ClientHandler(Socket socket, EmailService emailService) {
        this.socket = socket;
        this.emailService = emailService;
    }



    public void run() {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

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
                case "CHECK_EMAIL_EXISTS":
                    handleCheckEmailExists(input, output);
                    break;
                case "DELETE_EMAIL":
                    handleDeleteEmail(input, output);
                    break;
                case "REPLY_EMAIL":
                    handleReplyEmail(input, output);
                    break;
                case "REPLY_ALL_EMAIL":
                    handleReplyAllEmail(input, output);
                    break;
                case "FORWARD_EMAIL":
                    handleForwardEmail(input, output);
                    break;
                default:
                    LogService.warn("Unknown command received: " + command);
                    output.writeObject(false);
            }
            
        } catch (IOException | ClassNotFoundException e) {
            LogService.error("Error processing client request: " + e.getMessage());
    }
    }


    private void handleLogin(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String email = (String) input.readObject();

        if(emailService.checkRegisteredAccount(email)) {
            LogService.info("Login successful for user: " + email);
            List<Email> emails = emailService.getUserMailbox(email);
            output.writeObject(emails);
        } else {
            LogService.warn("Login failed for user: " + email);
            output.writeObject(null);
        }
    }

    private void handleSendEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        Email email = (Email) input.readObject();

        boolean success = emailService.sendEmail(email);
        output.writeObject(success);
        
        if(success) {
            LogService.info("Email successfully sent from " + email.getFrom() + " to " + email.getTo());
        } else {
            LogService.warn("Email could not be sent from " + email.getFrom());
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
            
            LogService.info("Fetched " + newEmails.size() + " new emails for user: " + userEmail);
        } else {
            LogService.warn("Fetch request for non-existent user: " + userEmail);
            output.writeObject(null);
        }
    }

    private void handleCheckEmailExists(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String email = (String) input.readObject();
        
        boolean exists = emailService.checkRegisteredAccount(email);
        output.writeObject(exists);
        
        LogService.debug("Email existence check for " + email + ": " + (exists ? "exists" : "not found"));
    }

    private void handleDeleteEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String userEmail = (String) input.readObject();
        String emailId = (String) input.readObject();
        
        if (emailService.checkRegisteredAccount(userEmail)) {
            boolean deleted = emailService.deleteEmail(userEmail, emailId);
            output.writeObject(deleted);
            
            LogService.info("Delete email request for user " + userEmail + ", email ID " + emailId + ": " + (deleted ? "success" : "failed"));
        } else {
            LogService.warn("Delete request for non-existent user: " + userEmail);
            output.writeObject(false);
        }
    }

    private void handleReplyEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String originalEmailId = (String) input.readObject();
        Email replyEmail = (Email) input.readObject();
        
        boolean success = emailService.replyToEmail(originalEmailId, replyEmail);
        output.writeObject(success);
        
        if (success) {
            LogService.info("Reply email sent from " + replyEmail.getFrom() + " to " + replyEmail.getTo());
        } else {
            LogService.warn("Reply email could not be sent from " + replyEmail.getFrom());
        }
    }

    private void handleReplyAllEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String originalEmailId = (String) input.readObject();
        Email replyAllEmail = (Email) input.readObject();
        
        boolean success = emailService.replyAllToEmail(originalEmailId, replyAllEmail);
        output.writeObject(success);
        
        if (success) {
            LogService.info("Reply-all email sent from " + replyAllEmail.getFrom() + " to " + replyAllEmail.getTo());
        } else {
            LogService.warn("Reply-all email could not be sent from " + replyAllEmail.getFrom());
        }
    }

    private void handleForwardEmail(ObjectInputStream input, ObjectOutputStream output) throws IOException, ClassNotFoundException {
        String originalEmailId = (String) input.readObject();
        Email forwardEmail = (Email) input.readObject();
        
        boolean success = emailService.forwardEmail(originalEmailId, forwardEmail);
        output.writeObject(success);
        
        if (success) {
            LogService.info("Forward email sent from " + forwardEmail.getFrom() + " to " + forwardEmail.getTo());
        } else {
            LogService.warn("Forward email could not be sent from " + forwardEmail.getFrom());
        }
    }

}
