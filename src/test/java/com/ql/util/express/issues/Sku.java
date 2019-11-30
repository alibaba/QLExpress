package com.ql.util.express.issues;

public class Sku {
    int categoryId;
    int price;

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
}
