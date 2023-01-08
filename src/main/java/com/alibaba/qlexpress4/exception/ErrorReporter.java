package com.alibaba.qlexpress4.exception;

public interface ErrorReporter {

    QLRuntimeException report(Object catchObj, String errorCode, String reason);

    QLRuntimeException report(String errorCode, String reason);

    QLRuntimeException reportFormat(String errorCode, String format, Object... args);

}
