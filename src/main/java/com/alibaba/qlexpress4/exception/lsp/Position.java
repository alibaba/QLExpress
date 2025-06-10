package com.alibaba.qlexpress4.exception.lsp;

/**
 * Author: DQinYuan
 */
public class Position {

    /**
     * Line position in a document (zero-based).
     */
    private final int line;

    /**
     * Character offset on a line in a document (zero-based).
     * If the character value is greater than the line length it defaults back
     * to the line length.
     */
    private final int character;

    public Position(int line, int character) {
        this.line = line;
        this.character = character;
    }

    public int getLine() {
        return line;
    }

    public int getCharacter() {
        return character;
    }
}
