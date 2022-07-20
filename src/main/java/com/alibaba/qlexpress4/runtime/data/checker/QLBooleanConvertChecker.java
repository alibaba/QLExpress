package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.convert.BooleanConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午11:05
 */
public class QLBooleanConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type == Boolean.class || type == boolean.class;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return BooleanConversion.trans(value);
    }
}
