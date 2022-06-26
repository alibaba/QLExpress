package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.exception.QLTransferException;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:39
 */
public class CharacterConversion {

    public static Character trans(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Character) {
            return (Character) object;
        } else if (object instanceof Number) {
            Number value = (Number) object;
            return (char) value.intValue();
        }
        String text = object.toString();
        if (text.length() == 1) {
            return text.charAt(0);
        } else {
            throw new QLTransferException("can not cast to char");
        }
    }
}
