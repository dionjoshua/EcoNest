package com.econest.service;

import com.econest.dao.EmailLogDAO;
import com.econest.model.Notifiable;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Properties;

// Sends the notification emails through SMTP (Gmail by default).
// The account details are read from config/email.properties.
public class EmailService {
    private static final String CONFIG_FILE = "config/email.properties";

    private final EmailLogDAO logDAO = new EmailLogDAO();

    // The file is read every time so the details can be changed without restarting the program
    private Properties loadConfig() {
        Properties config = new Properties();
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            // If the file is missing we just return empty settings.
            // send() then shows "Email account is not set up", so nothing else to do here.
        }
        return config;
    }

    public void send(String to, String subject, String htmlBody) throws MessagingException, UnsupportedEncodingException {
        Properties config = loadConfig();
        final String username = config.getProperty("mail.username", "").trim();
        String host = config.getProperty("mail.smtp.host", "smtp.gmail.com").trim();
        String port = config.getProperty("mail.smtp.port", "587").trim();
        String senderName = config.getProperty("mail.sender.name", "EcoNest").trim();
        String pw = config.getProperty("mail.password", "").trim();
        if (host.contains("gmail")) {
            pw = pw.replace(" ", "");   // Gmail shows app passwords in groups of four with spaces
        }
        // the Authenticator below is an inner class, and inner classes can only use
        // variables that are final, so we copy the password into a final variable
        final String password = pw;

        if (username.isEmpty() || password.isEmpty()) {
            throw new MessagingException("Email account is not set up. Add the username and password to " + CONFIG_FILE);
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        if (port.equals("465")) {
            props.put("mail.smtp.ssl.enable", "true");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
        }
        // give up after 20 seconds instead of hanging if there is no internet
        props.put("mail.smtp.connectiontimeout", "20000");
        props.put("mail.smtp.timeout", "20000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username, senderName));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");
        message.setContent(htmlBody, "text/html; charset=UTF-8");
        Transport.send(message);
    }

    // Sending an email can take a few seconds. If we did it on the normal Swing thread
    // the whole window would freeze, so the sending is done on a separate Thread.
    public void sendInBackground(Notifiable recipient, String subject, String htmlBody, EmailCallback callback) {
        Thread emailThread = new Thread(() -> {
            boolean sent = false;
            String error = "";
            try {
                send(recipient.getEmail(), subject, htmlBody);
                sent = true;
            } catch (MessagingException e) {
                // wrong password, no internet connection, bad email address ...
                error = e.getMessage();
            } catch (UnsupportedEncodingException e) {
                // the sender name in email.properties has characters that can't be used
                error = e.getMessage();
            }

            // Swing screens should only be changed from the Swing thread,
            // so we hand the result back to it with invokeLater
            final boolean wasSent = sent;
            final String errorMessage = error;
            SwingUtilities.invokeLater(() -> finishSending(recipient, subject, wasSent, errorMessage, callback));
        });
        emailThread.start();
    }

    // Saves the result in the email log table and tells the screen what happened
    private void finishSending(Notifiable recipient, String subject, boolean sent, String error,
                               EmailCallback callback) {
        try {
            logDAO.save(recipient.getName() + " <" + recipient.getEmail() + ">", subject, sent ? "Sent" : "Failed");
        } catch (SQLException e) {
            // the email itself already went out, so we only print the log problem
            e.printStackTrace();
        }

        if (sent) {
            callback.finished(true, "Email sent to " + recipient.getEmail());
        } else {
            callback.finished(false, "Email to " + recipient.getEmail() + " was not sent. " + error);
        }
    }
}
