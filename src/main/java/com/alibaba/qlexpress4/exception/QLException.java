package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.exception.lsp.Diagnostic;
import com.alibaba.qlexpress4.exception.lsp.Position;
import com.alibaba.qlexpress4.exception.lsp.Range;

public class QLException extends RuntimeException {

    private final Diagnostic diagnostic;

    protected QLException(String message, Diagnostic diagnostic) {
        super(message);
        this.diagnostic = diagnostic;
    }

    public Diagnostic getDiagnostic() {
        return diagnostic;
    }

    public int getPos() {
        return diagnostic.getPos();
    }

    public String getReason() {
        return diagnostic.getMessage();
    }

    /**
     * @return line no based 1
     */
    public int getLineNo() {
        return diagnostic.getRange().getStart().getLine() + 1;
    }

    /**
     * @return col no based 1
     */
    public int getColNo() {
        return diagnostic.getRange().getStart().getCharacter() + 1;
    }

    public String getErrLexeme() {
        return diagnostic.getLexeme();
    }

    public String getErrorCode() {
        return diagnostic.getCode();
    }

    public static QLSyntaxException reportScannerErr(String script, int tokenStartPos,
                                                     int line, int col, String lexeme,
                                                     String errorCode, String reason) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(
                script, tokenStartPos, line, col, lexeme, errorCode, reason);
        Diagnostic diagnostic = toDiagnostic(tokenStartPos, line, col, lexeme, errorCode, reason, exMessage.getSnippet());
        return new QLSyntaxException(exMessage.getMessage(), diagnostic);
    }

    public static QLRuntimeException reportRuntimeErrWithAttach(String script, int tokenStartPos, int line,
                                                                int col, String lexeme,
                                                                String errorCode, String reason, Object catchObj) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(script, tokenStartPos, line, col, lexeme, errorCode, reason);
        Diagnostic diagnostic = toDiagnostic(tokenStartPos, line, col, lexeme, errorCode, reason, exMessage.getSnippet());
        return  new QLRuntimeException(catchObj, exMessage.getMessage(), diagnostic);
    }

    private static Diagnostic toDiagnostic(int startPos, int line, int col, String lexeme, String errorCode, String reason, String snippet) {
        int zeroBasedLine = line - 1;
        int zeroBasedCol = col - 1;
        Position start = new Position(zeroBasedLine, zeroBasedCol);
        Position end = new Position(zeroBasedLine, zeroBasedCol + lexeme.length());
        Range range = new Range(start, end);
        return new Diagnostic(startPos, range, lexeme, errorCode, reason, snippet);
    }
}
