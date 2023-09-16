package com.alibaba.qlexpress4.runtime.data.checker.convertchecker.number;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * Author: TaoKan
 */
public class QLNumberToBigDecimalConvertChecker implements TypeConvertChecker<Number> {

    @Override
    public QLConvertResult typeReturn(Number n, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, NumberMath.toBigDecimal(n));
    }
}
