package com.alibaba.qlexpress4.test.property;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:55
 */
public class Child extends Parent{
    public String getWork(){
        return "child";
    }

    public String work = "childWork";

    public Boolean getBooValue() {
        return true;
    }

    public void setBooValue(Boolean booValue) {
        this.booValue = booValue;
    }

    private Boolean booValue = false;

    private String getMethod1(int a, int b){
        return "c";
    }

    public static String getStaticGetParam(Integer a, Integer b) {
        return "5";
    }
}
