package com.econest.view.components;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

// Flat rounded buttons. The colour comes from the button's own background,
// so it can be chosen in the form designer.
public class EcoButtonUI extends BasicButtonUI {

    public static ComponentUI createUI(JComponent c) {
        return new EcoButtonUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setRolloverEnabled(true);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = c.getWidth();
        int h = c.getHeight();

        if (Boolean.TRUE.equals(c.getClientProperty("eco.nav"))) {
            // sidebar button: only gets a background when hovered or selected
            boolean selected = Boolean.TRUE.equals(c.getClientProperty("eco.selected"));
            if (selected || model.isRollover()) {
                // a slightly lighter wine colour behind the item
                g2.setColor(selected ? new Color(0x5A0E26) : new Color(0x50081F));
                g2.fillRect(0, 0, w, h);
            }
            if (selected) {
                // thin gold line on the left of the selected item
                g2.setColor(Theme.GOLD);
                g2.fillRect(0, 8, 2, h - 16);
            }
        } else {
            Color bg = c.getBackground();
            if (!button.isEnabled()) {
                bg = mix(bg, Color.WHITE, 0.55f);
            } else if (model.isPressed()) {
                bg = mix(bg, Color.BLACK, 0.18f);
            } else if (model.isRollover()) {
                bg = mix(bg, Color.BLACK, 0.08f);
            }
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, 5, 5);
        }
        g2.dispose();
        super.paint(g, c);
    }

    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fm = g2.getFontMetrics();
        Color fg = b.getForeground();
        if (!b.isEnabled()) {
            fg = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 110);   // faded text when disabled
        }
        g2.setColor(fg);
        g2.drawString(text, textRect.x, textRect.y + fm.getAscent());
    }

    private static Color mix(Color a, Color b, float amount) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * amount);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * amount);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * amount);
        return new Color(r, g, bl);
    }
}
