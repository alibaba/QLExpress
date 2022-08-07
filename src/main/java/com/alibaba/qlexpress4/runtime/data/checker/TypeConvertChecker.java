package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:24
 */
public interface TypeConvertChecker<S> {

    default boolean typeCheck(S source, Class<?> sourceType){
        return true;
    }

    QLConvertResult typeReturn(S source, Class<?> sourceType);

}
