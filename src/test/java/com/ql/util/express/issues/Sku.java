package com.ql.util.express.issues;

import java.util.HashMap;
import java.util.Map;

public class Sku {
    int categoryId;
    int price;
    Map<String,Object> extProperties = new HashMap();

    public Sku(int categoryId, int price) {
        this.categoryId = categoryId;
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Map<String, Object> getExtProperties() {
        return extProperties;
    }

    public void setExtProperties(Map<String, Object> extProperties) {
        this.extProperties = extProperties;
    }
}
