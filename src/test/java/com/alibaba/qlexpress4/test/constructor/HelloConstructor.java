package com.alibaba.qlexpress4.test.constructor;

import com.alibaba.qlexpress4.runtime.QLambda;

/**
 * Author: DQinYuan
 */
public class HelloConstructor {

    public int flag;

    public HelloConstructor(HelloParent helloParent) {
        this.flag = 0;
    }

    public HelloConstructor(HelloChild child) {
        this.flag = 1;
    }

    public HelloConstructor(String ... s) {
        this.flag = 2;
    }

    public HelloConstructor(String s) {
        this.flag = 3;
    }

    public HelloConstructor(HelloChild child, Runnable r) {
        this.flag = 4;
    }

    public HelloConstructor(HelloParent helloParent, QLambda q) {
        this.flag = 5;
    }
}
