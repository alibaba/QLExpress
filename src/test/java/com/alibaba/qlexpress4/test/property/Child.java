package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:55
 */
public class Child extends Parent implements Value {
    public String work = "childWork";
    private Boolean booValue = false;

    public static String getStaticGetParam(Integer a, Integer b) {
        return "5";
    }

    public String getWork() {
        return "child";
    }

    public Boolean getBooValue() {
        return true;
    }

    public void setBooValue(Boolean booValue) {
        this.booValue = booValue;
    }

    private String getMethod1(int a, int b) {
        return "c";
    }

    @Override
    public Object get() {
        return this;
    }
}
