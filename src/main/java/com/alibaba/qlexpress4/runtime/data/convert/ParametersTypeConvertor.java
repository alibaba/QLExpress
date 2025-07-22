package com.alibaba.qlexpress4.runtime.data.convert;

import java.lang.reflect.Array;

/**
 * Author: TaoKan
 */
public class ParametersTypeConvertor {
    public static Object[] cast(Object[] arguments, Class<?>[] paramTypes, boolean isVarArg) {
        if (!isVarArg) {
            Object[] result = new Object[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                Object argument = arguments[i];
                result[i] = ObjTypeConvertor.cast(argument, paramTypes[i]).getConverted();
            }
            return result;
        }
        
        Class<?> itemType = paramTypes[paramTypes.length - 1].getComponentType();
        Object varArgs = Array.newInstance(itemType, arguments.length - paramTypes.length + 1);
        int varArgStart = paramTypes.length - 1;
        for (int i = varArgStart; i < arguments.length; i++) {
            Object argument = arguments[i];
            Object castValue = ObjTypeConvertor.cast(argument, itemType).getConverted();
            Array.set(varArgs, i - varArgStart, castValue);
        }
        
        Object[] result = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length - 1; i++) {
            result[i] = ObjTypeConvertor.cast(arguments[i], paramTypes[i]).getConverted();
        }
        result[result.length - 1] = varArgs;
        return result;
    }
}
