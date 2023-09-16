package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.BooleanConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * Author: TaoKan
 */
public class QLBooleanConvertChecker implements TypeConvertChecker<Object> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type == Boolean.class || type == boolean.class;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return BooleanConversion.trans(value);
    }
}
