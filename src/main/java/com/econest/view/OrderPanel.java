package com.econest.view;

import com.econest.model.Client;
import com.econest.model.Order;
import com.econest.model.OrderItem;
import com.econest.model.OrderStatus;
import com.econest.model.Product;
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

public class OrderPanel {
    private JPanel rootPanel;
    private JTabbedPane tabs;
    private JPanel cardOrderForm;
    private JComboBox cmbClient;
    private JTextField txtAddress;
    private JComboBox cmbProduct;
    private JLabel lblAvailable;
    private JSpinner spnQuantity;
    private JButton btnAddItem;
    private JPanel cardItems;
    private JTable tblItems;
    private JButton btnRemoveItem;
    private JLabel lblTotal;
    private JButton btnPlaceOrder;
    private JComboBox cmbStatusFilter;
    private JButton btnCancelOrder;
    private JPanel cardOrders;
    private JTable tblOrders;
    private JLabel lblOrderItems;
    private JTable tblOrderItems;

    private final DefaultTableModel itemsModel = Theme.tableModel("Product", "Unit price", "Qty", "Subtotal");
    private final DefaultTableModel ordersModel = Theme.tableModel("Order no", "Date", "Client", "Total", "Agent", "Status");
    private final DefaultTableModel orderItemsModel = Theme.tableModel("Product", "Unit price", "Qty", "Subtotal");
    private List<Order> rows = new ArrayList<>();

    public OrderPanel() {
        spnQuantity.setModel(new SpinnerNumberModel(1, 1, 999, 1));
        ((JSpinner.DefaultEditor) spnQuantity.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);

        tblItems.setModel(itemsModel);
        Theme.styleTable(tblItems);
        Theme.rightAlign(tblItems, 1, 2, 3);

        tblOrders.setModel(ordersModel);
        Theme.styleTable(tblOrders);
        Theme.columnWidths(tblOrders, 92, 95, 210, 110, 160, 105);
        Theme.rightAlign(tblOrders, 3);
        tblOrders.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        tblOrderItems.setModel(orderItemsModel);
        Theme.styleTable(tblOrderItems);
        Theme.rightAlign(tblOrderItems, 1, 2, 3);
        tblOrderItems.setRowSelectionAllowed(false);

        cmbStatusFilter.addItem("All orders");
        for (OrderStatus status : OrderStatus.values()) {
            cmbStatusFilter.addItem(status);
        }
    }

    private void createUIComponents() {
        cardOrderForm = new RoundedPanel();
        cardItems = new RoundedPanel();
        cardOrders = new RoundedPanel();
    }

    public void setClients(List<Client> clients) {
        cmbClient.removeAllItems();
        for (Client c : clients) {
            cmbClient.addItem(c);
        }
    }

    public void setProducts(List<Product> products) {
        cmbProduct.removeAllItems();
        for (Product p : products) {
            cmbProduct.addItem(p);
        }
    }

    public void showAvailable(Product p, int alreadyInOrder) {
        if (p == null) {
            lblAvailable.setText("In stock: -");
            return;
        }
        int left = p.getQuantity() - alreadyInOrder;
        lblAvailable.setText("In stock: " + left + "   |   " + Theme.money(p.getPrice()) + " each");
        lblAvailable.setForeground(left <= 0 ? Theme.DANGER : Theme.MUTED);
    }

    // the order being built on the "New order" tab
    public void showDraft(Order draft) {
        fillItems(itemsModel, draft.getItems());
        lblTotal.setText(Theme.money(draft.calculateTotal()));
    }

    public void showOrders(List<Order> orders) {
        rows = orders;
        ordersModel.setRowCount(0);
        for (Order o : orders) {
            ordersModel.addRow(new Object[]{o.getOrderNo(), o.getOrderDate(), o.getClientName(), Theme.money(o.getTotal()),
                    o.getAgentName() == null ? "-" : o.getAgentName(), o.getStatus().toString()});
        }
        showOrderItems(null);
    }

    public void showOrderItems(Order order) {
        if (order == null) {
            lblOrderItems.setText("Items in selected order");
            orderItemsModel.setRowCount(0);
        } else {
            lblOrderItems.setText("Items in " + order.getOrderNo() + "  -  deliver to " + order.getDeliveryAddress());
            fillItems(orderItemsModel, order.getItems());
        }
    }

    private void fillItems(DefaultTableModel model, List<OrderItem> items) {
        model.setRowCount(0);
        for (OrderItem item : items) {
            model.addRow(new Object[]{item.getProductName(), Theme.money(item.getUnitPrice()), item.getQuantity(),
                    Theme.money(item.getSubtotal())});
        }
    }

    public Order getSelectedOrder() {
        int row = tblOrders.getSelectedRow();
        return row >= 0 && row < rows.size() ? rows.get(row) : null;
    }

    public Client getSelectedClient() {
        return (Client) cmbClient.getSelectedItem();
    }

    public Product getSelectedProduct() {
        return (Product) cmbProduct.getSelectedItem();
    }

    public int getQuantity() {
        return (Integer) spnQuantity.getValue();
    }

    public void resetQuantity() {
        spnQuantity.setValue(1);
    }

    public JPanel getRootPanel() { return rootPanel; }
    public JTabbedPane getTabs() { return tabs; }
    public JComboBox getCmbClient() { return cmbClient; }
    public JComboBox getCmbProduct() { return cmbProduct; }
    public JTextField getTxtAddress() { return txtAddress; }
    public JTable getTblItems() { return tblItems; }
    public JTable getTblOrders() { return tblOrders; }
    public JComboBox getCmbStatusFilter() { return cmbStatusFilter; }
    public JButton getBtnAddItem() { return btnAddItem; }
    public JButton getBtnRemoveItem() { return btnRemoveItem; }
    public JButton getBtnPlaceOrder() { return btnPlaceOrder; }
    public JButton getBtnCancelOrder() { return btnCancelOrder; }

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
        label1.setText("Client Orders");
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
        label2.setText("Create orders and see what has been ordered");
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
        tabs = new JTabbedPane();
        rootPanel.add(tabs, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(16, 0));
        panel4.setBackground(new Color(-527378));
        tabs.addTab("New order", panel4);
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        cardOrderForm.setLayout(new GridBagLayout());
        cardOrderForm.setOpaque(false);
        cardOrderForm.setPreferredSize(new Dimension(300, -1));
        panel4.add(cardOrderForm, BorderLayout.WEST);
        cardOrderForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label3 = new JLabel();
        label3.setText("Order details");
        label3.setForeground(new Color(-14018019));
        Font label3Font = this.$$$getFont$$$(null, Font.BOLD, 15, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 8, 0);
        cardOrderForm.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Client");
        label4.setForeground(new Color(-7570572));
        Font label4Font = this.$$$getFont$$$(null, -1, 12, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 3, 0);
        cardOrderForm.add(label4, gbc);
        cmbClient = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardOrderForm.add(cmbClient, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Delivery address");
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
        cardOrderForm.add(label5, gbc);
        txtAddress = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardOrderForm.add(txtAddress, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Product");
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
        cardOrderForm.add(label6, gbc);
        cmbProduct = new JComboBox();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardOrderForm.add(cmbProduct, gbc);
        lblAvailable = new JLabel();
        lblAvailable.setText("In stock: -");
        lblAvailable.setForeground(new Color(-7570572));
        Font lblAvailableFont = this.$$$getFont$$$(null, -1, 12, lblAvailable.getFont());
        if (lblAvailableFont != null) lblAvailable.setFont(lblAvailableFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        cardOrderForm.add(lblAvailable, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Quantity");
        label7.setForeground(new Color(-7570572));
        Font label7Font = this.$$$getFont$$$(null, -1, 12, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 3, 0);
        cardOrderForm.add(label7, gbc);
        spnQuantity = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardOrderForm.add(spnQuantity, gbc);
        btnAddItem = new JButton();
        btnAddItem.setText("Add to order");
        btnAddItem.setBackground(new Color(-12451305));
        btnAddItem.setForeground(new Color(-1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 0, 0, 0);
        cardOrderForm.add(btnAddItem, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel5.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        cardOrderForm.add(panel5, gbc);
        cardItems.setLayout(new BorderLayout(0, 10));
        cardItems.setOpaque(false);
        panel4.add(cardItems, BorderLayout.CENTER);
        cardItems.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label8 = new JLabel();
        label8.setText("Items in this order");
        label8.setForeground(new Color(-14018019));
        Font label8Font = this.$$$getFont$$$(null, Font.BOLD, 15, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        cardItems.add(label8, BorderLayout.NORTH);
        final JScrollPane scrollPane1 = new JScrollPane();
        cardItems.add(scrollPane1, BorderLayout.CENTER);
        tblItems = new JTable();
        scrollPane1.setViewportView(tblItems);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel6.setOpaque(false);
        cardItems.add(panel6, BorderLayout.SOUTH);
        btnRemoveItem = new JButton();
        btnRemoveItem.setText("Remove item");
        btnRemoveItem.setBackground(new Color(-1054753));
        btnRemoveItem.setForeground(new Color(-14018019));
        panel6.add(btnRemoveItem, BorderLayout.WEST);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        panel7.setOpaque(false);
        panel6.add(panel7, BorderLayout.EAST);
        final JLabel label9 = new JLabel();
        label9.setText("Total");
        label9.setForeground(new Color(-7570572));
        Font label9Font = this.$$$getFont$$$(null, -1, 12, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        panel7.add(label9);
        lblTotal = new JLabel();
        lblTotal.setText("Rs. 0.00");
        lblTotal.setForeground(new Color(-14018019));
        Font lblTotalFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblTotal.getFont());
        if (lblTotalFont != null) lblTotal.setFont(lblTotalFont);
        panel7.add(lblTotal);
        btnPlaceOrder = new JButton();
        btnPlaceOrder.setText("Place order");
        btnPlaceOrder.setBackground(new Color(-12451305));
        btnPlaceOrder.setForeground(new Color(-1));
        panel7.add(btnPlaceOrder);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 12));
        panel8.setBackground(new Color(-527378));
        tabs.addTab("All orders", panel8);
        panel8.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel9.setOpaque(false);
        panel8.add(panel9, BorderLayout.NORTH);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel10.setOpaque(false);
        panel9.add(panel10, BorderLayout.WEST);
        final JLabel label10 = new JLabel();
        label10.setText("Status");
        label10.setForeground(new Color(-7570572));
        Font label10Font = this.$$$getFont$$$(null, -1, 12, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        panel10.add(label10);
        cmbStatusFilter = new JComboBox();
        panel10.add(cmbStatusFilter);
        btnCancelOrder = new JButton();
        btnCancelOrder.setText("Cancel selected order");
        btnCancelOrder.setBackground(new Color(-6078161));
        btnCancelOrder.setForeground(new Color(-1));
        panel9.add(btnCancelOrder, BorderLayout.EAST);
        cardOrders.setLayout(new BorderLayout(0, 12));
        cardOrders.setOpaque(false);
        panel8.add(cardOrders, BorderLayout.CENTER);
        cardOrders.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane2 = new JScrollPane();
        cardOrders.add(scrollPane2, BorderLayout.CENTER);
        tblOrders = new JTable();
        scrollPane2.setViewportView(tblOrders);
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new BorderLayout(0, 6));
        panel11.setOpaque(false);
        cardOrders.add(panel11, BorderLayout.SOUTH);
        lblOrderItems = new JLabel();
        lblOrderItems.setText("Items in selected order");
        lblOrderItems.setForeground(new Color(-14018019));
        Font lblOrderItemsFont = this.$$$getFont$$$(null, Font.BOLD, 13, lblOrderItems.getFont());
        if (lblOrderItemsFont != null) lblOrderItems.setFont(lblOrderItemsFont);
        panel11.add(lblOrderItems, BorderLayout.NORTH);
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setPreferredSize(new Dimension(-1, 140));
        panel11.add(scrollPane3, BorderLayout.CENTER);
        tblOrderItems = new JTable();
        scrollPane3.setViewportView(tblOrderItems);
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
