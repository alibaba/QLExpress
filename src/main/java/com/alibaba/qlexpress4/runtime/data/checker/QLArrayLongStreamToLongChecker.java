package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

import java.util.stream.LongStream;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午7:52
 */
public class QLArrayLongStreamToLongChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, ((LongStream) value).boxed().toArray(Long[]::new));
    }
}