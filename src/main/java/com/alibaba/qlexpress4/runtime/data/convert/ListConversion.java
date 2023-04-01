package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

import java.util.Collection;

/**
 * @Author TaoKan
 * @Date 2022/12/15 下午5:02
 */
public class ListConversion {
    public static QLConvertResult trans(final Object object, final Class type) {
        Collection<?> collection = ArrayConversion.toCollection(object);
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, collection);
    }
}