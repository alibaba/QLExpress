package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.annotation.QLField;
import com.alibaba.qlexpress4.annotation.QLFunction;

/**
 * @Author TaoKan
 * @Date 2022/7/31 下午12:18
 */
public class Child8 {
    @QLField("全局字段1")
    public int addField(int a, int b){
        return a+b;
    };


    @QLFunction("全局函数1")
    public int add(int a, int b){
        return a+b;
    };
}
