package com.econest.util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// Runs the same action whenever the text in a field changes (used for the search boxes)
public class SearchListener implements DocumentListener {
    private final Runnable action;

    public SearchListener(Runnable action) {
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        action.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        action.run();
    }
}
