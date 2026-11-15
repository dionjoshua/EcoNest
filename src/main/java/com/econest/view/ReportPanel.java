package com.econest.view;

import com.econest.model.Order;
import com.econest.model.Product;
import java.time.LocalDate;
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

public class ReportPanel {
    private JPanel rootPanel;
    private JComboBox cmbMonth;
    private JComboBox cmbYear;
    private JButton btnGenerate;
    private JButton btnExport;
    private JTabbedPane tabs;
    private JPanel cardSalesRevenue;
    private JLabel lblSalesRevenue;
    private JPanel cardSalesOrders;
    private JLabel lblSalesOrders;
    private JPanel cardSalesAverage;
    private JLabel lblSalesAverage;
    private JPanel cardProductSales;
    private JTable tblProductSales;
    private JPanel cardMonthOrders;
    private JTable tblMonthOrders;
    private JPanel cardInvItems;
    private JLabel lblInvItems;
    private JPanel cardInvValue;
    private JLabel lblInvValue;
    private JPanel cardInvLow;
    private JLabel lblInvLow;
    private JPanel cardInventory;
    private JTable tblInventory;

    private final DefaultTableModel productSalesModel = Theme.tableModel("Product", "Category", "Units sold", "Revenue");
    private final DefaultTableModel monthOrdersModel = Theme.tableModel("Order no", "Date", "Client", "Total", "Status");
    private final DefaultTableModel inventoryModel = Theme.tableModel("Product", "Category", "On hand", "Reorder level", "Stock value", "Status");

    public ReportPanel() {
        tblProductSales.setModel(productSalesModel);
        Theme.styleTable(tblProductSales);
        Theme.rightAlign(tblProductSales, 2, 3);

        tblMonthOrders.setModel(monthOrdersModel);
        Theme.styleTable(tblMonthOrders);
        Theme.rightAlign(tblMonthOrders, 3);
        tblMonthOrders.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        tblInventory.setModel(inventoryModel);
        Theme.styleTable(tblInventory);
        Theme.rightAlign(tblInventory, 4);
        tblInventory.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        // this year and the two before it
        int year = LocalDate.now().getYear();
        for (int y = year; y >= year - 2; y--) {
            cmbYear.addItem(y);
        }
        cmbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
    }

    private void createUIComponents() {
        cardSalesRevenue = new RoundedPanel();
        cardSalesOrders = new RoundedPanel();
        cardSalesAverage = new RoundedPanel();
        cardProductSales = new RoundedPanel();
        cardMonthOrders = new RoundedPanel();
        cardInvItems = new RoundedPanel();
        cardInvValue = new RoundedPanel();
        cardInvLow = new RoundedPanel();
        cardInventory = new RoundedPanel();
    }

    public void showSalesSummary(double revenue, int orders) {
        lblSalesRevenue.setText(Theme.money(revenue));
        lblSalesOrders.setText(String.valueOf(orders));
        lblSalesAverage.setText(orders == 0 ? "-" : Theme.money(revenue / orders));
    }

    // each row: product name, category, units, revenue
    public void showProductSales(List<Object[]> sales) {
        productSalesModel.setRowCount(0);
        for (Object[] row : sales) {
            productSalesModel.addRow(new Object[]{row[0], row[1], row[2], Theme.money((Double) row[3])});
        }
    }

    public void showMonthOrders(List<Order> orders) {
        monthOrdersModel.setRowCount(0);
        for (Order o : orders) {
            monthOrdersModel.addRow(new Object[]{o.getOrderNo(), o.getOrderDate(), o.getClientName(),
                    Theme.money(o.getTotal()), o.getStatus().toString()});
        }
    }

    public void showInventory(List<Product> products) {
        inventoryModel.setRowCount(0);
        double value = 0;
        int low = 0;
        for (Product p : products) {
            double stockValue = p.getQuantity() * p.getPrice();
            value += stockValue;
            if (p.isLowStock()) {
                low++;
            }
            inventoryModel.addRow(new Object[]{p.getName(), p.getCategory(), p.getQuantity(), p.getReorderLevel(),
                    Theme.money(stockValue), p.getStockStatus()});
        }
        lblInvItems.setText(String.valueOf(products.size()));
        lblInvValue.setText(Theme.money(value));
        lblInvLow.setText(String.valueOf(low));
        lblInvLow.setForeground(low > 0 ? Theme.DANGER : Theme.INK);
    }

    public int getSelectedMonth() {
        return cmbMonth.getSelectedIndex() + 1;
    }

    public int getSelectedYear() {
        return (Integer) cmbYear.getSelectedItem();
    }

    public JPanel getRootPanel() { return rootPanel; }
    public JButton getBtnGenerate() { return btnGenerate; }
    public JButton getBtnExport() { return btnExport; }

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
        label1.setText("Reports");
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
        label2.setText("Monthly sales and inventory");
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
        cmbMonth = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("January");
        defaultComboBoxModel1.addElement("February");
        defaultComboBoxModel1.addElement("March");
        defaultComboBoxModel1.addElement("April");
        defaultComboBoxModel1.addElement("May");
        defaultComboBoxModel1.addElement("June");
        defaultComboBoxModel1.addElement("July");
        defaultComboBoxModel1.addElement("August");
        defaultComboBoxModel1.addElement("September");
        defaultComboBoxModel1.addElement("October");
        defaultComboBoxModel1.addElement("November");
        defaultComboBoxModel1.addElement("December");
        cmbMonth.setModel(defaultComboBoxModel1);
        panel3.add(cmbMonth);
        cmbYear = new JComboBox();
        panel3.add(cmbYear);
        btnGenerate = new JButton();
        btnGenerate.setText("Generate");
        btnGenerate.setBackground(new Color(-12451305));
        btnGenerate.setForeground(new Color(-1));
        panel3.add(btnGenerate);
        btnExport = new JButton();
        btnExport.setText("Export to file");
        btnExport.setBackground(new Color(-1054753));
        btnExport.setForeground(new Color(-14018019));
        panel3.add(btnExport);
        tabs = new JTabbedPane();
        rootPanel.add(tabs, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        panel4.setBackground(new Color(-527378));
        tabs.addTab("Monthly sales", panel4);
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        cardSalesRevenue.setLayout(new GridBagLayout());
        cardSalesRevenue.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 12, 7);
        panel4.add(cardSalesRevenue, gbc);
        cardSalesRevenue.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label3 = new JLabel();
        label3.setText("Revenue");
        label3.setForeground(new Color(-7570572));
        Font label3Font = this.$$$getFont$$$(null, -1, 12, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardSalesRevenue.add(label3, gbc);
        lblSalesRevenue = new JLabel();
        lblSalesRevenue.setText("-");
        lblSalesRevenue.setForeground(new Color(-14018019));
        Font lblSalesRevenueFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblSalesRevenue.getFont());
        if (lblSalesRevenueFont != null) lblSalesRevenue.setFont(lblSalesRevenueFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardSalesRevenue.add(lblSalesRevenue, gbc);
        cardSalesOrders.setLayout(new GridBagLayout());
        cardSalesOrders.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 7, 12, 7);
        panel4.add(cardSalesOrders, gbc);
        cardSalesOrders.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label4 = new JLabel();
        label4.setText("Orders");
        label4.setForeground(new Color(-7570572));
        Font label4Font = this.$$$getFont$$$(null, -1, 12, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardSalesOrders.add(label4, gbc);
        lblSalesOrders = new JLabel();
        lblSalesOrders.setText("-");
        lblSalesOrders.setForeground(new Color(-14018019));
        Font lblSalesOrdersFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblSalesOrders.getFont());
        if (lblSalesOrdersFont != null) lblSalesOrders.setFont(lblSalesOrdersFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardSalesOrders.add(lblSalesOrders, gbc);
        cardSalesAverage.setLayout(new GridBagLayout());
        cardSalesAverage.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 7, 12, 0);
        panel4.add(cardSalesAverage, gbc);
        cardSalesAverage.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label5 = new JLabel();
        label5.setText("Average order value");
        label5.setForeground(new Color(-7570572));
        Font label5Font = this.$$$getFont$$$(null, -1, 12, label5.getFont());
        if (label5Font != null) label5.setFont(label5Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardSalesAverage.add(label5, gbc);
        lblSalesAverage = new JLabel();
        lblSalesAverage.setText("-");
        lblSalesAverage.setForeground(new Color(-14018019));
        Font lblSalesAverageFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblSalesAverage.getFont());
        if (lblSalesAverageFont != null) lblSalesAverage.setFont(lblSalesAverageFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardSalesAverage.add(lblSalesAverage, gbc);
        cardProductSales.setLayout(new BorderLayout(0, 8));
        cardProductSales.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel4.add(cardProductSales, gbc);
        cardProductSales.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label6 = new JLabel();
        label6.setText("Sales by product");
        label6.setForeground(new Color(-14018019));
        Font label6Font = this.$$$getFont$$$(null, Font.BOLD, 13, label6.getFont());
        if (label6Font != null) label6.setFont(label6Font);
        cardProductSales.add(label6, BorderLayout.NORTH);
        final JScrollPane scrollPane1 = new JScrollPane();
        cardProductSales.add(scrollPane1, BorderLayout.CENTER);
        tblProductSales = new JTable();
        scrollPane1.setViewportView(tblProductSales);
        cardMonthOrders.setLayout(new BorderLayout(0, 8));
        cardMonthOrders.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel4.add(cardMonthOrders, gbc);
        cardMonthOrders.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label7 = new JLabel();
        label7.setText("Orders in this month");
        label7.setForeground(new Color(-14018019));
        Font label7Font = this.$$$getFont$$$(null, Font.BOLD, 13, label7.getFont());
        if (label7Font != null) label7.setFont(label7Font);
        cardMonthOrders.add(label7, BorderLayout.NORTH);
        final JScrollPane scrollPane2 = new JScrollPane();
        cardMonthOrders.add(scrollPane2, BorderLayout.CENTER);
        tblMonthOrders = new JTable();
        scrollPane2.setViewportView(tblMonthOrders);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        panel5.setBackground(new Color(-527378));
        tabs.addTab("Inventory", panel5);
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        cardInvItems.setLayout(new GridBagLayout());
        cardInvItems.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 12, 7);
        panel5.add(cardInvItems, gbc);
        cardInvItems.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label8 = new JLabel();
        label8.setText("Products in catalogue");
        label8.setForeground(new Color(-7570572));
        Font label8Font = this.$$$getFont$$$(null, -1, 12, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardInvItems.add(label8, gbc);
        lblInvItems = new JLabel();
        lblInvItems.setText("-");
        lblInvItems.setForeground(new Color(-14018019));
        Font lblInvItemsFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblInvItems.getFont());
        if (lblInvItemsFont != null) lblInvItems.setFont(lblInvItemsFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardInvItems.add(lblInvItems, gbc);
        cardInvValue.setLayout(new GridBagLayout());
        cardInvValue.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 7, 12, 7);
        panel5.add(cardInvValue, gbc);
        cardInvValue.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label9 = new JLabel();
        label9.setText("Stock value");
        label9.setForeground(new Color(-7570572));
        Font label9Font = this.$$$getFont$$$(null, -1, 12, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardInvValue.add(label9, gbc);
        lblInvValue = new JLabel();
        lblInvValue.setText("-");
        lblInvValue.setForeground(new Color(-14018019));
        Font lblInvValueFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblInvValue.getFont());
        if (lblInvValueFont != null) lblInvValue.setFont(lblInvValueFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardInvValue.add(lblInvValue, gbc);
        cardInvLow.setLayout(new GridBagLayout());
        cardInvLow.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 7, 12, 0);
        panel5.add(cardInvLow, gbc);
        cardInvLow.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label10 = new JLabel();
        label10.setText("Low stock alerts");
        label10.setForeground(new Color(-7570572));
        Font label10Font = this.$$$getFont$$$(null, -1, 12, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cardInvLow.add(label10, gbc);
        lblInvLow = new JLabel();
        lblInvLow.setText("-");
        lblInvLow.setForeground(new Color(-14018019));
        Font lblInvLowFont = this.$$$getFont$$$(null, Font.BOLD, 20, lblInvLow.getFont());
        if (lblInvLowFont != null) lblInvLow.setFont(lblInvLowFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        cardInvLow.add(lblInvLow, gbc);
        cardInventory.setLayout(new BorderLayout(0, 8));
        cardInventory.setOpaque(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(cardInventory, gbc);
        cardInventory.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label11 = new JLabel();
        label11.setText("Inventory status");
        label11.setForeground(new Color(-14018019));
        Font label11Font = this.$$$getFont$$$(null, Font.BOLD, 13, label11.getFont());
        if (label11Font != null) label11.setFont(label11Font);
        cardInventory.add(label11, BorderLayout.NORTH);
        final JScrollPane scrollPane3 = new JScrollPane();
        cardInventory.add(scrollPane3, BorderLayout.CENTER);
        tblInventory = new JTable();
        scrollPane3.setViewportView(tblInventory);
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
