package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * Author: TaoKan
 */
public interface TypeConvertChecker<S> {

    default boolean typeCheck(S source, Class<?> sourceType){
        return true;
    }

    QLConvertResult typeReturn(S source, Class<?> sourceType);

}
