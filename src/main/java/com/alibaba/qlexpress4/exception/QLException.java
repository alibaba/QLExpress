package com.alibaba.qlexpress4.exception;

public class QLException extends RuntimeException {

    private final int pos;

    private final int lineNo;

    private final int colNo;

    private final String errLexeme;

    private final String errorCode;

    private final String reason;

    /**
     * snippet near error position
     */
    private final String snippet;

    protected QLException(String message, int pos, int lineNo, int colNo, String errLexeme,
                          String errorCode, String reason, String snippet) {
        super(message);
        this.pos = pos;
        this.lineNo = lineNo;
        this.colNo = colNo;
        this.errLexeme = errLexeme;
        this.errorCode = errorCode;
        this.reason = reason;
        this.snippet = snippet;
    }

    public int getPos() {
        return pos;
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

    public static QLSyntaxException reportScannerErr(String script, int tokenStartPos,
                                                     int tokenLine, int tokenCol, String lexeme,
                                                     String errorCode, String reason) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(
                script, tokenStartPos, tokenLine, tokenCol, lexeme, errorCode, reason);
        return new QLSyntaxException(exMessage.getMessage(), tokenStartPos, tokenLine, tokenCol, lexeme,
                errorCode, reason, exMessage.getSnippet());
    }

    public static QLRuntimeException reportRuntimeErrWithAttach(String script, int tokenStartPos, int line,
                                                                int col, String lexeme,
                                                                String errorCode, String reason, Object catchObj) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(script, tokenStartPos, line, col, lexeme, errorCode, reason);
        return  new QLRuntimeException(catchObj, exMessage.getMessage(), tokenStartPos, line, col,
                lexeme, errorCode, reason, exMessage.getSnippet());
    }
}
