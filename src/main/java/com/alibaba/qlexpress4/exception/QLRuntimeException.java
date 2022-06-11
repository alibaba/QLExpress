package com.alibaba.qlexpress4.exception;

import com.alibaba.qlexpress4.parser.Token;

public class QLRuntimeException extends QLException {

    private final Object attachment;

    protected QLRuntimeException(String message, int lineNo, int colNo, String errLexeme,
                                 String reason, String errorCode, String snippet) {
        super(message, lineNo, colNo, errLexeme, reason, errorCode, snippet);
        this.attachment = null;
    }

    protected QLRuntimeException(Object attachment, String message, int lineNo, int colNo, String errLexeme,
                                 String reason, String errorCode, String snippet) {
        super(message, lineNo, colNo, errLexeme, reason, errorCode, snippet);
        this.attachment = attachment;
    }

    public Object getAttachment() {
        return attachment;
    }
}
