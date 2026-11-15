package com.econest.util;

// Thrown when something typed into a form is missing or wrong.
// The message is shown to the user as it is.
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
