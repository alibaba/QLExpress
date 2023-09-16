package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

import java.util.Collection;

/**
 * Author: TaoKan
 */
public class ListConversion {
    public static QLConvertResult trans(final Object object, final Class type) {
        Collection<?> collection = ArrayConversion.toCollection(object);
        return new QLConvertResult(QLConvertResultType.CAN_TRANS, collection);
    }
}