package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午6:53
 */
public class Child2 extends Parent implements Value {
    private final long result;
    public Child2(){
        this.result = 0L;
    }
    public Child2(Boolean t){
        this.result = 0L;
    }

    public Child2(Object a, Boolean b){
        this.result = 1;
    }

    public boolean getMethod3(boolean t){
        return t;
    }

    public int getMethod4(Object s,Boolean t){
        return 2;
    }
}
