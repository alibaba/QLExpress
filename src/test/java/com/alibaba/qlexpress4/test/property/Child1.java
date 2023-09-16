package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * Author: TaoKan
 */
public class Child1 extends Parent implements Value {
    private final long result;
    public Child1(){
        this.result = 0L;
    }
    public Child1(boolean t){
        this.result = 0L;
    }

    public Child1(long a, int b){
        this.result = a+b+1;
    }

    public boolean getMethod3(boolean t){
        return t;
    }

    public int getMethod4(Object s,boolean t){
        return 2;
    }
}
