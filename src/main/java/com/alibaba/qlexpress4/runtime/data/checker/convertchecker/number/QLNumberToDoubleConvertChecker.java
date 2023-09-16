package com.alibaba.qlexpress4.runtime.data.checker.convertchecker.number;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * Author: TaoKan
 */
public class QLNumberToDoubleConvertChecker implements TypeConvertChecker<Number> {

    @Override
    public QLConvertResult typeReturn(Number n, Class<?> type) {
        double answer = n.doubleValue();
        if (!(n instanceof Double) && (answer == Double.NEGATIVE_INFINITY
                || answer == Double.POSITIVE_INFINITY)) {
            return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
        }
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, answer);
    }
}