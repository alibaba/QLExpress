package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.annotation.QLAlias;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:55
 */
public class Parent {
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;

    private String name = "example";

    public String sex = "man";

    @QLAlias("生日")
    public String birth = "2022-01-01";

    public static final String staticPublic = "staticPublic";

    public static String staticSet = "staticSet";

    private static final String staticPrivate = "staticPrivate";

    public static String getStaticGet() {
        return staticGet;
    }

    private static final String staticGet =  "staticGet";

    public String getWork(){
        return "parent";
    }

    public static String findStatic(){
        return "static";
    }

    private String getMethod1(int a, int b){
        return "a";
    }
}
