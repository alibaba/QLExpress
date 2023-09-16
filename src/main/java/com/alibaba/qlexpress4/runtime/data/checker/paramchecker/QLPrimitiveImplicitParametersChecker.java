package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.MatchChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * Author: TaoKan
 */
public class QLPrimitiveImplicitParametersChecker implements MatchChecker {


    @Override
    public boolean typeMatch(Class<?> source, Class<?> target) {
        Class<?> sourcePrimitive = source.isPrimitive() ? source : BasicUtil.transToPrimitive(source);
        Class<?> targetPrimitive = target.isPrimitive() ? target : BasicUtil.transToPrimitive(target);
        return sourcePrimitive != null && targetPrimitive != null && BasicUtil.classMatchImplicit(targetPrimitive, sourcePrimitive);
    }

    @Override
    public ParametersConversion.QLMatchConverter typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConverter.IMPLICIT;
    }
}
