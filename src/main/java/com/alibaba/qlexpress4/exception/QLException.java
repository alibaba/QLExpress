package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.parser.Token;

import java.text.MessageFormat;

public abstract class QLException extends RuntimeException {

    private static final String REPORT_TEMPLATE = "[Error: {0}]\n[Near: {1}]\n{2}\n[Line: {3}, Column: {4}]";

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

    public static QLSyntaxException reportParserErr(String script, Token token, String errorCode, String reason) {
        return reportErrWithToken(script, token, errorCode, reason, QLSyntaxException::new);
    }

    public static QLSyntaxException reportScannerErr(String script, int tokenPos,
                                                     int tokenLine, int tokenCol, String lexeme,
                                                     String errorCode, String reason) {
        return QLException.reportErr(script, tokenPos, tokenLine, tokenCol, lexeme,  errorCode, reason,
                QLSyntaxException::new);
    }

    public static QLRuntimeException reportRuntimeErr(String script, Token token, String errorCode, String reason) {
        return reportErrWithToken(script, token, errorCode, reason, QLRuntimeException::new);
    }

    private static <T extends QLException> T reportErrWithToken(String script, Token token, String errCode,
                                                                String reason, ExceptionFactory<T> exceptionFactory) {
        return reportErr(script, token.getPos(), token.getLine(), token.getCol(), token.getLexeme(), errCode, reason,
                exceptionFactory);
    }

    private static <T extends QLException> T reportErr(String script, int tokenPos,
                                                       int tokenLine, int tokenCol, String lexeme, String errorCode,
                                                       String reason, ExceptionFactory<T> exceptionFactory) {
        int tokenStartPos = tokenPos - lexeme.length();
        int startReportPos = Math.max(tokenStartPos - 10, 0);
        int endReportPos = Math.min(tokenPos + 10, script.length());

        StringBuilder snippetBuilder = new StringBuilder();
        for (int i = startReportPos; i < endReportPos; i++) {
            char codeChar = script.charAt(i);
            snippetBuilder.append(codeChar < ' '? ' ': codeChar);
        }

        StringBuilder carteBuilder = new StringBuilder().append("       ");
        for (int i = startReportPos; i < tokenStartPos; i++) {
            carteBuilder.append(' ');
        }
        for (int i = 0; i < lexeme.length(); i++) {
            carteBuilder.append('^');
        }

        int colNo = tokenCol - lexeme.length();
        String snippet = snippetBuilder.toString();
        String message = MessageFormat.format(REPORT_TEMPLATE, reason, snippet,
                carteBuilder.toString(), tokenLine, colNo);
        return exceptionFactory.newException(message, tokenLine, colNo, lexeme, errorCode, reason, snippet);
    }
}
