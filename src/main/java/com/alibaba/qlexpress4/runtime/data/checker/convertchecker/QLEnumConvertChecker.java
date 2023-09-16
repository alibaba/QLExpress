package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.convert.EnumConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * Author: TaoKan
 */
public class QLEnumConvertChecker implements TypeConvertChecker<Object> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type.isEnum();
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return EnumConversion.trans(value, (Class<? extends Enum>) type);
    }
}
