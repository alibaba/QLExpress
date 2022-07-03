package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.*;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午6:05
 */
public class MemberHandler {

    public static class Access {
        public static Member getAccessMember(Class<?> clazz, String property, AccessMode propertyMode) {
            //from Method
            Method method = null;
            if (AccessMode.READ.equals(propertyMode)) {
                method = MethodHandler.getGetter(clazz, property);
            } else {
                method = MethodHandler.getSetter(clazz, property);
            }
            if (method != null) {
                return method;
            }
            //from field
            Field f = null;
            try {
                f = clazz.getField(property);
                return f;
            } catch (NoSuchFieldException e) {
            }
            //from QLAlias
            return FieldHandler.Preferred.gatherFieldRecursive(clazz, property);
        }
    }

    public static class Preferred {

        public static QLImplicitMatcher findMostSpecificSignatureForConstructor(Class<?>[] goalMatch, Class<?>[][] candidates) {
            QLParametersMatcher bestMatcher = new QLParametersMatcher();

            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i];
                int weight = ParametersConversion.calculatorMatchConversionWeight(goalMatch, targetMatch);
                if (weight > bestMatcher.getMatchWeight()) {
                    bestMatcher.setParametersClassType(targetMatch);
                    bestMatcher.setMatchWeight(weight);
                    bestMatcher.setIndex(i);
                } else if (weight == bestMatcher.getMatchWeight() && weight != BasicUtil.DEFAULT_WEIGHT) {
                    return new QLImplicitMatcher(false,-1);
                }
            }
            return new QLImplicitMatcher(bestMatcher.getMatchWeight() == ParametersConversion.QLMatchConversation.EXTEND.getWeight() ? true : false
                    ,bestMatcher.getIndex());
        }


        public static QLImplicitMatcher findMostSpecificSignature(Class<?>[] goalMatch, QLCandidateMethodAttr[] candidates) {
            QLParametersMatcher bestMatcher = new QLParametersMatcher();

            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i].getParamClass();
                //parent first
                int assignLevel = candidates[i].getLevel();
                int weight = ParametersConversion.calculatorMatchConversionWeight(goalMatch, targetMatch, new QLWeighter(assignLevel));
                if (weight != BasicUtil.DEFAULT_WEIGHT){
                    if (weight < bestMatcher.getMatchWeight()) {
                        bestMatcher.setParametersClassType(targetMatch);
                        bestMatcher.setMatchWeight(weight);
                        bestMatcher.setIndex(i);
                    } else if (weight == bestMatcher.getMatchWeight()) {
                        return new QLImplicitMatcher(false,-1);
                    }
                }
            }
            return new QLImplicitMatcher(bestMatcher.getMatchWeight() == ParametersConversion.QLMatchConversation.EXTEND.getWeight() ? true : false
                    ,bestMatcher.getIndex());
        }
    }

}
