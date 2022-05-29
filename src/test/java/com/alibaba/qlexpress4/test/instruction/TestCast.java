package com.alibaba.qlexpress4.test.instruction;

import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;

import java.util.function.Function;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:43
 */
public class TestCast {
    public static void main(String[] args){
        QLambda qLambda = new QLambdaMethod(null,null,true,null);
        System.out.println(QLambda.class.isAssignableFrom(qLambda.getClass()));
    }
}
