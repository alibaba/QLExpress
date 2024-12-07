package com.alibaba.qlexpress4.test.qlalias;

import com.alibaba.qlexpress4.annotation.QLAlias;

@QLAlias("订单")
public class Order {

    @QLAlias("订单号")
    private String orderNum;

    @QLAlias("金额")
    private int amount;

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
