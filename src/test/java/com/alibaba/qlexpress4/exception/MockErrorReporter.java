package com.alibaba.qlexpress4.exception;

public class MockErrorReporter implements ErrorReporter {
    @Override
    public QLRuntimeException reportFormatWithCatch(Object catchObj, String errorCode, String format, Object... args) {
        return new QLRuntimeException(null, String.format(format, args), errorCode);
    }
}
