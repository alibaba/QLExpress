package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public class DefaultErrReporter implements ErrorReporter {

    private final String script;
    private final int tokenPos;
    private final int line;
    private final int col;
    private final String lexeme;

    public DefaultErrReporter(String script, int tokenPos, int line, int col, String lexeme) {
        this.script = script;
        this.tokenPos = tokenPos;
        this.line = line;
        this.col = col;
        this.lexeme = lexeme;
    }

    @Override
    public QLRuntimeException report(Object catchObj, String errorCode, String reason) {
        return QLException.reportRuntimeErrWithAttach(script, tokenPos, line, col, lexeme,
                errorCode, reason, catchObj);
    }

    @Override
    public QLRuntimeException report(String errorCode, String reason) {
        return QLException.reportRuntimeErr(script, tokenPos, line, col, lexeme, errorCode, reason);
    }

    @Override
    public QLRuntimeException reportFormat(String errorCode, String format, Object... args) {
        return QLException.reportRuntimeErr(script, tokenPos, line, col, lexeme, errorCode, String.format(format, args));
    }
}
