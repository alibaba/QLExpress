package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.MatchChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:34
 */
public class QLArrayParametersChecker implements MatchChecker {

    @Override
    public boolean typeMatch(Class<?> source, Class<?> target) {
        return target.isArray() && source.isArray();
    }

    @Override
    public ParametersConversion.QLMatchConverter typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.compareParametersTypes(target, source);
    }
}
