package com.alibaba.qlexpress4.exception;

public class MockErrorReporter implements ErrorReporter {
    @Override
    public QLRuntimeException reportFormatWithCatch(Object catchObj, String errorCode, String format, Object... args) {
        String message = String.format(format, args);
        QLRuntimeException exception = new QLRuntimeException(catchObj, message, errorCode) {};
        if (catchObj instanceof Throwable) {
            exception.initCause((Throwable) catchObj);
        }
        return exception;
    }
}
