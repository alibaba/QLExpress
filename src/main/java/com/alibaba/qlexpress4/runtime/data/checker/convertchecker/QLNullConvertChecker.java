package com.alibaba.qlexpress4.runtime.data.checker.convertchecker;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午10:50
 */
public class QLNullConvertChecker implements TypeConvertChecker<Object> {
    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return type == Object.class;
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, value);
    }
}
