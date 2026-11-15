package com.econest.view.components;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

// Tabs shown as plain text with a green underline on the selected one
public class EcoTabbedPaneUI extends BasicTabbedPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new EcoTabbedPaneUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(8, 14, 10, 14);
        tabAreaInsets = new Insets(0, 0, 0, 0);
        contentBorderInsets = new Insets(1, 0, 0, 0);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        tabPane.setOpaque(false);
    }

    @Override
    protected void paintTabBackground(Graphics g, int placement, int index, int x, int y, int w, int h, boolean selected) {
    }

    @Override
    protected void paintTabBorder(Graphics g, int placement, int index, int x, int y, int w, int h, boolean selected) {
        if (selected) {
            g.setColor(Theme.PRIMARY);
            g.fillRect(x, y + h - 3, w, 3);
        }
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int placement, Rectangle[] rects, int index,
                                       Rectangle iconRect, Rectangle textRect, boolean selected) {
    }

    @Override
    protected void paintContentBorder(Graphics g, int placement, int selectedIndex) {
        int y = calculateTabAreaHeight(placement, runCount, maxTabHeight);
        g.setColor(Theme.LINE);
        g.fillRect(0, y, tabPane.getWidth(), 1);
    }

    @Override
    protected void paintText(Graphics g, int placement, Font font, FontMetrics metrics, int index,
                             String title, Rectangle textRect, boolean selected) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(Theme.font(selected ? Font.BOLD : Font.PLAIN, 13));
        g2.setColor(selected ? Theme.INK : Theme.MUTED);
        g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
    }

    @Override
    protected int getTabLabelShiftY(int placement, int index, boolean selected) {
        return 0;
    }

    @Override
    protected int getTabLabelShiftX(int placement, int index, boolean selected) {
        return 0;
    }
}
