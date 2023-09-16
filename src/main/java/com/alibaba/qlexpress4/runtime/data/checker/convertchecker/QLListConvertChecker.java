package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.ListConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

import java.util.Collection;

/**
 * Author: TaoKan
 */
public class QLListConvertChecker implements TypeConvertChecker<Object> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return ListConversion.trans(value, type);
    }
}
