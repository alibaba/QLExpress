package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public class DefaultErrReporter implements ErrorReporter {
    
    private final String script;
    
    private final int tokenStartPos;
    
    private final int line;
    
    private final int col;
    
    private final String lexeme;
    
    public DefaultErrReporter(String script, int tokenStartPos, int line, int col, String lexeme) {
        this.script = script;
        this.tokenStartPos = tokenStartPos;
        this.line = line;
        this.col = col;
        this.lexeme = lexeme;
    }
    
    @Override
    public QLRuntimeException reportFormatWithCatch(Object catchObj, String errorCode, String format, Object... args) {
        return QLException.reportRuntimeErrWithAttach(script,
            tokenStartPos,
            line,
            col,
            lexeme,
            errorCode,
            String.format(format, args),
            catchObj);
    }
}
