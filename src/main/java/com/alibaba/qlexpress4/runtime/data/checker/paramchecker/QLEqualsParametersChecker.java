package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:30
 */
public class QLEqualsParametersChecker implements TypeConvertChecker<ParametersConversion.QLMatchConversation, Class<?>, Class<?>> {
    @Override
    public boolean typeCheck(Class<?> source, Class<?> target) {
        if (target == source){
            return true;
        }
        Class<?> sourcePrimitive = source.isPrimitive() ? source : BasicUtil.transToPrimitive(source);
        Class<?> targetPrimitive = target.isPrimitive() ? target : BasicUtil.transToPrimitive(target);
        return sourcePrimitive != null && targetPrimitive != null && sourcePrimitive == targetPrimitive;
    }

    @Override
    public ParametersConversion.QLMatchConversation typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConversation.EQUALS;
    }
}
