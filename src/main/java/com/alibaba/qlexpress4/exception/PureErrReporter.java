package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public class PureErrReporter implements ErrorReporter {
    
    public static PureErrReporter INSTANCE = new PureErrReporter();
    
    private PureErrReporter() {
    }
    
    @Override
    public QLRuntimeException reportFormatWithCatch(Object catchObj, String errorCode, String format, Object... args) {
        return new QLRuntimeException(null, String.format(format, args), errorCode);
    }
}
