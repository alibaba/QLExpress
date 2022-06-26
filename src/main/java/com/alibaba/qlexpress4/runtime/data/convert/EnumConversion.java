package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.exception.QLTransferException;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:38
 */
public class EnumConversion {

    public static Enum trans(Object object, Class<? extends Enum> type) {
        if (object == null) {
            return null;
        }
        if (type.isInstance(object)) {
            return (Enum) object;
        }
        if (object instanceof String) {
            return Enum.valueOf(type, object.toString());
        }
        throw new QLTransferException("can not cast " + object.getClass().getName()
                + " value " + object + " to enum type");
    }

}
