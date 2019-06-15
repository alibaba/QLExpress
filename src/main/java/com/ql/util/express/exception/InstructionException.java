package com.ql.util.express.exception;

import java.io.Serializable;

public class InstructionException extends Exception implements Serializable {
    private static final long serialVersionUID = 5718252068122092635L;

    private Integer line;

    public InstructionException(Integer line, String message, Throwable cause) {
        super(message, cause);

        this.line = line;
    }

    public Integer getLine() {
        return line;
    }
}
