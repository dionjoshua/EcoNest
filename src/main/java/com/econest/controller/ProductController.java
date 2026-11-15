package com.econest.controller;

import com.econest.dao.ProductDAO;
import com.econest.model.Product;
import com.econest.util.Messages;
import com.econest.util.SearchListener;
import com.econest.util.ValidationException;
import com.econest.util.Validator;
import com.econest.view.ProductPanel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Requirement 1: manage the product catalogue
public class ProductController implements ScreenController {
    private final ProductPanel view;
    private final ProductDAO productDAO;
    private List<Product> allProducts = new ArrayList<>();

    public ProductController(ProductPanel view, ProductDAO productDAO) {
        this.view = view;
        this.productDAO = productDAO;

        view.getBtnAdd().addActionListener(e -> addProduct());
        view.getBtnUpdate().addActionListener(e -> updateProduct());
        view.getBtnRemove().addActionListener(e -> removeProduct());
        view.getBtnClear().addActionListener(e -> view.clearForm());
        view.getTxtSearch().getDocument().addDocumentListener(new SearchListener(this::applyFilter));
        view.getCmbFilter().addActionListener(e -> applyFilter());
        view.getTblProducts().getSelectionModel().addListSelectionListener(e -> {
            Product selected = view.getSelectedProduct();
            if (!e.getValueIsAdjusting() && selected != null) {
                view.fillForm(selected);
            }
        });
    }

    @Override
    public void refresh() {
        try {
            allProducts = productDAO.findAll();
            applyFilter();
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the products.", e);
        }
    }

    // search box + category drop down
    private void applyFilter() {
        String search = view.getTxtSearch().getText().trim().toLowerCase();
        int categoryIndex = view.getCmbFilter().getSelectedIndex();   // 0 = all
        List<Product> shown = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchesText = p.getName().toLowerCase().contains(search);
            boolean matchesCategory = categoryIndex == 0 || p.getCategory().ordinal() == categoryIndex - 1;
            if (matchesText && matchesCategory) {
                shown.add(p);
            }
        }
        view.showProducts(shown);
    }

    private void addProduct() {
        try {
            Product product = readForm();
            product.setQuantity(Validator.wholeNumber(view.getTxtQuantity().getText(), "Opening quantity", 0));
            productDAO.add(product);
            view.clearForm();
            refresh();
            Messages.info(view.getRootPanel(), product.getName() + " was added to the catalogue.");
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not save the product.", e);
        }
    }

    private void updateProduct() {
        Product selected = view.getSelectedProduct();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select a product from the table first.");
            return;
        }
        try {
            Product product = readForm();
            product.setId(selected.getId());
            productDAO.update(product);
            view.clearForm();
            refresh();
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not update the product.", e);
        }
    }

    private void removeProduct() {
        Product selected = view.getSelectedProduct();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select a product from the table first.");
            return;
        }
        if (!Messages.confirm(view.getRootPanel(), "Remove " + selected.getName() + " from the catalogue?")) {
            return;
        }
        try {
            productDAO.delete(selected.getId());
            view.clearForm();
            refresh();
        } catch (SQLException e) {
            // the database refuses to delete a product that is part of an order
            Messages.warn(view.getRootPanel(), selected.getName() + " is on existing orders, so it cannot be removed.");
        }
    }

    // Builds a Plant, Planter or GardeningKit from what was typed
    private Product readForm() throws ValidationException {
        Product product = Product.create(view.getCategory());
        product.setName(Validator.required(view.getTxtName().getText(), "Product name"));
        product.setPrice(Validator.positiveNumber(view.getTxtPrice().getText(), "Price"));
        product.setEcoRating(view.getEcoRating());
        product.setSpec(view.getTxtSpec().getText().trim());
        product.setDescription(view.getTxtDescription().getText().trim());
        product.setReorderLevel(Validator.wholeNumber(view.getTxtReorderLevel().getText(), "Reorder level", 0));
        return product;
    }
}
