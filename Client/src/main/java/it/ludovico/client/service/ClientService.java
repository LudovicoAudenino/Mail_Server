package it.ludovico.client.service;

import it.ludovico.client.model.ClientModel;
import it.ludovico.shared.model.Commands;
import it.ludovico.shared.model.Email;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;

    private final ClientModel model;
    
    private ScheduledExecutorService autoRefreshScheduler;
    private ScheduledFuture<?> autoRefreshTask;
    private final int refreshInterval = 30;
    private Runnable onEmailCountChange;
    
    private ScheduledExecutorService heartbeatScheduler;
    private ScheduledFuture<?> heartbeatTask;
    private final int heartbeatInterval = 5;
    private Runnable onServerStatusChange;
    private boolean lastServerStatus = true;

    public ClientService(ClientModel model) {
        this.model = model;
    }
    
    public void setOnEmailCountChange(Runnable callback) {
        this.onEmailCountChange = callback;
    }
    
    public void setOnServerStatusChange(Runnable callback) {
        this.onServerStatusChange = callback;
    }

    public void login(Runnable OnSuccess) {
        new Thread(() -> {
            try {
                List<Email> emails = loginConnect(model.getUser());

                Platform.runLater(() -> {
                    if (emails != null) {
                        model.clearEmails();
                        model.getEmails().addAll(emails);
                        model.setConnectionStatus(true);
                        OnSuccess.run();
                    } else {
                        model.setConnectionStatus(false);
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    model.setConnectionStatus(false);
                    AlertService.showError("Login Error", "Cannot connect to mail server. Please check your connection and try again.");
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
                    AlertService.showError("Send Error", "Cannot connect to mail server. Your email was not sent. Please try again.");
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
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertService.showError("Refresh Error", "Cannot connect to mail server. Unable to check for new emails.");
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
                        model.getEmails().removeIf(email -> email.getId().toString().equals(emailId));
                        AlertService.showSuccess("Delete", "Email deleted successfully");
                    } else {
                        AlertService.showError("Delete Failed", "Could not delete email");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertService.showError("Delete Error", "Cannot connect to mail server. The email was not deleted. Please try again.");
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
            output.writeObject(model.getUser());
            output.writeObject(emailId);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

    public void replyToEmail(String originalEmailId, List<String> to, String subject, String body) {
        new Thread(() -> {
            Email replyEmail = new Email(model.getUser(), to, subject, body);
            try {
                boolean success = replyEmailConnect(originalEmailId, replyEmail);
                Platform.runLater(() -> {
                    if (success) {
                        AlertService.showSuccess("Reply Sent", "Your reply has been sent successfully!");
                    } else {
                        AlertService.showError("Reply Failed", "Failed to send reply");
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    AlertService.showError("Reply Error", "Cannot connect to mail server. Your reply was not sent. Please try again.");
                });
            }
        }).start();
    }

    public void replyAllToEmail(String originalEmailId, List<String> to, String subject, String body) {
        new Thread(() -> {
            Email replyAllEmail = new Email(model.getUser(), to, subject, body);
            try {
                boolean success = replyAllEmailConnect(originalEmailId, replyAllEmail);
                Platform.runLater(() -> {
                    if (success) {
                        AlertService.showSuccess("Reply All Sent", "Your reply to all has been sent successfully!");
                    } else {
                        AlertService.showError("Reply All Failed", "Failed to send reply to all");
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    AlertService.showError("Reply All Error", "Cannot connect to mail server. Your reply was not sent. Please try again.");
                });
            }
        }).start();
    }

    public void forwardEmail(String originalEmailId, List<String> to, String subject, String body) {
        new Thread(() -> {
            Email forwardEmail = new Email(model.getUser(), to, subject, body);
            try {
                boolean success = forwardEmailConnect(originalEmailId, forwardEmail);
                Platform.runLater(() -> {
                    if (success) {
                        AlertService.showSuccess("Forward Sent", "Your forward has been sent successfully!");
                    } else {
                        AlertService.showError("Forward Failed", "Failed to forward email");
                    }
                });
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() -> {
                    AlertService.showError("Forward Error", "Cannot connect to mail server. Your email was not forwarded. Please try again.");
                });
            }
        }).start();
    }

    private boolean replyEmailConnect(String originalEmailId, Email replyEmail) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.REPLY_EMAIL);
            output.writeObject(originalEmailId);
            output.writeObject(replyEmail);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

    private boolean replyAllEmailConnect(String originalEmailId, Email replyAllEmail) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.REPLY_ALL_EMAIL);
            output.writeObject(originalEmailId);
            output.writeObject(replyAllEmail);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }

    private boolean forwardEmailConnect(String originalEmailId, Email forwardEmail) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            output.writeObject(Commands.FORWARD_EMAIL);
            output.writeObject(originalEmailId);
            output.writeObject(forwardEmail);

            Object response = input.readObject();
            return (Boolean) response;
        }
    }
    
    public void startAutoRefresh() {
        if (autoRefreshScheduler == null || autoRefreshScheduler.isShutdown()) {
            autoRefreshScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "AutoRefresh-Thread");
                t.setDaemon(true);
                return t;
            });
        }
        
        scheduleNextRefresh();
    }
    
    public void stopAutoRefresh() {
        if (autoRefreshTask != null) {
            autoRefreshTask.cancel(true);
            autoRefreshTask = null;
        }
        if (autoRefreshScheduler != null) {
            autoRefreshScheduler.shutdown();
            autoRefreshScheduler = null;
        }
        stopHeartbeat();
    }
    
    private void scheduleNextRefresh() {
        if (autoRefreshTask != null) {
            autoRefreshTask.cancel(false);
        }
        
        autoRefreshTask = autoRefreshScheduler.schedule(() -> {
            refreshEmailsAuto();
            
            if (!autoRefreshScheduler.isShutdown()) {
                scheduleNextRefresh();
            }
        }, refreshInterval, TimeUnit.SECONDS);
    }
    
    private void refreshEmailsAuto() {
        Thread refreshThread = new Thread(() -> {
            try {
                List<Email> newEmails = fetchNewEmailsConnect(model.getUser());

                Platform.runLater(() -> {
                    if (newEmails != null && !newEmails.isEmpty()) {
                        model.addNewEmails(FXCollections.observableList(newEmails));
                        if (onEmailCountChange != null) {
                            onEmailCountChange.run();
                        }
                    }
                });
            } catch (Exception e) {
                //intenzionale
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }
    
    public void startHeartbeat() {
        if (heartbeatScheduler == null || heartbeatScheduler.isShutdown()) {
            heartbeatScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "Heartbeat-Thread");
                t.setDaemon(true);
                return t;
            });
        }
        
        checkServerStatus();
        scheduleNextHeartbeat();
    }
    
    public void stopHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
            heartbeatTask = null;
        }
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdown();
            heartbeatScheduler = null;
        }
    }
    
    private void scheduleNextHeartbeat() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(false);
        }
        
        heartbeatTask = heartbeatScheduler.schedule(() -> {
            checkServerStatus();
            
            if (!heartbeatScheduler.isShutdown()) {
                scheduleNextHeartbeat();
            }
        }, heartbeatInterval, TimeUnit.SECONDS);
    }
    
    private void checkServerStatus() {
        Thread heartbeatThread = new Thread(() -> {
            boolean serverOnline = false;
            try (Socket socket = new Socket()) {
                socket.connect(new java.net.InetSocketAddress(SERVER_HOST, SERVER_PORT), 2000);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                output.writeObject(Commands.PING);
                serverOnline = true;
            } catch (IOException e) {
                serverOnline = false;
            }
            
            final boolean currentStatus = serverOnline;
            
            if (currentStatus != lastServerStatus) {
                lastServerStatus = currentStatus;
                Platform.runLater(() -> {
                    if (onServerStatusChange != null) {
                        onServerStatusChange.run();
                    }
                    
                    if (currentStatus && model.getConnectionStatus()) {
                        refreshEmails();
                    }
                });
            }
        });
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }
    
    public boolean isServerOnline() {
        return lastServerStatus;
    }

}
