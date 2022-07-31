package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:27
 */
public class QLNullParametersChecker implements TypeConvertChecker<ParametersConversion.QLMatchConversation, Class<?>, Class<?>> {

    @Override
    public boolean typeCheck(Class<?> source, Class<?> target) {
        return source == null;
    }

    @Override
    public ParametersConversion.QLMatchConversation typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConversation.NOT_MATCH;
//        return target == boolean.class ? ParametersConversion.QLMatchConversation.EXTEND : ParametersConversion.QLMatchConversation.NOT_MATCH;
    }
}
