package com.econest.model;

// Anyone who can receive an email from the system (clients and delivery agents)
public interface Notifiable {
    String getName();
    String getEmail();
}
