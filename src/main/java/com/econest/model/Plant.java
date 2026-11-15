package com.econest.model;

public class Plant extends Product {

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.PLANT;
    }

    @Override
    public String getSpecLabel() {
        return "Light needs";
    }
}
