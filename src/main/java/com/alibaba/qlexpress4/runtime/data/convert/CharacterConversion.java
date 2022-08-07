package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:39
 */
public class CharacterConversion {

    public static QLConvertResult trans(Object object) {
        if (object == null) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, null);
        } else if (object instanceof Character) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (Character) object);
        } else if (object instanceof Number) {
            Number value = (Number) object;
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, (char) value.intValue());
        }
        String text = object.toString();
        if (text.length() == 1) {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, text.charAt(0));
        } else {
            return new QLConvertResult(QLConvertResultType.CAN_TRANS, null);
        }
    }
}
