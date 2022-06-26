package com.alibaba.qlexpress4.runtime.data.convert;


import com.alibaba.qlexpress4.exception.QLTransferException;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:40
 */
public class BooleanConversion {
    public static boolean trans(Object object) {
        if (object == null) {
            return false;
        }
        if (object.getClass() == Boolean.class) {
            return (Boolean) object;
        }
        throw new QLTransferException("can not cast " + object.getClass().getName()
                + " value " + object + " to boolean type");
    }

}
