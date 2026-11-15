package com.econest.service;

// Called on the Swing thread when a background email has finished
public interface EmailCallback {
    void finished(boolean sent, String message);
}
