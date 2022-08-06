package com.alibaba.qlexpress4.runtime;

/**
 * @Author TaoKan
 * @Date 2022/8/6 上午10:27
 */
@FunctionalInterface
public interface QLFunctional<T,R> {
    R call(T t);
}
