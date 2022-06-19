package com.alibaba.qlexpress4.exception;

public class MockErrorReporter implements ErrorReporter {

    @Override
    public QLRuntimeException report(Object attachment, String errorCode, String reason) {
        return new QLRuntimeException(attachment, reason, errorCode);
    }

    @Override
    public QLRuntimeException report(String errorCode, String reason) {
        return new QLRuntimeException(null, reason, errorCode);
    }

    @Override
    public QLRuntimeException report(String errorCode, String format, Object... args) {
        return new QLRuntimeException(null, String.format(format, args), errorCode);
    }
}
