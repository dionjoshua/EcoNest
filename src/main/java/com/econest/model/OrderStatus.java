package com.econest.model;

public enum OrderStatus {
    PENDING("Pending"),
    ASSIGNED("Assigned"),
    DISPATCHED("Dispatched"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
