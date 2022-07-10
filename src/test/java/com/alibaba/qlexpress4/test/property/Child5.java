package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午6:53
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
