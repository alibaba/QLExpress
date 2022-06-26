package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.exception.QLTransferException;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:41
 */
public class ClassConversion {

    public static Class trans(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Class) {
            return (Class) object;
        }
        try {
            return Class.forName(object.toString());
        } catch (Exception e) {
            throw new QLTransferException("can not cast " + object.getClass().getName()
                    + " value " + object + " to class type");
        }
    }

}
