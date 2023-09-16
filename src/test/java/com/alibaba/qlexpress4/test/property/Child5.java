package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * Author: TaoKan
 */
public class Child5 extends Parent implements Value {
    private final long result;
    public Child5(){
        this.result = 0L;
    }
    public Child5(double t){
        this.result = 0L;
    }


    public double getMethod8(double t){
        return t;
    }


}
