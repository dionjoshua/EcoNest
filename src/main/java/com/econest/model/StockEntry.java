package com.econest.model;

import java.time.LocalDate;

// One stock-in delivery received from a supplier
public class StockEntry {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private String supplier;
    private LocalDate receivedDate;
    private String note;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
