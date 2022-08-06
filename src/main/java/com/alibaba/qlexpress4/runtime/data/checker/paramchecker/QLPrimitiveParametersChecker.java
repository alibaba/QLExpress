package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:38
 */
public class QLPrimitiveParametersChecker implements TypeConvertChecker<ParametersConversion.QLMatchConversation, Class<?>, Class<?>> {


    @Override
    public boolean typeCheck(Class<?> source, Class<?> target) {
        Class<?> sourcePrimitive = source.isPrimitive() ? source : BasicUtil.transToPrimitive(source);
        Class<?> targetPrimitive = target.isPrimitive() ? target : BasicUtil.transToPrimitive(target);
        return sourcePrimitive != null && targetPrimitive != null && BasicUtil.classMatchImplicit(targetPrimitive, sourcePrimitive);
    }

    @Override
    public ParametersConversion.QLMatchConversation typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConversation.IMPLICIT;
    }
}
