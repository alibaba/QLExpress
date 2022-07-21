package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午6:38
 */
public class QLArrayAssignableConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type.isAssignableFrom(value.getClass());
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
       return new QLConvertResult(QLConvertResultType.CAN_TRANS, value);
    }
}
