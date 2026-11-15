package com.econest.view.components;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

// Combo box drawn to match the text fields
public class EcoComboBoxUI extends BasicComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new EcoComboBoxUI();
    }

    @Override
    protected JButton createArrowButton() {
        return new ArrowButton();
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox);
        popup.setBorder(BorderFactory.createLineBorder(new Color(0xDCD1C6)));
        return popup;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        g.setColor(Color.WHITE);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        // never draw the value as "selected", it looks odd inside the rounded field
        super.paintCurrentValue(g, bounds, false);
    }

    // small down arrow, painted by hand so it does not use the normal button look
    private static class ArrowButton extends JButton {
        ArrowButton() {
            setBorder(BorderFactory.createEmptyBorder());
            setFocusable(false);
        }

        @Override
        public void updateUI() {
            // keep it plain, no button UI
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(22, 18);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Theme.MUTED);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            g2.drawPolyline(new int[]{cx - 4, cx, cx + 4}, new int[]{cy - 2, cy + 2, cy - 2}, 3);
            g2.dispose();
        }
    }
}
