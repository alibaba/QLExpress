package com.alibaba.qlexpress4.runtime.util;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.exception.UserDefineException;

import java.util.Objects;

/**
 * Author: DQinYuan
 */
public class ThrowUtils {

    public static QLRuntimeException wrapThrowable(Throwable t, ErrorReporter errorReporter, String errCode,
                                                   String errMsg, String... args) {
        return t instanceof QLRuntimeException? (QLRuntimeException) t :
                errorReporter.reportFormatWithCatch(t, errCode, errMsg, (Object[]) args);
    }

    public static QLRuntimeException reportUserDefinedException(ErrorReporter errorReporter, UserDefineException e) {
        if (Objects.equals(e.getType(), UserDefineException.ExceptionType.INVALID_ARGUMENT)) {
            throw errorReporter.report(QLErrorCodes.INVALID_ARGUMENT.name(), e.getMessage());
        } else {
            throw errorReporter.report(QLErrorCodes.BIZ_EXCEPTION.name(), e.getMessage());
        }
    }
}
