package com.alibaba.qlexpress4.test.property;


/**
 * @Author TaoKan
 * @Date 2022/7/31 下午12:18
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
