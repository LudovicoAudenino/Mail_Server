package it.ludovico.client.service;

import it.ludovico.client.model.ClientModel;
import it.ludovico.client.model.Commands;
import it.ludovico.shared.model.Email;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private final ClientModel model;

    public ClientService(ClientModel model) {
        this.model = model;
    }

    // Async login method
    public CompletableFuture<Boolean> loginAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Platform.runLater(() -> model.setStatusMessage("Connecting..."));
                
                List<Email> emails = loginSync(model.getUser());
                
                Platform.runLater(() -> {
                    if (emails != null) {
                        model.clearEmails();
                        model.getEmails().addAll(emails);
                        model.setConnectionStatus(true);
                        model.setStatusMessage("Login successful - " + emails.size() + " emails loaded");
                    } else {
                        model.setConnectionStatus(false);
                        model.setStatusMessage("Login failed - User not registered");
                    }
                });
                return emails != null;
            } catch (Exception e) {
                Platform.runLater(() -> {
                    model.setConnectionStatus(false);
                    model.setStatusMessage("Connection error: " + e.getMessage());
                });
                return false;
            }
        });
    }

    // Async send email method
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String text) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Platform.runLater(() -> model.setStatusMessage("Sending email..."));
                
                List<String> recipients = Arrays.stream(to.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
                    
                Email email = new Email(model.getUser(), recipients, subject, text);
                boolean success = sendEmailSync(email);
                
                Platform.runLater(() -> {
                    if (success) {
                        model.setStatusMessage("Email sent successfully");
                    } else {
                        model.setStatusMessage("Failed to send email");
                    }
                });
                
                return success;
            } catch (Exception e) {
                Platform.runLater(() -> model.setStatusMessage("Error sending email: " + e.getMessage()));
                return false;
            }
        });
    }

    // Async refresh emails method  
    public CompletableFuture<Integer> refreshEmailsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Platform.runLater(() -> model.setStatusMessage("Checking for new emails..."));
                
                List<Email> newEmails = fetchNewEmailsSync(model.getUser());
                
                Platform.runLater(() -> {
                    if (newEmails != null && !newEmails.isEmpty()) {
                        model.addNewEmails(FXCollections.observableList(newEmails));
                        model.setStatusMessage("Received " + newEmails.size() + " new emails");
                    } else {
                        model.setStatusMessage("No new emails");
                    }
                });
                
                return newEmails != null ? newEmails.size() : 0;
            } catch (Exception e) {
                Platform.runLater(() -> model.setStatusMessage("Error refreshing emails: " + e.getMessage()));
                return -1;
            }
        });
    }

    // Private synchronous methods for actual server communication
    private List<Email> loginSync(String email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.LOGIN);
            output.writeObject(email);

            Object response = input.readObject();
            return (List<Email>) response;
        }
    }

    private boolean sendEmailSync(Email email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.SEND_EMAIL);
            output.writeObject(email);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

    private List<Email> fetchNewEmailsSync(String email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.FETCH_NEW_EMAIL);
            output.writeObject(email);

            Object response = input.readObject();
            return (List<Email>) response;
        }
    }

    // Getter for model (useful for controllers)
    public ClientModel getModel() {
        return model;
    }
}
