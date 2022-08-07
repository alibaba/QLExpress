package com.alibaba.qlexpress4.test.property;

/**
 * @Author TaoKan
 * @Date 2022/8/6 下午4:16
 */
public class Child10 {
    public void setAA(int a){System.out.println("setAA");}

    public boolean setA(int a){return true;}

    public Integer setAAA(Integer... a){
        int s = 0;
        for(int i : a){
           s += i;
        }
        return s;
    }


    public Integer setAAAA(String a, Integer...b){
        int s = 0;
        for(int i : b){
            s += i;
        }
        return s;
    }
}
