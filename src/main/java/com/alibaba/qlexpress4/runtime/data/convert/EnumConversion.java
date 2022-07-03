package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:38
 */
public class EnumConversion {

    public static QLConvertResult trans(Object object, Class<? extends Enum> type) {
        if (object == null) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, null);
        }
        if (type.isInstance(object)) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (Enum) object);
        }
        if (object instanceof String) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, Enum.valueOf(type, object.toString()));
        }
        return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
    }

}
