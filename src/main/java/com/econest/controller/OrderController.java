package com.econest.controller;

import com.econest.dao.ClientDAO;
import com.econest.dao.OrderDAO;
import com.econest.dao.ProductDAO;
import com.econest.model.Client;
import com.econest.model.Order;
import com.econest.model.OrderItem;
import com.econest.model.OrderStatus;
import com.econest.model.Product;
import com.econest.util.Messages;
import com.econest.util.ValidationException;
import com.econest.util.Validator;
import com.econest.view.OrderPanel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Requirement 5: create orders, add products to them and work out the total
public class OrderController implements ScreenController {
    private final OrderPanel view;
    private final ClientDAO clientDAO;
    private final ProductDAO productDAO;
    private final OrderDAO orderDAO;

    private Order draft = new Order();   // the order being put together on the "New order" tab
    private boolean loading;             // true while the combo boxes are being refilled

    public OrderController(OrderPanel view, ClientDAO clientDAO, ProductDAO productDAO, OrderDAO orderDAO) {
        this.view = view;
        this.clientDAO = clientDAO;
        this.productDAO = productDAO;
        this.orderDAO = orderDAO;

        view.getCmbClient().addActionListener(e -> clientChanged());
        view.getCmbProduct().addActionListener(e -> productChanged());
        view.getBtnAddItem().addActionListener(e -> addItem());
        view.getBtnRemoveItem().addActionListener(e -> removeItem());
        view.getBtnPlaceOrder().addActionListener(e -> placeOrder());
        view.getBtnCancelOrder().addActionListener(e -> cancelOrder());
        view.getCmbStatusFilter().addActionListener(e -> loadOrders());
        view.getTblOrders().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedOrder();
            }
        });
    }

    @Override
    public void refresh() {
        try {
            // remember what was chosen so reloading does not reset the form
            Client client = view.getSelectedClient();
            Product product = view.getSelectedProduct();
            String address = view.getTxtAddress().getText();

            loading = true;
            view.setClients(clientDAO.findAll());
            view.setProducts(productDAO.findAll());
            select(view.getCmbClient(), client == null ? -1 : client.getId());
            select(view.getCmbProduct(), product == null ? -1 : product.getId());
            loading = false;

            if (client == null) {
                clientChanged();
            } else {
                view.getTxtAddress().setText(address);
            }
            productChanged();
            view.showDraft(draft);
            loadOrders();
        } catch (SQLException e) {
            loading = false;
            Messages.error(view.getRootPanel(), "Could not load the orders.", e);
        }
    }

    private void select(javax.swing.JComboBox combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            boolean match = item instanceof Client ? ((Client) item).getId() == id : ((Product) item).getId() == id;
            if (match) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void loadOrders() {
        try {
            Object filter = view.getCmbStatusFilter().getSelectedItem();
            List<Order> shown = new ArrayList<>();
            for (Order order : orderDAO.findAll()) {
                if (!(filter instanceof OrderStatus) || order.getStatus() == filter) {
                    shown.add(order);
                }
            }
            view.showOrders(shown);
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the orders.", e);
        }
    }

    // delivery address starts as the client's own address but can be changed
    private void clientChanged() {
        Client client = view.getSelectedClient();
        if (!loading && client != null) {
            view.getTxtAddress().setText(client.getAddress());
        }
    }

    private void productChanged() {
        if (!loading) {
            Product product = view.getSelectedProduct();
            view.showAvailable(product, product == null ? 0 : quantityInDraft(product));
        }
    }

    private int quantityInDraft(Product product) {
        for (OrderItem item : draft.getItems()) {
            if (item.getProductId() == product.getId()) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    private void addItem() {
        Product product = view.getSelectedProduct();
        if (product == null) {
            Messages.warn(view.getRootPanel(), "Choose a product first.");
            return;
        }
        int quantity = view.getQuantity();
        int left = product.getQuantity() - quantityInDraft(product);
        if (quantity > left) {
            Messages.warn(view.getRootPanel(), left <= 0
                    ? product.getName() + " is out of stock."
                    : "Only " + left + " of " + product.getName() + " left in stock.");
            return;
        }
        draft.addItem(new OrderItem(product, quantity));
        view.showDraft(draft);
        view.resetQuantity();
        productChanged();
    }

    private void removeItem() {
        int row = view.getTblItems().getSelectedRow();
        if (row < 0) {
            Messages.warn(view.getRootPanel(), "Select the item to remove from the list.");
            return;
        }
        draft.getItems().remove(row);
        view.showDraft(draft);
        productChanged();
    }

    private void placeOrder() {
        Client client = view.getSelectedClient();
        if (client == null) {
            Messages.warn(view.getRootPanel(), "Choose a client first.");
            return;
        }
        if (draft.getItems().isEmpty()) {
            Messages.warn(view.getRootPanel(), "Add at least one product to the order.");
            return;
        }
        try {
            draft.setClientId(client.getId());
            draft.setDeliveryAddress(Validator.required(view.getTxtAddress().getText(), "Delivery address"));
            draft.setOrderDate(LocalDate.now());
            draft.setStatus(OrderStatus.PENDING);
            orderDAO.create(draft);   // also takes the items out of stock

            Messages.info(view.getRootPanel(), "Order " + draft.getOrderNo() + " saved for " + client.getName()
                    + ".\nTotal: " + String.format("Rs. %,.2f", draft.getTotal())
                    + "\n\nGo to Deliveries to assign a delivery agent.");
            draft = new Order();
            refresh();
        } catch (ValidationException e) {
            Messages.warn(view.getRootPanel(), e.getMessage());
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "The order was not saved.", e);
            refresh();
        }
    }

    private void showSelectedOrder() {
        Order selected = view.getSelectedOrder();
        try {
            view.showOrderItems(selected == null ? null : orderDAO.findById(selected.getId()));
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not load the order.", e);
        }
    }

    private void cancelOrder() {
        Order selected = view.getSelectedOrder();
        if (selected == null) {
            Messages.warn(view.getRootPanel(), "Select an order from the table first.");
            return;
        }
        if (selected.getStatus() != OrderStatus.PENDING && selected.getStatus() != OrderStatus.ASSIGNED) {
            Messages.warn(view.getRootPanel(), "Only orders that have not been dispatched yet can be cancelled.");
            return;
        }
        if (!Messages.confirm(view.getRootPanel(), "Cancel " + selected.getOrderNo() + "? The items go back into stock.")) {
            return;
        }
        try {
            orderDAO.cancel(selected.getId());
            refresh();
        } catch (SQLException e) {
            Messages.error(view.getRootPanel(), "Could not cancel the order.", e);
        }
    }
}
