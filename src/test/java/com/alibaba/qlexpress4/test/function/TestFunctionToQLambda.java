package com.alibaba.qlexpress4.test.function;

import com.alibaba.qlexpress4.runtime.QLambda;

/**
 * @Author TaoKan
 * @Date 2022/12/25 上午10:05
 */
public class TestFunctionToQLambda {
    public static Object runnable(QLambda qLambda) throws Throwable {
        return qLambda.call().getResult().get();
    }

    public static Object runnable(QLambda qLambda, Object... param) throws Throwable {
        return qLambda.call(param).getResult().get();
    }

}
