package com.alibaba.qlexpress4.exception;

public class QLException extends RuntimeException {

    private final int lineNo;

    private final int colNo;

    private final String errLexeme;

    private final String errorCode;

    private final String reason;

    /**
     * snippet near error position
     */
    private final String snippet;

    protected QLException(String message, int lineNo, int colNo, String errLexeme,
                          String errorCode, String reason, String snippet) {
        super(message);
        this.lineNo = lineNo;
        this.colNo = colNo;
        this.errLexeme = errLexeme;
        this.errorCode = errorCode;
        this.reason = reason;
        this.snippet = snippet;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getColNo() {
        return colNo;
    }

    public String getErrLexeme() {
        return errLexeme;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }

    public String getSnippet() {
        return snippet;
    }

    public static QLSyntaxException reportScannerErr(String script, int tokenPos,
                                                     int tokenLine, int tokenCol, String lexeme,
                                                     String errorCode, String reason) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(
                script, tokenPos, tokenLine, tokenCol, lexeme, errorCode, reason);
        return new QLSyntaxException(exMessage.getMessage(), tokenLine, tokenCol, lexeme,
                errorCode, reason, exMessage.getSnippet());
    }

    public static QLRuntimeException reportRuntimeErrWithAttach(String script, int tokenPos, int line,
                                                                int col, String lexeme,
                                                                String errorCode, String reason, Object catchObj) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(script, tokenPos, line, col, lexeme, errorCode, reason);
        return  new QLRuntimeException(catchObj, exMessage.getMessage(), line, col, lexeme, errorCode, reason, exMessage.getSnippet());
    }
}
