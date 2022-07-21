package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午8:04
 */
public class QLNumberToFloatConvertChecker implements TypeConvertChecker<QLConvertResult, Number, Class<?>> {

    @Override
    public QLConvertResult typeReturn(Number n, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, n.floatValue());
    }
}