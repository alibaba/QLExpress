package com.alibaba.qlexpress4.exception;

public interface ErrorReporter {

    QLRuntimeException report(Object attachment, String errorCode, String reason);

    QLRuntimeException report(String errorCode, String reason);

    QLRuntimeException report(String errorCode, String format, Object... args);

}
