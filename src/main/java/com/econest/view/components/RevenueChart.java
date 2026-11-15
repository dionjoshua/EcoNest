package com.econest.view.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Simple bar chart for the dashboard, drawn with Graphics2D
public class RevenueChart extends JPanel {
    private final List<String> labels = new ArrayList<>();
    private final List<Double> values = new ArrayList<>();

    public RevenueChart() {
        setOpaque(false);
    }

    public void setData(List<String> newLabels, List<Double> newValues) {
        labels.clear();
        values.clear();
        labels.addAll(newLabels);
        values.addAll(newValues);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double max = 1;
        for (double v : values) {
            max = Math.max(max, v);
        }

        int top = 26;
        int bottom = getHeight() - 28;
        int chartHeight = bottom - top;
        int slot = getWidth() / values.size();
        int barWidth = Math.min(40, slot - 30);
        g2.setFont(Theme.font(Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();

        // base line
        g2.setColor(Theme.LINE);
        g2.drawLine(0, bottom, getWidth(), bottom);

        for (int i = 0; i < values.size(); i++) {
            double value = values.get(i);
            int barHeight = (int) Math.round(chartHeight * value / max);
            int x = i * slot + (slot - barWidth) / 2;
            int y = bottom - barHeight;

            // the current month (last bar) is darker than the others
            boolean current = i == values.size() - 1;
            g2.setColor(current ? Theme.PRIMARY : new Color(0xDCC7CB));
            if (barHeight > 0) {
                g2.fillRect(x, y, barWidth, barHeight);
            }

            String amount = shortAmount(value);
            g2.setColor(current ? Theme.INK : Theme.MUTED);
            g2.drawString(amount, x + (barWidth - fm.stringWidth(amount)) / 2, y - 7);

            String label = labels.get(i);
            g2.setColor(Theme.MUTED);
            g2.drawString(label, x + (barWidth - fm.stringWidth(label)) / 2, bottom + 18);
        }
        g2.dispose();
    }

    // 45200 -> "45.2k"
    private String shortAmount(double value) {
        if (value >= 1000) {
            return String.format("%.1fk", value / 1000);
        }
        return String.format("%.0f", value);
    }
}
