package com.alibaba.qlexpress4.test.property;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Author: TaoKan
 */
public class Child6 extends Parent {
    private final long result;
    
    public Child6() {
        this.result = 0L;
    }
    
    public Child6(double t) {
        this.result = 0L;
    }
    
    public Child6(BigInteger bigInteger) {
        this.result = 0L;
    }
    
    public int getMethod9(BigInteger t) {
        return t.intValue();
    }
    
    public BigDecimal getMethod10(double t) {
        return new BigDecimal(String.valueOf(t));
    }
    
}
