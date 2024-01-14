package com.alibaba.qlexpress4.test.property;

import java.math.BigDecimal;

/**
 * Author: TaoKan
 */
public class NumberConstructor {

    private int flag;

    public NumberConstructor(double a){
        this.flag = 0;
    }

    public NumberConstructor(Number a){
        this.flag = 1;
    }

    public NumberConstructor(BigDecimal a){
        this.flag = 2;
    }

    public NumberConstructor(String a){
        this.flag = 3;
    }

    public int getFlag() {
        return flag;
    }
}
