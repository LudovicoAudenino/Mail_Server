package it.ludovico.server.model;


import it.ludovico.server.repository.MailboxesRepository;
import it.ludovico.server.service.EmailService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerModel implements EmailService {

    private final ObservableList<String> logs = FXCollections.observableArrayList();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger requestsProcessed = new AtomicInteger(0);
    
    // JavaFX Properties for binding
    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);
    private final IntegerProperty requestsProcessedProperty = new SimpleIntegerProperty(0);

    private final MailboxesRepository mailboxesRepository;

    private static final List<String> ACCOUNT_REGISTERED = Arrays.asList(
            "ludo@gmail.com", "franci@gmail.com", "elisa@gmail.com"
    );

    public ServerModel() {
        this.mailboxesRepository = new MailboxesRepository();
        initializeAccounts();
    }

    public boolean checkToAccounts(List<String> accounts) {
        for (String account : accounts) {
            if (!ACCOUNT_REGISTERED.contains(account)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRegisteredAccount(String account) {
        return ACCOUNT_REGISTERED.contains(account);
    }

    public boolean sendEmail(Email email) {
        if (email != null &&  checkToAccounts(email.getTo())) {
            for (String account : email.getTo()) {
                mailboxesRepository.addEmail(account, email);
            }
            try {
                mailboxesRepository.saveMailBoxes();
                return true;
            } catch (IOException e) {
                addLog("Errore nel salvataggio: " + e.getMessage());
                return false;
            }

        }
        return false;
    }

    public void initializeAccounts() {
        if (mailboxesRepository.getAccounts().isEmpty()) {
            for(String account : ACCOUNT_REGISTERED) {
                mailboxesRepository.addAccount(account);
            }
        }
        try {
            mailboxesRepository.saveMailBoxes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Email> getUserMailbox(String user) {
        return mailboxesRepository.getMailBox(user);
    }

    public List<Email> getNewEmails(String user) {
        return mailboxesRepository.getMailBox(user).stream()
                .filter(email -> !email.isDelivered())
                .toList();
    }

    public void markEmailsAsDelivered(String user, List<Email> emails) {
        for (Email email : emails) {
            email.setDelivered(true);
        }
        try {
            mailboxesRepository.saveMailBoxes();
        } catch (IOException e) {
            addLog("Errore nel salvataggio dopo aver marcato email come consegnate: " + e.getMessage());
        }
    }

    public ObservableList<String> getLogs() {
        return logs;
    }

    public Boolean getRunning() {
        return running.get();
    }

    public AtomicInteger getRequestsProcessed() {
        return requestsProcessed;
    }

    public void incrementRequestsProcessed() {
        int count = requestsProcessed.incrementAndGet();
        Platform.runLater(() -> requestsProcessedProperty.set(count));
    }

    public void setRunning(boolean running) {
        this.running.set(running);
        Platform.runLater(() -> runningProperty.set(running));
    }
    
    // JavaFX Properties getters for binding
    public BooleanProperty runningProperty() {
        return runningProperty;
    }
    
    public IntegerProperty requestsProcessedProperty() {
        return requestsProcessedProperty;
    }

    public void addLog(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = String.format("[%s] %s", timestamp, message);
        Platform.runLater(() -> logs.add(logMessage));
    }

}