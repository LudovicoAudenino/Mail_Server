package it.ludovico.server.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class MailboxesRepository {
    private Map<String, List<Email>> accounts = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Path DATA_PATH = Paths.get("data", "database.txt");

    public MailboxesRepository() {
        loadMailBoxes();
    }

    public void loadMailBoxes() {
        lock.writeLock().lock();
        try {
            if (Files.exists(DATA_PATH)) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_PATH.toFile()))) {
                    Object o = ois.readObject();
                    if (o instanceof Map) {
                        accounts = (Map<String, List<Email>>) o;
                    }
                }   catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    accounts = new ConcurrentHashMap<>();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveMailBoxes() {
        lock.writeLock().lock();
        try {
            Files.createDirectories(DATA_PATH.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_PATH.toFile()))) {
                oos.writeObject(accounts);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String, List<Email>> getAccounts() {
        lock.readLock().lock();
        try {
            HashMap<String, List<Email>> map = new HashMap<>(accounts);
            System.out.println(map.toString());
            return new HashMap<>(accounts);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Email> getMailBox(String account) {
        lock.readLock().lock();
        try {
            List<Email> mailbox = accounts.get(account);
            return mailbox != null ? new ArrayList<Email>(mailbox) : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<String> getAccountMails() {
        lock.readLock().lock();
        try {
            return new HashSet<>(accounts.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addAccount(String mail) {
        lock.writeLock().lock();
        try {
            accounts.putIfAbsent(mail, new ArrayList<Email>());
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addEmail(String mail, Email email) {
        lock.writeLock().lock();
        try {
            accounts.get(mail).add(email);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void deleteEmail(String mail, Email email) {
        lock.writeLock().lock();
        try {
            accounts.get(mail).remove(email);
        } finally {
            lock.writeLock().unlock();
        }
    }


}
