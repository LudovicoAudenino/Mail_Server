package it.ludovico.server.model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private final ObservableList<String> logs = FXCollections.observableArrayList();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger connectedClients = new AtomicInteger(0);

    private final MailboxesRepository mailboxesRepository;

    private static final List<String> ACCOUNT_REGISTERED = Arrays.asList(
            "ludo@gmail.com", "franci@gmail.com", "elisa@gmail.com"
    );

    public Server() {
        this.mailboxesRepository = new MailboxesRepository();

        if (mailboxesRepository.getAccounts().isEmpty()) {
            for(String account : ACCOUNT_REGISTERED) {
                mailboxesRepository.addAccount(account);
            }
        }
        mailboxesRepository.saveMailBoxes();
    }

    public boolean checkRegisteredAccount(List<String> accounts) {
        boolean check = true;
        for (String account : accounts) {
            if (!ACCOUNT_REGISTERED.contains(account)) {
                check = false;
            }
        }
        return check;
    }

    public boolean sendEmail(Email email) {
        if (email != null &&  checkRegisteredAccount(email.getTo())) {}
    }



}