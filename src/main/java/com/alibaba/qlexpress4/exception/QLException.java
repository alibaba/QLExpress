package com.alibaba.qlexpress4.exception;

import java.text.MessageFormat;

public class QLException extends RuntimeException {

    private static final String REPORT_TEMPLATE = "[Error {0}: {1}]\n[Near: {2}]\n{3}\n[Line: {4}, Column: {5}]";

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
        return QLException.reportErr(script, tokenPos, tokenLine, tokenCol, lexeme,  errorCode, reason,
                QLSyntaxException::new);
    }

    public static QLRuntimeException reportRuntimeErr(String script, int tokenPos, int line, int col, String lexeme,
            String errorCode, String reason) {
        return reportErr(script, tokenPos, line, col, lexeme, errorCode, reason, QLRuntimeException::new);
    }

    public static QLRuntimeException reportRuntimeErrWithAttach(String script, int tokenPos, int line,
                                                                int col, String lexeme,
                                                                String errorCode, String reason, Object catchObj) {
        return reportErr(script, tokenPos, line, col, lexeme, errorCode, reason,
                (message, lineNo, colNo, errLexeme, errorCode0, reason0, snippet) ->
                        new QLRuntimeException(catchObj, message, lineNo, colNo, errLexeme, errorCode0, reason0, snippet));
    }

    private static <T extends QLException> T reportErr(String script, int tokenPos,
                                                       int tokenLine, int tokenCol, String lexeme, String errorCode,
                                                       String reason, ExceptionFactory<T> exceptionFactory) {
        int tokenStartPos = tokenPos - lexeme.length();
        int startReportPos = Math.max(tokenStartPos - 10, 0);
        int endReportPos = Math.min(tokenPos + 10, script.length());

        StringBuilder snippetBuilder = new StringBuilder();
        if (startReportPos > 0) {
            snippetBuilder.append("...");
        }
        for (int i = startReportPos; i < endReportPos; i++) {
            char codeChar = script.charAt(i);
            snippetBuilder.append(codeChar < ' '? ' ': codeChar);
        }
        if (endReportPos < script.length()) {
            snippetBuilder.append("...");
        }

        StringBuilder carteBuilder = new StringBuilder().append("       ");
        if (startReportPos > 0) {
            carteBuilder.append("   ");
        }
        for (int i = startReportPos; i < tokenStartPos; i++) {
            carteBuilder.append(' ');
        }
        for (int i = 0; i < lexeme.length(); i++) {
            carteBuilder.append('^');
        }

        String snippet = snippetBuilder.toString();
        String message = MessageFormat.format(REPORT_TEMPLATE, errorCode, reason, snippet,
                carteBuilder.toString(), tokenLine, tokenCol);
        return exceptionFactory.newException(message, tokenLine, tokenCol, lexeme, errorCode, reason, snippet);
    }
}
