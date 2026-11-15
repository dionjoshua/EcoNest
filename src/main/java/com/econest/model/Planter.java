package com.econest.model;

public class Planter extends Product {

    @Override
    public ProductCategory getCategory() {
        return ProductCategory.PLANTER;
    }

    @Override
    public String getSpecLabel() {
        return "Material";
    }
}
