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
import java.util.List;

public class ClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private final ClientModel model;

    public ClientService(ClientModel model) {
        this.model = model;
    }

    public void login() {
        new Thread(() -> {
            try {
                List<Email> emails = loginConnect(model.getUser());

                Platform.runLater(() -> {
                    if (emails != null) {
                        model.clearEmails();
                        model.getEmails().addAll(emails);
                        model.setConnectionStatus(true);
                    } else {
                        model.setConnectionStatus(false);
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    model.setConnectionStatus(false);
                    AlertService.showError("Login Error", "Connection failed: " + e.getMessage());
                });
            }
        }).start();
    }

    public void sendEmail(List<String> to, String subject, String body) {
        new Thread(() -> {

            Email mail = new Email(model.getUser(), to, subject, body);
            try {
                boolean success = (Boolean) sendEmailConnect(mail);
                Platform.runLater(() -> {
                    if (success) {
                        AlertService.showSuccess("Email Sent", "Your email has been sent successfully!");
                    } else {
                        AlertService.showError("Send Failed", "Failed to send email to server");
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    AlertService.showError("Send Error", "Connection error: " + e.getMessage());
                });
            }

        }).start();
    }

    public void refreshEmails() {
        new Thread(() -> {
            try {
                List<Email> newEmails = fetchNewEmailsConnect(model.getUser());

                Platform.runLater(() -> {
                    if (newEmails != null && !newEmails.isEmpty()) {
                        model.addNewEmails(FXCollections.observableList(newEmails));
                        AlertService.showSuccess("Refresh", "Received " + newEmails.size() + " new emails");
                    } else {
                        AlertService.showInfo("Refresh", "No new emails found");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertService.showError("Refresh Error", "Could not fetch emails: " + e.getMessage());
                });
            }
        }).start();
    }

    public void deleteEmail(String emailId) {
        new Thread(() -> {
            try {
                boolean deleted = deleteEmailConnect(emailId);
                
                Platform.runLater(() -> {
                    if (deleted) {
                        // Remove email from local model
                        model.getEmails().removeIf(email -> email.getId().toString().equals(emailId));
                        AlertService.showSuccess("Delete", "Email deleted successfully");
                    } else {
                        AlertService.showError("Delete Failed", "Could not delete email");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertService.showError("Delete Error", "Connection error: " + e.getMessage());
                });
            }
        }).start();
    }

    private List<Email> loginConnect(String email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.LOGIN);
            output.writeObject(email);

            Object response = input.readObject();
            return (List<Email>) response;
        }
    }

    private boolean sendEmailConnect(Email email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.SEND_EMAIL);
            output.writeObject(email);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

    private List<Email> fetchNewEmailsConnect(String email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.FETCH_NEW_EMAIL);
            output.writeObject(email);

            Object response = input.readObject();
            return (List<Email>) response;
        }
    }

    public boolean checkEmailExistsConnect(String email) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.CHECK_EMAIL_EXISTS);
            output.writeObject(email);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

    public boolean deleteEmailConnect(String emailId) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.DELETE_EMAIL);
            output.writeObject(model.getUser());  // Send current user email
            output.writeObject(emailId);          // Send email ID to delete

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

}
