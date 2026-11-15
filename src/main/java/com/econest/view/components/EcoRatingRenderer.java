package com.econest.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.Path2D;

// Draws the eco-rating as leaves (out of 5) instead of a number
public class EcoRatingRenderer extends DefaultTableCellRenderer {
    private int rating;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focus, int row, int column) {
        super.getTableCellRendererComponent(table, "", selected, false, row, column);
        rating = value instanceof Integer ? (Integer) value : 0;
        setToolTipText(rating + " out of 5");
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = (getHeight() - 12) / 2;
        for (int i = 0; i < 5; i++) {
            g2.setColor(i < rating ? new Color(0xB8955A) : new Color(0xEAE2D8));
            g2.fill(leaf(10 + i * 15, y, 12));
        }
        g2.dispose();
    }

    // leaf shape: two curves between the bottom-left and top-right corners
    static Path2D leaf(int x, int y, int size) {
        Path2D path = new Path2D.Float();
        path.moveTo(x, y + size);
        path.quadTo(x, y, x + size, y);
        path.quadTo(x + size, y + size, x, y + size);
        path.closePath();
        return path;
    }
}
