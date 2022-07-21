package com.alibaba.qlexpress4.runtime.data.convert;


import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:41
 */
public class NumberConversion {
    public static QLConvertResult castToNumber(Object object) {
        if (object instanceof Number) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (Number) object);
        }
        if (object instanceof Character) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (int) (Character) object);
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);

    }

}
