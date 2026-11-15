package com.econest.model;

public enum ProductCategory {
    PLANT("Plant"),
    PLANTER("Planter"),
    KIT("Gardening Kit");

    private final String label;

    ProductCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
