package com.econest.view.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// All the colours and the look of the standard components are kept here
// so every screen looks the same. If you want to change the colour scheme,
// this is the only place you need to touch.
//
// Colour scheme: deep wine red (#420217) with warm ivory and a soft gold accent.
public class Theme {
    public static final Color PRIMARY = new Color(0x420217);    // main brand colour (buttons, chart)
    public static final Color SIDEBAR = new Color(0x420217);    // left menu background
    public static final Color GOLD = new Color(0xC3A57A);       // small accents (selected menu line, ratings)
    public static final Color BACKGROUND = new Color(0xF7F3EE); // ivory page background
    public static final Color CARD = Color.WHITE;
    public static final Color INK = new Color(0x2A1A1D);        // normal text
    public static final Color MUTED = new Color(0x8C7B74);      // grey-brown helper text
    public static final Color DANGER = new Color(0xA3412F);     // remove button, low stock numbers
    public static final Color SUCCESS = new Color(0x4F6B45);    // "email sent" message
    public static final Color LINE = new Color(0xE7DED4);       // thin borders
    public static final Color SELECTION = new Color(0xF3E4E6);  // selected table row
    public static final Color HEADER = new Color(0xFAF6F1);     // table header background

    private static String fontFamily = "SansSerif";
    private static String serifFamily = "Serif";   // used for page titles and big numbers

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // setLookAndFeel can throw a few different exceptions, but if any of them
            // happens Java just keeps the default look, so we can safely ignore it
        }

        // nicer font if the computer has it
        List<String> installed = java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String candidate : new String[]{"Segoe UI", "Inter", "Helvetica Neue"}) {
            if (installed.contains(candidate)) {
                fontFamily = candidate;
                break;
            }
        }
        // serif font for headings, it gives the app a more "classic shop" feel
        for (String candidate : new String[]{"Georgia", "Palatino Linotype", "Book Antiqua", "DejaVu Serif"}) {
            if (installed.contains(candidate)) {
                serifFamily = candidate;
                break;
            }
        }
        FontUIResource font = new FontUIResource(fontFamily, Font.PLAIN, 13);
        List<Object> fontKeys = new ArrayList<>();
        for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
            if (key.toString().endsWith(".font")) {
                fontKeys.add(key);
            }
        }
        for (Object key : fontKeys) {
            UIManager.put(key, font);
        }

        UIManager.put("ClassLoader", Theme.class.getClassLoader());
        UIManager.put("ButtonUI", EcoButtonUI.class.getName());
        UIManager.put("ScrollBarUI", EcoScrollBarUI.class.getName());
        UIManager.put("ComboBoxUI", EcoComboBoxUI.class.getName());
        UIManager.put("TabbedPaneUI", EcoTabbedPaneUI.class.getName());
        UIManager.put("SpinnerUI", EcoSpinnerUI.class.getName());

        ColorUIResource ink = new ColorUIResource(INK);
        ColorUIResource white = new ColorUIResource(Color.WHITE);
        ColorUIResource selection = new ColorUIResource(SELECTION);
        Border fieldBorder = new RoundedBorder();

        UIManager.put("Panel.background", new ColorUIResource(BACKGROUND));
        UIManager.put("OptionPane.background", new ColorUIResource(BACKGROUND));
        UIManager.put("OptionPane.messageForeground", ink);
        UIManager.put("Label.foreground", ink);
        UIManager.put("CheckBox.foreground", ink);
        UIManager.put("CheckBox.background", new ColorUIResource(BACKGROUND));
        UIManager.put("CheckBox.icon", new CheckIcon());
        UIManager.put("CheckBox.focus", new ColorUIResource(BACKGROUND));

        UIManager.put("TextField.border", fieldBorder);
        UIManager.put("TextField.background", white);
        UIManager.put("TextField.foreground", ink);
        UIManager.put("TextField.selectionBackground", selection);
        UIManager.put("TextField.selectionForeground", ink);
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(HEADER));
        UIManager.put("FormattedTextField.border", BorderFactory.createEmptyBorder(0, 6, 0, 6));
        UIManager.put("FormattedTextField.selectionBackground", selection);
        UIManager.put("Spinner.border", fieldBorder);
        UIManager.put("Spinner.background", white);
        UIManager.put("Spinner.arrowButtonInsets", new Insets(5, 0, 5, 6));
        UIManager.put("Button.background", new ColorUIResource(new Color(0xEFE7DF)));
        UIManager.put("Button.foreground", ink);

        UIManager.put("ComboBox.background", white);
        UIManager.put("ComboBox.foreground", ink);
        UIManager.put("ComboBox.selectionBackground", selection);
        UIManager.put("ComboBox.selectionForeground", ink);
        UIManager.put("ComboBox.border", fieldBorder);
        UIManager.put("ComboBox.padding", new Insets(0, 2, 0, 2));

        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollPane.background", white);
        UIManager.put("Viewport.background", white);
        UIManager.put("Table.background", white);
        UIManager.put("Table.foreground", ink);
        UIManager.put("Table.selectionBackground", selection);
        UIManager.put("Table.selectionForeground", ink);
        UIManager.put("Table.gridColor", new ColorUIResource(new Color(0xF1EBE4)));
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder(0, 10, 0, 10));
        UIManager.put("TabbedPane.foreground", ink);
        UIManager.put("TabbedPane.background", new ColorUIResource(BACKGROUND));
        UIManager.put("ToolTip.background", white);

        // repaint text fields when focus moves so the border colour changes
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", e -> {
            if (e.getOldValue() instanceof JComponent) {
                repaintField((JComponent) e.getOldValue());
            }
            if (e.getNewValue() instanceof JComponent) {
                repaintField((JComponent) e.getNewValue());
            }
        });
    }

    private static void repaintField(JComponent c) {
        c.repaint();
        // spinner / combo editors sit inside the component that owns the border
        Container parent = c.getParent();
        for (int i = 0; i < 2 && parent != null; i++) {
            parent.repaint();
            parent = parent.getParent();
        }
    }

    public static Font font(int style, int size) {
        return new Font(fontFamily, style, size);
    }

    public static Font serif(int style, int size) {
        return new Font(serifFamily, style, size);
    }

    // Goes through every label on a screen and gives the big ones (titles, totals)
    // the serif font. Doing it here means we don't have to change every .form file.
    public static void applyHeadingFonts(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                Font f = c.getFont();
                if (f != null && f.getSize() >= 20) {
                    // page titles (22) become a little bigger and not bold, numbers stay bold
                    if (f.getSize() == 22) {
                        c.setFont(serif(Font.PLAIN, 27));
                    } else {
                        c.setFont(serif(Font.BOLD, f.getSize() + 2));
                    }
                }
            }
            if (c instanceof Container) {
                applyHeadingFonts((Container) c);
            }
        }
    }

    // ----- buttons -----

    public static void navButton(JButton button) {
        button.putClientProperty("eco.nav", Boolean.TRUE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
    }

    public static void setNavSelected(JButton button, boolean selected) {
        button.putClientProperty("eco.selected", selected);
        button.setForeground(selected ? Color.WHITE : new Color(0xE9D3D8));
        button.repaint();
    }

    // ----- tables -----

    public static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new CellRenderer(SwingConstants.LEFT));

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(10, 36));
        header.setDefaultRenderer(new HeaderRenderer());

        Container scroll = SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
        if (scroll instanceof JScrollPane) {
            ((JScrollPane) scroll).setBorder(BorderFactory.createLineBorder(LINE));
            ((JScrollPane) scroll).getViewport().setBackground(Color.WHITE);
            // the little square above the scroll bar should match the header
            JPanel corner = new JPanel();
            corner.setBackground(HEADER);
            corner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
            ((JScrollPane) scroll).setCorner(JScrollPane.UPPER_RIGHT_CORNER, corner);
        }
    }

    public static void rightAlign(JTable table, int... columns) {
        for (int column : columns) {
            table.getColumnModel().getColumn(column).setCellRenderer(new CellRenderer(SwingConstants.RIGHT));
        }
    }

    public static void columnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] > 0) {
                table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    public static String money(double amount) {
        return String.format("Rs. %,.2f", amount);
    }

    // table cell with some padding
    public static class CellRenderer extends DefaultTableCellRenderer {
        public CellRenderer(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, false, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                       boolean focus, int row, int column) {
            // headers are shown in capitals, it looks cleaner
            String text = value == null ? "" : value.toString().toUpperCase();
            super.getTableCellRendererComponent(table, text, false, false, row, column);
            setBackground(HEADER);
            setForeground(MUTED);
            setFont(font(Font.BOLD, 11));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            return this;
        }
    }

    // small square check box icon
    private static class CheckIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean selected = ((AbstractButton) c).isSelected();
            g2.setColor(selected ? PRIMARY : Color.WHITE);
            g2.fillRoundRect(x, y, 16, 16, 3, 3);
            g2.setColor(selected ? PRIMARY : new Color(0xC9BDB2));
            g2.drawRoundRect(x, y, 15, 15, 3, 3);
            if (selected) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(new int[]{x + 4, x + 7, x + 12}, new int[]{y + 8, y + 11, y + 5}, 3);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
