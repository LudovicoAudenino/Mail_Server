package it.ludovico.server.service;

import it.ludovico.shared.model.Email;

import java.util.List;

public interface EmailService {
    boolean sendEmail(Email email);
    boolean checkToAccounts(List<String> accounts);
    boolean checkRegisteredAccount(String account);
    List<Email> getUserMailbox(String user);
    List<Email> getNewEmails(String user);
    void markEmailsAsDelivered(String user, List<Email> emails);
}
