package com.alibaba.qlexpress4.exception;

public class MockErrorReporter implements ErrorReporter {
    @Override
    public QLRuntimeException report(String errorCode, String reason) {
        return new QLRuntimeException(reason);
    }

    @Override
    public QLRuntimeException report(String errorCode, String format, Object... args) {
        return new QLRuntimeException(String.format(format, args));
    }
}
