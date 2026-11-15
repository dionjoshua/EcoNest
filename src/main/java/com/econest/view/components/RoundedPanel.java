package com.econest.view.components;

import javax.swing.*;
import java.awt.*;

// White card with slightly rounded corners that the screens put their content on
public class RoundedPanel extends JPanel {

    public RoundedPanel() {
        setOpaque(false);
        setBackground(Theme.CARD);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
        g2.setColor(Theme.LINE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
        g2.dispose();
        super.paintComponent(g);
    }
}
