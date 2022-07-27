package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.checker.*;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLWeighter;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午7:01
 */
public class ParametersConversion {
    private static final TypeConvertChecker[] typeParametersChecker;

    static {
        typeParametersChecker = new TypeConvertChecker[]{
                new QLNullParametersChecker(),
                new QLEqualsParametersChecker(),
                new QLPrimitiveParametersChecker(),
                new QLAssignableParametersChecker(),
                new QLObjectParametersChecker(),
                new QLLambdaFunctionalChecker(),
                new QLExtendParametersChecker(),
                new QLArrayParametersChecker()};
    }

    public static int calculatorMatchConversionWeight(Class<?>[] goal, Class<?>[] candidate) {
        return calculatorMatchConversionWeight(goal, candidate, new QLWeighter());
    }

    public static int calculatorMatchConversionWeight(Class<?>[] goal, Class<?>[] candidate, QLWeighter weighter) {
        int matchConversionWeight = QLMatchConversation.EQUALS.getWeight();
        for (int i = 0; i < goal.length; i++) {
            QLMatchConversation result = compareParametersTypes(candidate[i], goal[i]);
            if (QLMatchConversation.NOT_MATCH == result) {
                return QLMatchConversation.NOT_MATCH.getWeight();
            }
            int weight = weighter.addWeight(result.getWeight());
            if (matchConversionWeight < weight) {
                matchConversionWeight = weight;
            }
        }
        //child level first
        return matchConversionWeight;
    }


    public static QLMatchConversation compareParametersTypes(Class<?> target, Class<?> source) {
        for (TypeConvertChecker checker : typeParametersChecker){
            if(checker.typeCheck(source,target)){
                return (QLMatchConversation)checker.typeReturn(source,target);
            }
        }
        return QLMatchConversation.NOT_MATCH;
    }


    public static QLConvertResult convert(Object[] oriParams, Class<?>[] oriTypes, Class<?>[] goalTypes, boolean needImplicitTrans){
        if(!needImplicitTrans){
            return new QLConvertResult(QLConvertResultType.CAN_TRANS,oriParams);
        }
        for(int i = 0; i < oriTypes.length; i++){
            if(oriTypes[i] != goalTypes[i]){
                QLConvertResult paramResult = InstanceConversion.castObject(oriParams[i],goalTypes[i]);
                if(paramResult.getResultType().equals(QLConvertResultType.NOT_TRANS)){
                    return paramResult;
                }
                oriParams[i] = paramResult.getCastValue();
            }
        }
        return new QLConvertResult(QLConvertResultType.CAN_TRANS,oriParams);
    }

    //weight less = level higher
    public enum QLMatchConversation {

        NOT_MATCH(-1), EXTEND(8), IMPLICIT(3), ASSIGN(2), EQUALS(1);

        private final int weight;

        QLMatchConversation(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

    }
}
