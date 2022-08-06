package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.enums.AccessMode;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.*;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Optional;

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
                Optional<QLImplicitMatcher> optional = Optional.ofNullable(doParamsConversion(targetMatch,
                        goalMatch,0,bestMatcher,i,new QLImplicitVars(false,0)));
                QLImplicitMatcher result = optional.orElse(doParamsConversionSplit(targetMatch,goalMatch,0,bestMatcher,i));
                if(result != null){
                    return result;
                }
            }
            return new QLImplicitMatcher(bestMatcher.getMatchWeight()%BasicUtil.LEVEL_FACTOR == ParametersConversion.QLMatchConversation.EXTEND.getWeight() ? true : false
                    ,bestMatcher.getIndex(),bestMatcher.getQlImplicitVars());
        }


        public static QLImplicitMatcher findMostSpecificSignature(Class<?>[] goalMatch, QLCandidateMethodAttr[] candidates) {
            QLParametersMatcher bestMatcher = new QLParametersMatcher();
            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i].getParamClass();
                //parent first
                int assignLevel = candidates[i].getLevel();
                Optional<QLImplicitMatcher> optional = Optional.ofNullable(doParamsConversion(targetMatch,
                        goalMatch,assignLevel,bestMatcher,i,new QLImplicitVars(false,0)));
                QLImplicitMatcher result = optional.orElse(doParamsConversionSplit(targetMatch,goalMatch,assignLevel,bestMatcher,i));
                if(result != null){
                    return result;
                }
            }
            return new QLImplicitMatcher(bestMatcher.getMatchWeight()%BasicUtil.LEVEL_FACTOR == ParametersConversion.QLMatchConversation.EXTEND.getWeight() ? true : false
                    ,bestMatcher.getIndex(),bestMatcher.getQlImplicitVars());
        }
    }


    private static QLImplicitMatcher doParamsConversionSplit(Class<?>[] targetMatch, Class<?>[] goalMatch,
                    int assignLevel, QLParametersMatcher bestMatcher, int i){
        if(targetMatch.length >= goalMatch.length){
            return null;
        }
        Class<?>[] mergeTargetMatch = new Class[goalMatch.length];
        int index = 0;
        int varsIndex = 0;
        int length = goalMatch.length - targetMatch.length + 1;
        for(int j = 0; j < targetMatch.length;j++){
            Class<?> clazz = targetMatch[j];
            if(clazz.isArray()){
                if(clazz == goalMatch[j]){
                    mergeTargetMatch[index] = clazz;
                    index++;
                }else {
                    Class<?> arrayItemClazz = clazz.getComponentType();
                    varsIndex = index;
                    for(int k = 0; k < length; k++,index++){
                        mergeTargetMatch[index] = arrayItemClazz;
                    }
                }
            }else {
                mergeTargetMatch[index] = targetMatch[j];
                index++;
            }
        }
        return doParamsConversion(mergeTargetMatch,goalMatch,assignLevel,bestMatcher,i,new QLImplicitVars(true,varsIndex));
    }


    private static QLImplicitMatcher doParamsConversion(Class<?>[] targetMatch, Class<?>[] goalMatch, int assignLevel,
                                                        QLParametersMatcher bestMatcher, int i, QLImplicitVars needVars){
        int weight = ParametersConversion.calculatorMatchConversionWeight(goalMatch, targetMatch, new QLWeighter(assignLevel));
        if (weight != BasicUtil.DEFAULT_WEIGHT){
            if (weight < bestMatcher.getMatchWeight() && weight != ParametersConversion.QLMatchConversation.NOT_MATCH.getWeight()) {
                bestMatcher.setParametersClassType(targetMatch);
                bestMatcher.setMatchWeight(weight);
                bestMatcher.setIndex(i);
                bestMatcher.setQlImplicitVars(needVars);
            } else if (weight == bestMatcher.getMatchWeight()) {
                return new QLImplicitMatcher(false,BasicUtil.DEFAULT_MATCH_INDEX);
            }
        }
        return null;
    }

}
