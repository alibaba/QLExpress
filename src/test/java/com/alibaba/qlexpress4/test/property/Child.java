package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * Author: TaoKan
 */
public class Child extends Parent implements Value {
    public String work = "childWork";
    
    public static String staticWork = "childStaticWork";
    
    private Boolean booValue = false;
    
    private int age = 11;
    
    private String birth = "2021-02-02";
    
    private final long result;
    
    public Child() {
        this.result = 0L;
    }
    
    public Child(boolean a, int b) {
        this.result = 1;
    }
    
    public static String getStaticGet() {
        return "5";
    }
    
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
    
    private int getMethod1(int a, int b) {
        return a + b;
    }
    
    private long getMethod11(long a, int b) {
        return a + b;
    }
    
    private long getMethod12(Boolean a, int b) {
        return b;
    }
    
    @Override
    public int getAge() {
        return age;
    }
    
    @Override
    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public Child getParentOwn() {
        return new Child();
    }
    
    @Override
    public Object get() {
        return this;
    }
}
