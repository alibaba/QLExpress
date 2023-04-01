package com.alibaba.qlexpress4.test.lambda;

import java.util.function.Function;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午4:37
 */
public class TestQLambdaToFunction {
    public static Object apply(Function function, Object object) throws Throwable {
        return function.apply(object);
    }
}
