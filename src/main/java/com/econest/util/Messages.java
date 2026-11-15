package com.econest.util;

import javax.swing.*;
import java.awt.*;

// Message boxes used by the controllers
public class Messages {

    private Messages() {
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "EcoNest", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, message + "\n\n" + e.getMessage(), "Something went wrong", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String question) {
        int answer = JOptionPane.showConfirmDialog(parent, question, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return answer == JOptionPane.YES_OPTION;
    }
}
