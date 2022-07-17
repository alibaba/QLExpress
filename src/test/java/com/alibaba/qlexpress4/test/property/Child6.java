package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午6:53
 */
public class Child6 extends Parent implements Value {
    private final long result;
    public Child6(){
        this.result = 0L;
    }
    public Child6(double t){
        this.result = 0L;
    }
    public Child6(BigInteger bigInteger){
        this.result = 0L;
    }


    public int getMethod9(BigInteger t){
        return t.intValue();
    }

    public BigDecimal getMethod10(double t){
        return new BigDecimal(String.valueOf(t));
    }

}
