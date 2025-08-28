package it.ludovico.shared.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

public class Email implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String from;
    private List<String> to;
    private String subject;
    private String text;
    private LocalDateTime sent;
    private Set<String> deliveredTo;

    public Email(String from, List<String> to, String subject, String text) {
        this.id = UUID.randomUUID();
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.sent = LocalDateTime.now();
        this.deliveredTo = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }
    public String getFrom() {
        return from;
    }
    public List<String> getTo() {
        return to;
    }
    public String getSubject() {
        return subject;
    }
    public String getText() {
        return text;
    }
    public LocalDateTime getSent() {
        return sent;
    }
    public Set<String> getDeliveredTo() {
        return deliveredTo;
    }
    public void setDeliveredTo(Set<String> deliveredTo) {
        this.deliveredTo = deliveredTo;
    }
    public boolean isDeliveredTo(String user) {
        return deliveredTo.contains(user);
    }
    public void markDeliveredTo(String user) {
        deliveredTo.add(user);
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(List<String> to) {
        this.to = to;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", mittente='" + from + '\'' +
                ", destinatari=" + to +
                ", argomento='" + subject + '\'' +
                ", dataSpedizione=" + sent +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(this.id, email.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }
}