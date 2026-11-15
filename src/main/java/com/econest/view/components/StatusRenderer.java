package com.econest.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// Shows a status word (order status, stock status...) as coloured text
// with a small dot in front of it, so the user can see the status at a glance
public class StatusRenderer extends DefaultTableCellRenderer {
    private Color dot = Theme.MUTED;
    private boolean hasText;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focus, int row, int column) {
        super.getTableCellRendererComponent(table, value, selected, false, row, column);
        String text = value == null ? "" : value.toString();
        hasText = !text.isEmpty();
        // left padding leaves room for the dot
        setBorder(BorderFactory.createEmptyBorder(0, 26, 0, 10));
        setFont(Theme.font(Font.BOLD, 12));

        switch (text) {
            case "Delivered":
            case "In stock":
            case "Available":
            case "Sent":
                dot = new Color(0x5E7B52);      // green - all good
                break;
            case "Dispatched":
            case "Assigned":
                dot = new Color(0x5B5F7A);      // slate - on the way
                break;
            case "Pending":
            case "Low stock":
                dot = new Color(0xC08A2E);      // amber - needs attention
                break;
            case "Cancelled":
            case "Out of stock":
            case "Failed":
            case "Busy":
                dot = Theme.DANGER;             // red - problem
                break;
            default:
                dot = Theme.MUTED;
        }
        setForeground(dot);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (hasText) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(dot);
            g2.fillOval(12, getHeight() / 2 - 3, 7, 7);
            g2.dispose();
        }
    }
}
