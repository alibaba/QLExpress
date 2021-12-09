package com.ql.util.express.test;

public class ObjectBean {
    int amount;
    int volume;

    public ObjectBean(int aAmount, int aVolume) {
        this.amount = aAmount;
        this.volume = aVolume;
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
