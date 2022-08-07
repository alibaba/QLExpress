package com.alibaba.qlexpress4.runtime.data.checker.convertchecker.number;

import com.alibaba.qlexpress4.runtime.data.checker.TypeConvertChecker;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/7/21 上午8:04
 */
public class QLNumberToCharConvertChecker implements TypeConvertChecker<Number> {

    @Override
    public QLConvertResult typeReturn(Number n, Class<?> type) {
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, (char) n.intValue());
    }
}