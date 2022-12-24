package com.alibaba.qlexpress4.runtime.util;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.exception.UserDefineException;

import java.util.Objects;

/**
 * Author: DQinYuan
 */
public class ThrowUtils {

    public static QLRuntimeException wrapException(Exception e, ErrorReporter errorReporter, String errCode,
                                                   String errMsg, String... args) {
        return e instanceof QLRuntimeException? (QLRuntimeException) e :
                errorReporter.reportFormat(errCode, errMsg, (Object[]) args);
    }

    public static QLRuntimeException reportUserDefinedException(ErrorReporter errorReporter, UserDefineException e) {
        if (Objects.equals(e.getType(), UserDefineException.INVALID_PARAM)) {
            throw errorReporter.report("INVALID_ARGUMENT", e.getMessage());
        } else {
            throw errorReporter.report("BIZ_EXCEPTION", e.getMessage());
        }
    }
}
