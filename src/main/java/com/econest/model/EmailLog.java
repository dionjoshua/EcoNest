package com.econest.model;

// Record of an email the system tried to send
public class EmailLog {
    private String sentAt;
    private String recipient;
    private String subject;
    private String status;

    public EmailLog(String sentAt, String recipient, String subject, String status) {
        this.sentAt = sentAt;
        this.recipient = recipient;
        this.subject = subject;
        this.status = status;
    }

    public String getSentAt() { return sentAt; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getStatus() { return status; }
}
