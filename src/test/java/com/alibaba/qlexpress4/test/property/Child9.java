package com.alibaba.qlexpress4.test.property;


/**
 * Author: TaoKan
 */
public class Child9 {
    public Child9(){

    }

    public Child9(int a,String...args){
    }

    public String addField(int a, String... args){
        return "1";
    };

    public String addField1(Object... args){
        return "1";
    };

    public String addField2(Object s,Object... args){
        return "1";
    };

    public String addField3(Object s,Integer... args){
        return "1";
    };
}
