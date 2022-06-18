package com.alibaba.qlexpress4.parser;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    /**
     * token end position + 1, start from 0
     */
    private final int pos;
    /**
     * start from 1
     * without cross line token in qlexpress
     */
    private final int line;
    /**
     * start from 1
     * token end col + 1
     */
    private final int col;

    Token(TokenType type, String lexeme, int pos, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = null;
        this.pos = pos;
        this.line = line;
        this.col = col;
    }

    Token(TokenType type, String lexeme, Object literal, int pos, int line, int col) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.pos = pos;
        this.line = line;
        this.col = col;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getPos() {
        return pos;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public String toString() {
        return pos + ":Line " + line + ":Col " + (col - 1) + " " + type + " " + lexeme + " " +
                (literal == null? "": literal);
    }

}
