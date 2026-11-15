package com.econest.view;

import com.econest.model.DeliveryAgent;
import com.econest.model.EmailLog;
import com.econest.model.Order;
import com.econest.model.OrderStatus;
import com.econest.view.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryPanel {
    private JPanel rootPanel;
    private JCheckBox chkActiveOnly;
    private JPanel cardTable;
    private JTable tblDeliveries;
    private JTable tblEmailLog;
    private JPanel cardForm;
    private JLabel lblOrder;
    private JLabel lblOrderInfo;
    private JComboBox cmbAgent;
    private JTextField txtDeliveryDate;
    private JButton btnAssign;
    private JButton btnDispatch;
    private JButton btnDelivered;
    private JLabel lblEmailStatus;

    private final DefaultTableModel model = Theme.tableModel("Order no", "Client", "Total", "Agent", "Deliver on", "Status");
    private final DefaultTableModel emailModel = Theme.tableModel("Time", "Sent to", "Subject", "Result");
    private List<Order> rows = new ArrayList<>();

    public DeliveryPanel() {
        tblDeliveries.setModel(model);
        Theme.styleTable(tblDeliveries);
        Theme.columnWidths(tblDeliveries, 92, 150, 105, 150, 95, 105);
        Theme.rightAlign(tblDeliveries, 2);
        tblDeliveries.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        tblEmailLog.setModel(emailModel);
        Theme.styleTable(tblEmailLog);
        Theme.columnWidths(tblEmailLog, 115, 170, 270, 85);
        tblEmailLog.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        tblEmailLog.setRowSelectionAllowed(false);
        showSelected(null);
    }

    private void createUIComponents() {
        cardTable = new RoundedPanel();
        cardForm = new RoundedPanel();
    }

    public void setAgents(List<DeliveryAgent> agents) {
        cmbAgent.removeAllItems();
        for (DeliveryAgent a : agents) {
            cmbAgent.addItem(a);
        }
    }

    public void showOrders(List<Order> orders) {
        rows = orders;
        model.setRowCount(0);
        for (Order o : orders) {
            model.addRow(new Object[]{o.getOrderNo(), o.getClientName(), Theme.money(o.getTotal()),
                    o.getAgentName() == null ? "Not assigned" : o.getAgentName(),
                    o.getDeliveryDate() == null ? "-" : o.getDeliveryDate(), o.getStatus().toString()});
        }
    }

    public void showEmailLog(List<EmailLog> log) {
        emailModel.setRowCount(0);
        for (EmailLog e : log) {
            emailModel.addRow(new Object[]{e.getSentAt(), e.getRecipient(), e.getSubject(), e.getStatus()});
        }
    }

    public Order getSelectedOrder() {
        int row = tblDeliveries.getSelectedRow();
        return row >= 0 && row < rows.size() ? rows.get(row) : null;
    }

    public void selectOrder(int orderId) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getId() == orderId) {
                tblDeliveries.setRowSelectionInterval(i, i);
                return;
            }
        }
        showSelected(null);
    }

    // only the buttons that make sense for the order's current status are enabled
    public void showSelected(Order o) {
        if (o == null) {
            lblOrder.setText("Select an order");
            lblOrderInfo.setText("Click a row in the table");
        } else {
            lblOrder.setText(o.getOrderNo() + "  -  " + o.getStatus());
            lblOrderInfo.setText(o.getClientName());
            txtDeliveryDate.setText(o.getDeliveryDate() == null ? "" : o.getDeliveryDate().toString());
        }
        OrderStatus status = o == null ? null : o.getStatus();
        btnAssign.setEnabled(status == OrderStatus.PENDING || status == OrderStatus.ASSIGNED);
        btnAssign.setText(status == OrderStatus.ASSIGNED ? "Change agent" : "Assign agent");
        btnDispatch.setEnabled(status == OrderStatus.ASSIGNED);
        btnDelivered.setEnabled(status == OrderStatus.DISPATCHED);
    }

    public void setEmailStatus(String text, boolean problem) {
        // html so that a long message wraps inside the side panel
        lblEmailStatus.setText("<html><body style='width:200px'>" + text + "</body></html>");
        lblEmailStatus.setForeground(problem ? Theme.DANGER : Theme.SUCCESS);
    }

    public DeliveryAgent getSelectedAgent() {
        return (DeliveryAgent) cmbAgent.getSelectedItem();
    }

    public JPanel getRootPanel() { return rootPanel; }
    public JTable getTblDeliveries() { return tblDeliveries; }
    public JCheckBox getChkActiveOnly() { return chkActiveOnly; }
    public JComboBox getCmbAgent() { return cmbAgent; }
    public JTextField getTxtDeliveryDate() { return txtDeliveryDate; }
    public JButton getBtnAssign() { return btnAssign; }
    public JButton getBtnDispatch() { return btnDispatch; }
    public JButton getBtnDelivered() { return btnDelivered; }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setBackground(new Color(-527378));
        rootPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setOpaque(false);
        rootPanel.add(panel1, BorderLayout.NORTH);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel2.setOpaque(false);
        panel1.add(panel2, BorderLayout.WEST);
        final JLabel label1 = new JLabel();
        label1.setText("Deliveries");
        label1.setForeground(new Color(-14018019));
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 22, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Assign agents, schedule and track each order");
        label2.setForeground(new Color(-7570572));
        Font label2Font = this.$$$getFont$$$(null, -1, 12, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 0, 0);
        panel2.add(label2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        panel3.setOpaque(false);
        panel1.add(panel3, BorderLayout.EAST);
        chkActiveOnly = new JCheckBox();
        chkActiveOnly.setText("Hide delivered and cancelled");
        chkActiveOnly.setSelected(true);
        chkActiveOnly.setOpaque(false);
        panel3.add(chkActiveOnly);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(16, 0));
        panel4.setOpaque(false);
        rootPanel.add(panel4, BorderLayout.CENTER);
        cardTable.setLayout(new BorderLayout(0, 12));
        cardTable.setOpaque(false);
        panel4.add(cardTable, BorderLayout.CENTER);
        cardTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        cardTable.add(scrollPane1, BorderLayout.CENTER);
        tblDeliveries = new JTable();
        scrollPane1.setViewportView(tblDeliveries);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 6));
        panel5.setOpaque(false);
        cardTable.add(panel5, BorderLayout.SOUTH);
        final JLabel label3 = new JLabel();
        label3.setText("Email notifications sent");
        label3.setForeground(new Color(-14018019));
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, 13, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        panel5.add(label3, BorderLayout.NORTH);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setPreferredSize(new Dimension(-1, 150));
        panel5.add(scrollPane2, BorderLayout.CENTER);
        tblEmailLog = new JTable();
        scrollPane2.setViewportView(tblEmailLog);
        cardForm.setLayout(new GridBagLayout());
        cardForm.setOpaque(false);
        cardForm.setPreferredSize(new Dimension(292, -1));
        panel4.add(cardForm, BorderLayout.EAST);
        cardForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        lblOrder = new JLabel();
        lblOrder.setText("Select an order");
        lblOrder.setForeground(new Color(-14018019));
        Font lblOrderFont = this.$$$getFont$$$(null, Font.BOLD, 15, lblOrder.getFont());
        if (lblOrderFont != null) lblOrder.setFont(lblOrderFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardForm.add(lblOrder, gbc);
        lblOrderInfo = new JLabel();
        lblOrderInfo.setText("Click a row in the table");
        lblOrderInfo.setForeground(new Color(-7570572));
        Font lblOrderInfoFont = this.$$$getFont$$$(null, -1, 12, lblOrderInfo.getFont());
        if (lblOrderInfoFont != null) lblOrderInfo.setFont(lblOrderInfoFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 14, 0);
        cardForm.add(lblOrderInfo, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Assign delivery");
        label4.setForeground(new Color(-12451305));
        Font label4Font = this.$$$getFont$$$(null, Font.BOLD, 13, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 4, 0);
        cardForm.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Delivery agent");
        label5.setForeground(new Color(-7570572));
        Font label5Font = this.$$$getFont$$$(null, -1, 12, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 3, 0);
        cardForm.add(label5, gbc);
        cmbAgent = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardForm.add(cmbAgent, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Delivery date (YYYY-MM-DD)");
        label6.setForeground(new Color(-7570572));
        Font label6Font = this.$$$getFont$$$(null, -1, 12, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 3, 0);
        cardForm.add(label6, gbc);
        txtDeliveryDate = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardForm.add(txtDeliveryDate, gbc);
        btnAssign = new JButton();
        btnAssign.setText("Assign agent");
        btnAssign.setBackground(new Color(-12451305));
        btnAssign.setForeground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(14, 0, 0, 0);
        cardForm.add(btnAssign, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Track delivery");
        label7.setForeground(new Color(-12451305));
        Font label7Font = this.$$$getFont$$$(null, Font.BOLD, 13, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(24, 0, 6, 0);
        cardForm.add(label7, gbc);
        btnDispatch = new JButton();
        btnDispatch.setText("Mark as dispatched");
        btnDispatch.setBackground(new Color(-1054753));
        btnDispatch.setForeground(new Color(-14018019));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardForm.add(btnDispatch, gbc);
        btnDelivered = new JButton();
        btnDelivered.setText("Mark as delivered");
        btnDelivered.setBackground(new Color(-1054753));
        btnDelivered.setForeground(new Color(-14018019));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 0, 0);
        cardForm.add(btnDelivered, gbc);
        lblEmailStatus = new JLabel();
        lblEmailStatus.setText(" ");
        lblEmailStatus.setForeground(new Color(-7570572));
        Font lblEmailStatusFont = this.$$$getFont$$$(null, -1, 12, lblEmailStatus.getFont());
        if (lblEmailStatusFont != null) lblEmailStatus.setFont(lblEmailStatusFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 0, 0, 0);
        cardForm.add(lblEmailStatus, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel6.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        cardForm.add(panel6, gbc);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
