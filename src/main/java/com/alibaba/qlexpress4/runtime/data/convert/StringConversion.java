package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.utils.BasicUtil;

/**
 * Author: TaoKan
 */
public class StringConversion {
    public static QLConvertResult trans(Object object) {
        if (object == null) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, BasicUtil.NULL_SIGN);
        }
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, object.toString());
    }
}
