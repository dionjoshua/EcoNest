package com.econest.controller;

import com.econest.dao.ProductDAO;
import com.econest.dao.StockDAO;
import com.econest.model.Product;
import com.econest.model.StockEntry;
import com.econest.util.Messages;
import com.econest.util.ValidationException;
import com.econest.util.Validator;
import com.econest.view.InventoryPanel;

import java.sql.SQLException;
import java.time.LocalDate;

// Requirement 3: quantity on hand, reorder levels and stock-in from suppliers
public class InventoryController implements ScreenController {
    private final InventoryPanel view;
    private final ProductDAO productDAO;
    private final StockDAO stockDAO;

    public InventoryController(InventoryPanel view, ProductDAO productDAO, StockDAO stockDAO) {
        this.view = view;
        this.productDAO = productDAO;
        this.stockDAO = stockDAO;

        view.getBtnReceive().addActionListener(e -> receiveStock());
        view.getBtnSaveReorder().addActionListener(e -> saveReorderLevel());
        view.getChkLowOnly().addActionListener(e -> refresh());
        view.getTblStock().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                view.showSelected(view.getSelectedProduct());
            }
        });
    }

    @Override
    public void refresh() {
        try {
            Product selected = view.getSelectedProduct();
            if (view.getChkLowOnly().isSelected()) {
                view.showStock(productDAO.findLowStock());
            } else {
                view.showStock(productDAO.findAll());
            }
            view.showHistory(stockDAO.findRecent(30));
            if (selected != null) {
                view.selectProduct(selected.getId());
            }
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the stock.", e);
        }
    }

    private void receiveStock() {
        Product product = view.getSelectedProduct();
        if (product == null) {
            Messages.warn(view.getRootPanel(), "Select a product from the table first.");
            return;
        }
        try {
            StockEntry entry = new StockEntry();
            entry.setProductId(product.getId());
            entry.setQuantity(Validator.wholeNumber(view.getTxtStockQty().getText(), "Quantity received", 1));
            entry.setSupplier(Validator.required(view.getTxtSupplier().getText(), "Supplier"));
            entry.setNote(view.getTxtNote().getText().trim());
            entry.setReceivedDate(LocalDate.now());
            stockDAO.receiveStock(entry);
            refresh();
            Messages.info(view.getRootPanel(), entry.getQuantity() + " x " + product.getName() + " added to stock.");
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not save the stock-in.", e);
        }
    }

    private void saveReorderLevel() {
        Product product = view.getSelectedProduct();
        if (product == null) {
            Messages.warn(view.getRootPanel(), "Select a product from the table first.");
            return;
        }
        try {
            int level = Validator.wholeNumber(view.getTxtReorder().getText(), "Reorder level", 0);
            productDAO.updateReorderLevel(product.getId(), level);
            refresh();
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not save the reorder level.", e);
        }
    }
}
