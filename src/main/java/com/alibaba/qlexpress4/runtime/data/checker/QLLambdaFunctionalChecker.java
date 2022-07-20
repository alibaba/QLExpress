package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.CacheUtil;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:35
 */
public class QLLambdaFunctionalChecker implements TypeConvertChecker<ParametersConversion.QLMatchConversation, Class<?>, Class<?>> {


    @Override
    public boolean typeCheck(Class<?> source, Class<?> target) {
        return (source == QLambda.class) && CacheUtil.isFunctionInterface(target);
    }

    @Override
    public ParametersConversion.QLMatchConversation typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConversation.IMPLICIT;
    }
}
