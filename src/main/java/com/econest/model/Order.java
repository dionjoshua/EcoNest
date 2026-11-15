package com.econest.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int clientId;
    private String clientName;
    private LocalDate orderDate;
    private OrderStatus status = OrderStatus.PENDING;
    private String deliveryAddress;
    private double total;

    // delivery details, empty until an agent is assigned
    private Integer agentId;
    private String agentName;
    private LocalDate deliveryDate;

    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        // same product twice -> just increase the quantity
        for (OrderItem existing : items) {
            if (existing.getProductId() == item.getProductId()) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public double calculateTotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    public String getOrderNo() {
        return String.format("ORD-%04d", id);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Integer getAgentId() { return agentId; }
    public void setAgentId(Integer agentId) { this.agentId = agentId; }

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
