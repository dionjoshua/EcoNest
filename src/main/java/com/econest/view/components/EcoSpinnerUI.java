package com.econest.view.components;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

// Number spinner with small flat up/down arrows
public class EcoSpinnerUI extends BasicSpinnerUI {

    public static ComponentUI createUI(JComponent c) {
        return new EcoSpinnerUI();
    }

    @Override
    protected Component createNextButton() {
        JButton button = new ArrowButton(true);
        installNextButtonListeners(button);
        return button;
    }

    @Override
    protected Component createPreviousButton() {
        JButton button = new ArrowButton(false);
        installPreviousButtonListeners(button);
        return button;
    }

    private static class ArrowButton extends JButton {
        private final boolean up;

        ArrowButton(boolean up) {
            this.up = up;
            setBorder(BorderFactory.createEmptyBorder());
            setFocusable(false);
        }

        @Override
        public void updateUI() {
            // plain button, painted below
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(20, 9);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(getModel().isPressed() ? Theme.PRIMARY : Theme.MUTED);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cx = getWidth() / 2;
            int cy = getHeight() / 2 + (up ? 1 : -1);
            int d = up ? -2 : 2;
            g2.drawPolyline(new int[]{cx - 3, cx, cx + 3}, new int[]{cy - d, cy + d, cy - d}, 3);
            g2.dispose();
        }
    }
}
