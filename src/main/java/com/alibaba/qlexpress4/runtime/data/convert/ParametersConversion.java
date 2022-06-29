package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.data.process.Weighter;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午7:01
 */
public class ParametersConversion {

    public static int calculatorMatchConversionWeight(Class<?>[] goal, Class<?>[] candidate) {
        return calculatorMatchConversionWeight(goal, candidate, new Weighter());
    }

    public static int calculatorMatchConversionWeight(Class<?>[] goal, Class<?>[] candidate, Weighter weighter) {
        int matchConversionWeight = MatchConversation.EQUALS.weight;
        for (int i = 0; i < goal.length; i++) {
            MatchConversation result = compareParametersTypes(candidate[i], goal[i]);
            if (MatchConversation.NOT_MATCH == result) {
                return MatchConversation.NOT_MATCH.weight;
            }
            int weight = weighter.addWeight(result.weight);
            if (matchConversionWeight < weight) {
                matchConversionWeight = weight;
            }
        }
        //child level first
        return matchConversionWeight;
    }


    private static MatchConversation compareParametersTypes(Class<?> target, Class<?> source) {
        if (target == source) {
            return MatchConversation.EQUALS;
        }
        if (source.isAssignableFrom(target)) {
            return MatchConversation.ASSIGN;
        }
        if (target.isArray() && source.isArray()) {
            return compareParametersTypes(target.getComponentType(), source.getComponentType());
        }
        if (source.isPrimitive() && target == Object.class) {
            return MatchConversation.IMPLICIT;
        }
        if (BasicUtil.transToPrimitive(source) == BasicUtil.transToPrimitive(target)) {
            return MatchConversation.IMPLICIT;
        }
        if ((source == QLambda.class || source.isAssignableFrom(QLambda.class))
                && CacheUtil.isFunctionInterface(target)) {
            return MatchConversation.IMPLICIT;
        }
        for (Class<?>[] classMatch : BasicUtil.CLASS_MATCHES_IMPLICIT) {
            if (target == classMatch[0] && source == classMatch[1]) {
                return MatchConversation.IMPLICIT;
            }
        }
        for (Class<?>[] classMatch : BasicUtil.CLASS_MATCHES) {
            if (target == classMatch[0] && source == classMatch[1]) {
                return MatchConversation.EXTEND;
            }
        }
        return MatchConversation.NOT_MATCH;
    }


    enum MatchConversation {

        NOT_MATCH(-1), EXTEND(4), IMPLICIT(3), ASSIGN(2), EQUALS(1);

        private int weight;

        MatchConversation(int weight) {
            this.weight = weight;
        }
    }
}
