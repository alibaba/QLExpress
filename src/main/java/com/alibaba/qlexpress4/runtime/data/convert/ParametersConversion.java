package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.checker.*;
import com.alibaba.qlexpress4.runtime.data.checker.paramchecker.QLLambdaFunctionalChecker;
import com.alibaba.qlexpress4.runtime.data.checker.paramchecker.*;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitVars;
import com.alibaba.qlexpress4.runtime.data.implicit.QLWeighter;

/**
 * @Author TaoKan
 * @Date 2022/6/25 下午7:01
 */
public class ParametersConversion {
    private static final MatchChecker[] typeParametersChecker;

    static {
        typeParametersChecker = new MatchChecker[]{
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
        int matchConversionWeight = QLMatchConverter.EQUALS.getWeight();
        for (int i = 0; i < goal.length; i++) {
            QLMatchConverter result = compareParametersTypes(candidate[i], goal[i]);
            if (QLMatchConverter.NOT_MATCH == result) {
                return QLMatchConverter.NOT_MATCH.getWeight();
            }
            int weight = weighter.addWeight(result.getWeight());
            if (matchConversionWeight < weight) {
                matchConversionWeight = weight;
            }
        }
        //child level first
        return matchConversionWeight;
    }


    public static QLMatchConverter compareParametersTypes(Class<?> target, Class<?> source) {
        for (MatchChecker checker : typeParametersChecker){
            if(checker.typeMatch(source,target)){
                return checker.typeReturn(source,target);
            }
        }
        return QLMatchConverter.NOT_MATCH;
    }


    public static QLConvertResult convert(Object[] oriParams, Class<?>[] oriTypes, Class<?>[] goalTypes,
                                          boolean needImplicitTrans, QLImplicitVars vars){
        if(!needImplicitTrans && !vars.needVarsConvert()){
            return new QLConvertResult(QLConvertResultType.CAN_TRANS,oriParams);
        }
        if(vars.needVarsConvert()){
            int afterMergeLength = vars.getVarsIndex() + 1;
            Object[] objects = new Object[afterMergeLength];
            for(int i = 0; i < afterMergeLength; i++){
                if(i < vars.getVarsIndex()) {
                    //not change
                    objects[i] = oriParams[i];
                    if(oriTypes[i] != goalTypes[i]){
                        QLConvertResult paramResult = InstanceConversion.castObject(objects[i],goalTypes[i]);
                        if(paramResult.getResultType().equals(QLConvertResultType.NOT_TRANS)){
                            return paramResult;
                        }
                        objects[i] = paramResult.getCastValue();
                    }
                }else {
                    int mergeLength = oriParams.length - vars.getVarsIndex();
                    Object r = new Object[mergeLength];
                    //TODO lingxiang
                    System.arraycopy(oriParams,vars.getVarsIndex(),r,0,mergeLength);
                    QLConvertResult paramResult = InstanceConversion.castObject(r,goalTypes[i]);
                    if(paramResult.getResultType().equals(QLConvertResultType.NOT_TRANS)){
                        return paramResult;
                    }
                    objects[i] = paramResult.getCastValue();
                }
            }
            return new QLConvertResult(QLConvertResultType.CAN_TRANS,objects);
        }else {
            for(int i = 0; i < oriTypes.length; i++){
                if(oriTypes[i] != goalTypes[i]){
                    QLConvertResult paramResult = InstanceConversion.castObject(oriParams[i],goalTypes[i]);
                    if(paramResult.getResultType().equals(QLConvertResultType.NOT_TRANS)){
                        return paramResult;
                    }
                    oriParams[i] = paramResult.getCastValue();
                }
            }
        }

        return new QLConvertResult(QLConvertResultType.CAN_TRANS,oriParams);
    }

    //weight less = level higher
    public enum QLMatchConverter {

        NOT_MATCH(-1), EXTEND(8), IMPLICIT(3), ASSIGN(2), EQUALS(1);

        private final int weight;

        QLMatchConverter(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

    }
}
