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
    public static class Preferred {

        public static QLImplicitMatcher findMostSpecificSignatureForConstructor(Class<?>[] goalMatch, Class<?>[][] candidates) {
            QLParametersMatcher bestMatcher = new QLParametersMatcher();
            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i];
                QLParametersMatcher qlParametersMatcher = doParamsConversion(targetMatch,
                        goalMatch,0,i,new QLImplicitVars(false,0));
                QLParametersMatcher qlParametersMatcherSplit = doParamsConversionSplit(targetMatch,goalMatch,0,i);
                if(qlParametersMatcher.getMatchWeight() < qlParametersMatcherSplit.getMatchWeight()
                        && bestMatcher.getMatchWeight() > qlParametersMatcher.getMatchWeight()){
                    bestMatcher = qlParametersMatcher;
                }else if(qlParametersMatcher.getMatchWeight() > qlParametersMatcherSplit.getMatchWeight() &&
                        bestMatcher.getMatchWeight() > qlParametersMatcherSplit.getMatchWeight()){
                    bestMatcher = qlParametersMatcherSplit;
                }
            }
            return new QLImplicitMatcher(QLWeighter.calNeedImplicitTrans(bestMatcher.getMatchWeight()) ? true : false
                    ,bestMatcher.getIndex(),bestMatcher.getQlImplicitVars());
        }


        public static QLImplicitMatcher findMostSpecificSignature(Class<?>[] goalMatch, QLCandidateMethodAttr[] candidates) {
            QLParametersMatcher bestMatcher = new QLParametersMatcher();
            for (int i = candidates.length - 1; i >= 0; i--) {
                Class<?>[] targetMatch = candidates[i].getParamClass();
                //parent first
                int assignLevel = candidates[i].getLevel();
                QLParametersMatcher qlParametersMatcher = doParamsConversion(targetMatch,
                        goalMatch,assignLevel,i,new QLImplicitVars(false,0));
                QLParametersMatcher qlParametersMatcherSplit = doParamsConversionSplit(targetMatch,goalMatch,assignLevel,i);
                if(qlParametersMatcher.getMatchWeight() < qlParametersMatcherSplit.getMatchWeight()
                        && bestMatcher.getMatchWeight() > qlParametersMatcher.getMatchWeight()){
                    bestMatcher = qlParametersMatcher;
                }else if(qlParametersMatcher.getMatchWeight() > qlParametersMatcherSplit.getMatchWeight() &&
                        bestMatcher.getMatchWeight() > qlParametersMatcherSplit.getMatchWeight()){
                    bestMatcher = qlParametersMatcherSplit;
                }
            }
            return new QLImplicitMatcher(QLWeighter.calNeedImplicitTrans(bestMatcher.getMatchWeight()) ? true : false
                    ,bestMatcher.getIndex(),bestMatcher.getQlImplicitVars());
        }
    }


    private static QLParametersMatcher doParamsConversionSplit(Class<?>[] targetMatch, Class<?>[] goalMatch,
                    int assignLevel, int i){
        if(targetMatch.length >= goalMatch.length){
            return new QLParametersMatcher();
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
        return doParamsConversion(mergeTargetMatch,goalMatch,assignLevel,i,new QLImplicitVars(true,varsIndex));
    }

    private static QLParametersMatcher doParamsConversion(Class<?>[] targetMatch, Class<?>[] goalMatch, int assignLevel,
                                                        int i, QLImplicitVars needVars){
        QLParametersMatcher bestMatcher = new QLParametersMatcher();
        int weight = ParametersConversion.QLMatchConverter.NOT_MATCH.getWeight();
        if(targetMatch.length == goalMatch.length){
            weight = ParametersConversion.calculatorMatchConversionWeight(goalMatch, targetMatch, new QLWeighter(assignLevel));
        }
        if (weight != BasicUtil.DEFAULT_WEIGHT){
            if (weight < bestMatcher.getMatchWeight() && weight != ParametersConversion.QLMatchConverter.NOT_MATCH.getWeight()) {
                bestMatcher.setParametersClassType(targetMatch);
                bestMatcher.setMatchWeight(weight);
                bestMatcher.setIndex(i);
                bestMatcher.setQlImplicitVars(needVars);
            }
        }
        return bestMatcher;
    }

}
