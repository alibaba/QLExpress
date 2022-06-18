package com.alibaba.qlexpress4.runtime.util;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;

/**
 * Author: DQinYuan
 */
public class ThrowUtils {

    public static QLRuntimeException wrapException(Exception e, ErrorReporter errorReporter, String errCode,
                                                   String errMsg, String... args) {
        return e instanceof QLRuntimeException? (QLRuntimeException) e :
                errorReporter.report(errCode, errMsg, (Object[]) args);
    }

}
