package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午6:40
 */
public class QLArrayLongStreamConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    private final Map<Class<?>,TypeConvertChecker> longSteamMap;

    public QLArrayLongStreamConvertChecker(){
        longSteamMap = new HashMap<>(3);
        longSteamMap.put(long[].class,new QLArrayLongStreamToPrimitiveLongChecker());
        longSteamMap.put(double[].class,new QLArrayLongStreamToPrimitiveDoubleChecker());
        longSteamMap.put(Long[].class,new QLArrayLongStreamToLongChecker());
    }

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return value instanceof LongStream;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        TypeConvertChecker checker = longSteamMap.get(type);
        return checker == null?null:(QLConvertResult) checker.typeReturn(value,type);
    }
}