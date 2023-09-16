package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.StringConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * Author: TaoKan
 */
public class QLStringConvertChecker implements TypeConvertChecker<Object> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type == String.class;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return StringConversion.trans(value);
    }
}
