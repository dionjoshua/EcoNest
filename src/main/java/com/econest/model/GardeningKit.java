package com.econest.model;

public class GardeningKit extends Product {

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.KIT;
    }

    @Override
    public String getSpecLabel() {
        return "Kit contents";
    }
}
