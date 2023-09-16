package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ClassConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * Author: TaoKan
 */
public class QLClassConvertChecker implements TypeConvertChecker<Object> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type == Class.class;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return ClassConversion.trans(value);
    }
}
