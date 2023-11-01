package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitVars;

import java.lang.reflect.Array;

/**
 * Author: TaoKan
 */
public class ParametersConversion {
    public static Object[] convert(Object[] arguments, Class<?>[] paramTypes, boolean isVarArg) {
        if (!isVarArg) {
            Object[] result = new Object[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Object argument = arguments[i];
                result[i] = InstanceConversion.castObject(argument, paramTypes[i]).getCastValue();
            }
            return result;
        }

        Class<?> itemType = paramTypes[paramTypes.length - 1].getComponentType();
        Object varArgs = Array.newInstance(itemType, arguments.length - paramTypes.length + 1);
        int varArgStart = paramTypes.length - 1;
        for (int i = varArgStart; i < arguments.length; i++) {
            Object argument = arguments[i];
            Object castValue = InstanceConversion.castObject(argument, itemType).getCastValue();
            Array.set(varArgs, i - varArgStart, castValue);
        }

        Object[] result = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length - 1; i++) {
            result[i] = InstanceConversion.castObject(arguments[i], paramTypes[i]).getCastValue();
        }
        result[result.length - 1] = varArgs;
        return result;
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

        NOT_MATCH(-1), EXTEND(8), IMPLICIT(4), ASSIGN(3), PRIMITIVE(2), EQUALS(1);

        private final int weight;

        QLMatchConverter(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

    }
}
