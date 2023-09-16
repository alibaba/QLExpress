package com.alibaba.qlexpress4.runtime.data.checker.convertchecker.array;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * Author: TaoKan
 */
public class QLArrayAssignableConvertChecker implements TypeConvertChecker<Object> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type.isAssignableFrom(value.getClass());
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
       return new QLConvertResult(QLConvertResultType.CAN_TRANS, value);
    }
}
