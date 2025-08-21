package it.ludovico.server.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Account {
    private String email;
    private List<Email> mailbox;

    public Account(String email) {
        this.email = email;
        mailbox = Collections.synchronizedList(new ArrayList<>());
    }
}
