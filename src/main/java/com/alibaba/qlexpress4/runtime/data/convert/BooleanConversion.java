package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * Author: TaoKan
 */
public class BooleanConversion {
    public static QLConvertResult trans(Object object) {
        if (object instanceof Boolean) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, object);
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
    }
}
