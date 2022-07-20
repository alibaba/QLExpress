package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.runtime.data.convert.CharacterConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午11:03
 */
public class QLCharacterConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type == Character.class || type == char.class;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return CharacterConversion.trans(value);
    }
}
