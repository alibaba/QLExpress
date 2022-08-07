package com.alibaba.qlexpress4.runtime.data.checker.convertchecker.array;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午6:40
 */
public class QLArrayDoubleStreamConvertChecker  implements TypeConvertChecker<Object> {

    private final Map<Class<?>,TypeConvertChecker> doubleSteamMap;

    public QLArrayDoubleStreamConvertChecker(){
        doubleSteamMap = new HashMap<>(2);
        doubleSteamMap.put(double[].class,new QLArrayDoubleStreamToPrimitiveConvertChecker());
        doubleSteamMap.put(Double[].class,new QLArrayDoubleStreamToDoubleConvertChecker());
    }

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return value instanceof DoubleStream;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        TypeConvertChecker checker = doubleSteamMap.get(type);
        return checker == null?null:(QLConvertResult) checker.typeReturn(value,type);
    }
}