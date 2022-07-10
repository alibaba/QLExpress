package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/7/10 下午6:53
 */
public class Child3 extends Parent implements Value {
    private final long result;
    public Child3(){
        this.result = 0L;
    }
    public Child3(Parent t){
        this.result = 0L;
    }

    public int getMethod5(Parent t){
        return t.getAge();
    }

}
