package com.alibaba.qlexpress4.test.method;

/**
 * Author: DQinYuan
 */
public class TestChild extends TestParent implements InterWithDefault {

    public int get10() {
        return 10;
    }

    public int get10(String ... s) {
        return 11;
    }
}
