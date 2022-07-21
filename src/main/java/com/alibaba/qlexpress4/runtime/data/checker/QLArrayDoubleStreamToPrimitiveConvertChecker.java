package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午6:40
 */
public class QLArrayDoubleStreamToPrimitiveConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, ((DoubleStream) value).toArray());
    }
}