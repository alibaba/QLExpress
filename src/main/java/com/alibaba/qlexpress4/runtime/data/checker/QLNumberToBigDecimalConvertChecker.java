package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午8:04
 */
public class QLNumberToBigDecimalConvertChecker implements TypeConvertChecker<QLConvertResult, Number, Class<?>> {

    @Override
    public QLConvertResult typeReturn(Number n, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, NumberMath.toBigDecimal(n));
    }
}
