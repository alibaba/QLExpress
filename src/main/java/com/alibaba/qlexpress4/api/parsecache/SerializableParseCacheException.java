package com.alibaba.qlexpress4.api.parsecache;

import com.alibaba.qlexpress4.exception.ExMessageUtil;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.lsp.Diagnostic;
import com.alibaba.qlexpress4.exception.lsp.Position;
import com.alibaba.qlexpress4.exception.lsp.Range;

public class SerializableParseCacheException extends QLException {
    
    public SerializableParseCacheException(String script, SerializableSource source, String errorCode, String reason) {
        super(buildMessage(script, normalizeSource(script, source), errorCode, reason),
            buildDiagnostic(script, normalizeSource(script, source), errorCode, reason));
    }
    
    private static SerializableSource normalizeSource(String script, SerializableSource source) {
        SerializableSource normalized = new SerializableSource();
        int scriptLength = script == null ? 0 : script.length();
        int start = source == null ? 0 : source.getStart();
        normalized.setStart(Math.max(0, Math.min(start, scriptLength)));
        normalized.setLine(source == null || source.getLine() <= 0 ? 1 : source.getLine());
        normalized.setCol(source == null || source.getCol() < 0 ? 0 : source.getCol());
        normalized.setLexeme(source == null || source.getLexeme() == null ? "" : source.getLexeme());
        return normalized;
    }
    
    private static String buildMessage(String script, SerializableSource source, String errorCode, String reason) {
        return ExMessageUtil.format(script == null ? ""
            : script, source.getStart(), source.getLine(), source.getCol() + 1, source.getLexeme(), errorCode, reason)
            .getMessage();
    }
    
    private static Diagnostic buildDiagnostic(String script, SerializableSource source, String errorCode,
        String reason) {
        ExMessageUtil.ExMessage exMessage = ExMessageUtil.format(script == null ? ""
            : script, source.getStart(), source.getLine(), source.getCol() + 1, source.getLexeme(), errorCode, reason);
        Position start = new Position(source.getLine() - 1, source.getCol());
        Position end = new Position(source.getLine() - 1, source.getCol() + source.getLexeme().length());
        return new Diagnostic(source.getStart(), new Range(start, end), source.getLexeme(), errorCode, reason,
            exMessage.getSnippet());
    }
}
