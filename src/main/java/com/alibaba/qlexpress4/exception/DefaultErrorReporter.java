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
    public QLRuntimeException report(String errorCode, String reason) {
        return QLException.reportRuntimeErr(script, reportToken, errorCode, reason);
    }
}
