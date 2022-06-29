package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.data.implicit.QLWeighter;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午7:01
 */
public class ParametersConversion {

    public static int calculatorMatchConversionWeight(Class<?>[] goal, Class<?>[] candidate) {
        return calculatorMatchConversionWeight(goal, candidate, new QLWeighter());
    }

    public static int calculatorMatchConversionWeight(Class<?>[] goal, Class<?>[] candidate, QLWeighter weighter) {
        int matchConversionWeight = MatchConversation.EQUALS.getWeight();
        for (int i = 0; i < goal.length; i++) {
            MatchConversation result = compareParametersTypes(candidate[i], goal[i]);
            if (MatchConversation.NOT_MATCH == result) {
                return MatchConversation.NOT_MATCH.getWeight();
            }
            int weight = weighter.addWeight(result.getWeight());
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


    public static Object[] convert(Object[] oriParams, Class<?>[] oriTypes, Class<?>[] goalTypes, boolean needImplicitTrans){
        if(!needImplicitTrans){
            return oriParams;
        }
        for(int i = 0; i < oriTypes.length; i++){
            if(oriTypes[i] != goalTypes[i]){
                oriParams[i] = InstanceConversion.castObject(oriParams[i],goalTypes[i]);
            }
        }
        return oriParams;
    }

    public enum MatchConversation {

        NOT_MATCH(-1), EXTEND(4), IMPLICIT(3), ASSIGN(2), EQUALS(1);

        private final int weight;

        MatchConversation(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

    }
}
