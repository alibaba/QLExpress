package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.parser.Token;

/**
 * Author: DQinYuan
 */
public class DefaultErrorReporter implements ErrorReporter {

    private final String script;

    private final Token reportToken;

    public DefaultErrorReporter(String script, Token reportToken) {
        this.script = script;
        this.reportToken = reportToken;
    }

    @Override
    public QLRuntimeException report(Object attachment, String errorCode, String reason) {
        return QLException.reportRuntimeErrWithAttach(script, reportToken, errorCode, reason, attachment);
    }

    @Override
    public QLRuntimeException report(String errorCode, String reason) {
        return QLException.reportRuntimeErr(script, reportToken, errorCode, reason);
    }

    @Override
    public QLRuntimeException report(String errorCode, String format, Object... args) {
        return report(errorCode, String.format(format, args));
    }
}
