package com.econest.view.components;

import javax.swing.*;
import java.awt.*;

// The little leaf next to the EcoNest name
public class LeafIcon implements Icon {
    private final int size;
    private final Color color;

    public LeafIcon(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(EcoRatingRenderer.leaf(x, y, size));
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
