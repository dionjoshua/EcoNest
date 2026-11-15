package com.econest.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Validator {

    private Validator() {
    }

    public static String required(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    public static String email(String value) throws ValidationException {
        String email = required(value, "Email");
        if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new ValidationException("Email address is not valid.");
        }
        return email;
    }

    // Sri Lankan numbers: 0771234567 or +94771234567
    public static String phone(String value) throws ValidationException {
        String phone = required(value, "Phone").replace(" ", "").replace("-", "");
        if (!phone.matches("^(0\\d{9}|\\+94\\d{9})$")) {
            throw new ValidationException("Phone number should have 10 digits, for example 0771234567.");
        }
        return phone;
    }

    public static double positiveNumber(String value, String fieldName) throws ValidationException {
        try {
            double number = Double.parseDouble(required(value, fieldName));
            if (number <= 0) {
                throw new ValidationException(fieldName + " must be more than zero.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a number.");
        }
    }

    public static int wholeNumber(String value, String fieldName, int minimum) throws ValidationException {
        try {
            int number = Integer.parseInt(required(value, fieldName));
            if (number < minimum) {
                throw new ValidationException(fieldName + " cannot be less than " + minimum + ".");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a whole number.");
        }
    }

    public static LocalDate futureDate(String value, String fieldName) throws ValidationException {
        try {
            LocalDate date = LocalDate.parse(required(value, fieldName));
            if (date.isBefore(LocalDate.now())) {
                throw new ValidationException(fieldName + " cannot be in the past.");
            }
            return date;
        } catch (DateTimeParseException e) {
            throw new ValidationException(fieldName + " must be typed as YYYY-MM-DD, for example " + LocalDate.now().plusDays(1) + ".");
        }
    }
}
