package com.ql.util.express.test;

public class ObjectBean {
    int amount;
    int volume;

    public ObjectBean(int amount, int volume) {
        this.amount = amount;
        this.volume = volume;
    }

    public int getAmount() {
        return amount;
    }

    public int getAmount(int a) {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
