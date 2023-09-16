package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.MatchChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;

/**
 * Author: TaoKan
 */
public class QLArrayParametersChecker implements MatchChecker {

    @Override
    public boolean typeMatch(Class<?> source, Class<?> target) {
        return target.isArray() && source.isArray();
    }

    @Override
    public ParametersConversion.QLMatchConverter typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.compareParametersTypes(target.getComponentType(), source.getComponentType());
    }
}
