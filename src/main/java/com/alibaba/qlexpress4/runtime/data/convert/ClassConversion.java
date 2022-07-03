package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:41
 */
public class ClassConversion {

    public static QLConvertResult trans(Object object) {
        if (object == null) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, null);
        }
        if (object instanceof Class) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (Class) object);
        }
        try {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, Class.forName(object.toString()));
        } catch (Exception e) {
            return new QLConvertResult(QLConvertResultType.NOT_TRANS, null);
        }
    }

}
