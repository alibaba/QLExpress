package com.alibaba.qlexpress4.exception.lsp;

/**
 * Author: DQinYuan
 */
public class Diagnostic {

    /**
     * start position in script
     */
    private final int pos;

    /**
     * The range at which the message applies.
     */
    private final Range range;

    /**
     * The diagnostic's code, which might appear in the user interface.
     */
    private final String code;

    /**
     * The diagnostic's message.
     */
    private final String message;

    /**
     * snippet near error position
     */
    private final String snippet;

    public Diagnostic(int pos, Range range, String code, String message, String snippet) {
        this.pos = pos;
        this.range = range;
        this.code = code;
        this.message = message;
        this.snippet = snippet;
    }

    public int getPos() {
        return pos;
    }

    public Range getRange() {
        return range;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSnippet() {
        return snippet;
    }
}
