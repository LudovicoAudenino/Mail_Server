package it.ludovico.server.model;


import it.ludovico.shared.model.Email;
import it.ludovico.server.repository.MailboxesRepository;
import it.ludovico.server.service.EmailService;
import it.ludovico.server.service.LogService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerModel implements EmailService {

    private final ObservableList<String> logs = FXCollections.observableArrayList();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger requestsProcessed = new AtomicInteger(0);
    

    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);
    private final IntegerProperty requestsProcessedProperty = new SimpleIntegerProperty(0);

    private final MailboxesRepository mailboxesRepository;

    private static final List<String> ACCOUNT_REGISTERED = Arrays.asList(
            "ludo@gmail.com", "franci@gmail.com", "elisa@gmail.com"
    );

    public ServerModel() {
        LogService.setLogHandler(logs::add);
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
                LogService.error("Errore nel salvataggio: " + e.getMessage());
                return false;
            }

        }
        return false;
    }

    public void initializeAccounts() {
        Set<String> existingAccounts = mailboxesRepository.getAccountMails();
        boolean needsSave = false;
        
        for(String account : ACCOUNT_REGISTERED) {
            if (!existingAccounts.contains(account)) {
                mailboxesRepository.addAccount(account);
                needsSave = true;
                LogService.info("Inizializzato account mancante: " + account);
            }
        }
        
        if (needsSave) {
            try {
                mailboxesRepository.saveMailBoxes();
            } catch (IOException e) {
                LogService.error("Errore nel salvataggio durante inizializzazione: " + e.getMessage());
            }
        }
    }

    public List<Email> getUserMailbox(String user) {
        List<Email> mailbox = mailboxesRepository.getMailBox(user);
        if (mailbox == null && checkRegisteredAccount(user)) {
            mailboxesRepository.addAccount(user);
            try {
                mailboxesRepository.saveMailBoxes();
                LogService.info("Creata mailbox mancante per: " + user);
                return new ArrayList<>();
            } catch (IOException e) {
                LogService.error("Errore nella creazione mailbox per " + user + ": " + e.getMessage());
                return null;
            }
        }
        return mailbox;
    }

    public List<Email> getNewEmails(String user) {
        List<Email> mailbox = getUserMailbox(user);
        if (mailbox == null) {
            return null;
        }
        return mailbox.stream()
                .filter(email -> !email.isDeliveredTo(user))
                .toList();
    }

    public void markEmailsAsDelivered(String user, List<Email> emails) {
        for (Email email : emails) {
            email.markDeliveredTo(user);
        }
        try {
            mailboxesRepository.saveMailBoxes();
        } catch (IOException e) {
            LogService.error("Errore nel salvataggio dopo aver marcato email come consegnate: " + e.getMessage());
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

    public BooleanProperty runningProperty() {
        return runningProperty;
    }
    
    public IntegerProperty requestsProcessedProperty() {
        return requestsProcessedProperty;
    }

    public boolean deleteEmail(String user, String emailId) {
        List<Email> mailbox = getUserMailbox(user);
        if (mailbox == null) {
            return false;
        }
        
        Email emailToDelete = null;
        for (Email email : mailbox) {
            if (email.getId().toString().equals(emailId)) {
                emailToDelete = email;
                break;
            }
        }
        
        if (emailToDelete != null) {
            mailboxesRepository.deleteEmail(user, emailToDelete);
            try {
                mailboxesRepository.saveMailBoxes();
                LogService.info("Email deleted for user " + user + ": " + emailId);
                return true;
            } catch (IOException e) {
                LogService.error("Error saving after deleting email: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean replyToEmail(String originalEmailId, Email replyEmail) {
        return sendEmail(replyEmail);
    }

    @Override
    public boolean replyAllToEmail(String originalEmailId, Email replyEmail) {
        return sendEmail(replyEmail);
    }

    @Override
    public boolean forwardEmail(String originalEmailId, Email forwardEmail) {
        return sendEmail(forwardEmail);
    }

}