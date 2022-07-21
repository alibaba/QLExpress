package com.alibaba.qlexpress4.runtime.data.checker;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:24
 */
public interface TypeConvertChecker<R, S, T> {

    default boolean typeCheck(S source, T target){
        return true;
    }

    R typeReturn(S source, T target);

}
