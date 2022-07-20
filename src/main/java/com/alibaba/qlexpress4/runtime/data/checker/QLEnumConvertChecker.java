package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.convert.EnumConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午11:00
 */
public class QLEnumConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type.isEnum();
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return EnumConversion.trans(value, (Class<? extends Enum>) type);
    }
}
