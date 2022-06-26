package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.exception.QLTransferException;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.process.CandidateMethodAttr;
import com.alibaba.qlexpress4.runtime.data.process.ParametersMatcher;

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

        public static int findMostSpecificSignature(QLCaches qlCaches, Class<?>[] goalMatch, Class<?>[][] candidates) {
            ParametersMatcher bestMatcher = new ParametersMatcher();

            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i];
                int level = ParametersConversion.calculatorMatchConversionLevel(qlCaches, goalMatch, targetMatch, 0);
                if (level > bestMatcher.getMatchLevel()) {
                    bestMatcher.setParametersClassType(targetMatch);
                    bestMatcher.setMatchLevel(level);
                    bestMatcher.setIndex(i);
                } else if (level == bestMatcher.getMatchLevel() && level != ParametersMatcher.DEFAULT_LEVEL) {
                    throw new QLTransferException("not the only matcher found");
                }
            }
            return bestMatcher.getIndex();
        }


        public static int findMostSpecificSignature(QLCaches qlCaches, Class<?>[] goalMatch, CandidateMethodAttr[] candidates) {
            ParametersMatcher bestMatcher = new ParametersMatcher();

            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i].getParamClass();
                //parent first
                int assignLevel = candidates[i].getLevel();
                int level = ParametersConversion.calculatorMatchConversionLevel(qlCaches, goalMatch, targetMatch, assignLevel);
                if (level < bestMatcher.getMatchLevel()) {
                    bestMatcher.setParametersClassType(targetMatch);
                    bestMatcher.setMatchLevel(level);
                    bestMatcher.setIndex(i);
                } else if (level == bestMatcher.getMatchLevel() && level != ParametersMatcher.DEFAULT_LEVEL) {
                    throw new QLTransferException("not the only matcher found");
                }
            }
            return bestMatcher.getIndex();
        }
    }

}
