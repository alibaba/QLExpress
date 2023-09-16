package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.MatchChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;

import java.util.Collection;

/**
 * Author: TaoKan
 */
public class QLListParametersChecker implements MatchChecker {

    @Override
    public boolean typeMatch(Class<?> source, Class<?> target) {
        return (Collection.class.isAssignableFrom(target) && source.isArray())
                || (Collection.class.isAssignableFrom(source) && target.isArray());
    }

    @Override
    public ParametersConversion.QLMatchConverter typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConverter.EXTEND;
    }
}