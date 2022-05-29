package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:55
 */
public class Parent implements Value {
    public static final String staticPublic = "staticPublic";
    private static final String staticPrivate = "staticPrivate";
    private static final String staticGet = "staticGet";
    public static String staticSet = "staticSet";
    public String sex = "man";

    @QLAlias("生日")
    public String birth = "2022-01-01";
    private int age;
    private String name = "example";

    public static String getStaticGet() {
        return staticGet;
    }

    public static String getStaticGetParam(Integer a) {
        return staticGet;
    }

    public static String findStatic() {
        return "static";
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getWork() {
        return "parent";
    }

    private String getMethod1(int a, int b) {
        return "a";
    }

    @Override
    public Object get() {
        return this;
    }
}
