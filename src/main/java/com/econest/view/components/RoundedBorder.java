package com.econest.view.components;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

// Rounded outline used for text fields, combo boxes and spinners.
// Turns green when the field (or something inside it) has the focus.
public class RoundedBorder extends AbstractBorder {
    private static final int ARC = 6;

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D shape = new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, width - 1, height - 1, ARC, ARC);

        // cover the square corners of the field with the colour behind it
        if (c.getParent() != null) {
            Area corners = new Area(new Rectangle(x, y, width, height));
            corners.subtract(new Area(shape));
            g2.setColor(c.getParent().getBackground());
            g2.fill(corners);
        }

        boolean focused = c.isFocusOwner() || isChildFocused(c);
        g2.setColor(focused ? Theme.PRIMARY : new Color(0xDCD1C6));
        g2.draw(shape);
        g2.dispose();
    }

    private boolean isChildFocused(Component c) {
        Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return owner != null && c instanceof Container && ((Container) c).isAncestorOf(owner);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(7, 10, 7, 10);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(7, 10, 7, 10);
        return insets;
    }
}
