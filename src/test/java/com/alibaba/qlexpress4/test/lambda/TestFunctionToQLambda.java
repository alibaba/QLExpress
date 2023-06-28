package com.alibaba.qlexpress4.test.lambda;

import com.alibaba.qlexpress4.runtime.QLambda;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午4:09
 */
public class TestFunctionToQLambda {
    public static Object runnable(QLambda qLambda) throws Throwable {
        return qLambda.call().getResult().get();
    }

    public static Object runnable(QLambda qLambda, Object... param) throws Throwable {
        return qLambda.call(param).getResult().get();
    }

}