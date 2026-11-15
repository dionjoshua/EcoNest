package com.econest.model;

// Base class for everything EcoNest sells.
// Plant, Planter and GardeningKit extend this.
public abstract class Product {
    private int id;
    private String name;
    private double price;
    private int ecoRating;      // 1 (lowest) to 5 (most sustainable)
    private String description;
    private String spec;        // meaning depends on the type, see getSpecLabel()
    private int quantity;       // quantity on hand
    private int reorderLevel;

    public abstract ProductCategory getCategory();

    // label shown next to the "spec" field in the UI
    public abstract String getSpecLabel();

    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }

    public String getStockStatus() {
        if (quantity == 0) return "Out of stock";
        if (isLowStock()) return "Low stock";
        return "In stock";
    }

    // creates the right subclass for a category
    public static Product create(ProductCategory category) {
        switch (category) {
            case PLANT:   return new Plant();
            case PLANTER: return new Planter();
            default:      return new GardeningKit();
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getEcoRating() { return ecoRating; }
    public void setEcoRating(int ecoRating) { this.ecoRating = ecoRating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    // used by combo boxes
    @Override
    public String toString() {
        return name;
    }
}
