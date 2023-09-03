package com.alibaba.qlexpress4.runtime.util;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * Author: DQinYuan
 */
public class ValueUtils {

    public static Value toImmutable(Value origin) {
        return origin instanceof LeftValue? new DataValue(origin): origin;
    }

    public static <T> T assertType(Object obj, Class<T> assertType, String errCode,
                             String errMsg, ErrorReporter errorReporter) {
        if (obj != null && assertType.isAssignableFrom(obj.getClass())) {
            return assertType.cast(obj);
        }
        throw errorReporter.report(errCode, errMsg);
    }
}
