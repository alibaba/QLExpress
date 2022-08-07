package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;

/**
 * @Author TaoKan
 * @Date 2022/8/7 上午10:56
 */
public interface MatchChecker {
    boolean typeMatch(Class<?> sourceType, Class<?> targetType);

    ParametersConversion.QLMatchConverter typeReturn(Class<?> sourceType, Class<?> targetType);
}
