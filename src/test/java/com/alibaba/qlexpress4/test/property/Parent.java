package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.runtime.Value;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:55
 */
public class Parent implements Value {
    private final long result;
    public static final String staticPublic = "staticPublic";
    private static final String staticPrivate = "staticPrivate";
    private static final String staticGet = "staticGet";
    public static String staticSet = "staticSet";
    private static String staticSetPrivate = "staticSetPrivate";
    private static final String staticFinal = "staticFinal";
    public String sex = "man";


    public Parent(){
        this.result = 0L;
    }
    public Parent(int age){
        this.age = age;
        this.result = 0L;
    }

    public Parent(int a, int b){
        this.result = a+b;
    }

    @QLAlias("生日")
    public String birth = "2022-01-01";
    private int age;
    private String name = "example";


    public static String getStaticGet() {
        return "staticGet1";
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

    public Parent getParentOwn() {return new Parent(); }

    private String getMethod1(int a, int b) {
        return "a";
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
    @Override
    public Object get() {
        return this;
    }

//    public String getMethod2(long a,long b){return "b";}

    public String getMethod2(long a){return "b";}
    public String getMethod2(int b){return "b";}

    private long getMethod11(int a, int b) {
        return a+b+1;
    }

    private long getMethod12(int a, int b) {
        return a+b;
    }

}
