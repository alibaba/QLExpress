package com.alibaba.qlexpress4.exception;

public interface ErrorReporter {

    default QLRuntimeException report(Object catchObj, String errorCode, String reason) {
        return reportFormatWithCatch(catchObj, errorCode, reason);
    }

    default QLRuntimeException report(String errorCode, String reason) {
        return reportFormatWithCatch(null, errorCode, reason);
    }

    default QLRuntimeException reportFormat(String errorCode, String format, Object... args) {
        return reportFormatWithCatch(null, errorCode, format, args);
    }

    QLRuntimeException reportFormatWithCatch(Object catchObj, String errorCode, String format, Object... args);
}
