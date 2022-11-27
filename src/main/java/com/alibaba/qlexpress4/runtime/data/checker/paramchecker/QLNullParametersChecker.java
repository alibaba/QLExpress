package com.alibaba.qlexpress4.runtime.data.checker.paramchecker;

import com.alibaba.qlexpress4.runtime.data.checker.MatchChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:27
 */
public class QLNullParametersChecker implements MatchChecker {

    @Override
    public boolean typeMatch(Class<?> source, Class<?> target) {
        return source == null || target == null;
    }

    @Override
    public ParametersConversion.QLMatchConverter typeReturn(Class<?> source, Class<?> target) {
        return ParametersConversion.QLMatchConverter.NOT_MATCH;
//        return target == boolean.class ? ParametersConversion.QLMatchConversation.EXTEND : ParametersConversion.QLMatchConversation.NOT_MATCH;
    }
}
