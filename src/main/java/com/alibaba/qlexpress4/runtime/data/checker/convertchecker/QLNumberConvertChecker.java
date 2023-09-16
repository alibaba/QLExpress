package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.checker.convertchecker.number.*;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: TaoKan
 */
public class QLNumberConvertChecker implements TypeConvertChecker<Object> {

    private final Map<Class<?>,TypeConvertChecker> numberConvertMap;

    public QLNumberConvertChecker(){
        numberConvertMap = new HashMap<>(16);
        numberConvertMap.put(Byte.class,new QLNumberToByteConvertChecker());
        numberConvertMap.put(byte.class,new QLNumberToByteConvertChecker());
        numberConvertMap.put(Character.class,new QLNumberToCharConvertChecker());
        numberConvertMap.put(char.class,new QLNumberToCharConvertChecker());
        numberConvertMap.put(Short.class,new QLNumberToShortConvertChecker());
        numberConvertMap.put(short.class,new QLNumberToShortConvertChecker());
        numberConvertMap.put(Integer.class,new QLNumberToIntConvertChecker());
        numberConvertMap.put(int.class,new QLNumberToIntConvertChecker());
        numberConvertMap.put(Long.class,new QLNumberToLongConvertChecker());
        numberConvertMap.put(long.class,new QLNumberToLongConvertChecker());
        numberConvertMap.put(Float.class,new QLNumberToFloatConvertChecker());
        numberConvertMap.put(float.class,new QLNumberToFloatConvertChecker());
        numberConvertMap.put(Double.class,new QLNumberToDoubleConvertChecker());
        numberConvertMap.put(double.class,new QLNumberToDoubleConvertChecker());
        numberConvertMap.put(BigInteger.class,new QLNumberToBigIntegerConvertChecker());
        numberConvertMap.put(BigDecimal.class,new QLNumberToBigDecimalConvertChecker());
    }

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return Number.class.isAssignableFrom(type) || type.isPrimitive();
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        if(value instanceof Number){
            Number n = (Number) value;
            TypeConvertChecker typeConvertChecker = numberConvertMap.get(type);
            if(typeConvertChecker != null){
                return (QLConvertResult) typeConvertChecker.typeReturn(n,type);
            }
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
    }
}
