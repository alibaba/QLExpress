package com.alibaba.qlexpress4.exception;

public class QLRuntimeException extends QLException {

    private final Object attachment;

    /**
     * Visible for test
     * @param attachment
     * @param reason
     * @param errorCode
     */
    protected QLRuntimeException(Object attachment, String reason, String errorCode) {
        super("", 0, 0, "", reason, errorCode, "");
        this.attachment = attachment;
    }

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
