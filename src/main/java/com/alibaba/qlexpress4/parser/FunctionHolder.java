package com.alibaba.qlexpress4.parser;

import java.util.function.Function;

/**
 * Author: DQinYuan
 */
public class FunctionHolder<T, R> implements Function<T, R> {

    private final Function<T, R> delegated;

    private R result;

    public FunctionHolder(Function<T, R> delegated) {
        this.delegated = delegated;
    }

    @Override
    public R apply(T t) {
        R r = delegated.apply(t);
        this.result = r;
        return r;
    }

    public R getResult() {
        return result;
    }
}
