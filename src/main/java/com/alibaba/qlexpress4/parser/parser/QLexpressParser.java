package com.alibaba.qlexpress4.parser.parser;

import com.alibaba.qlexpress4.parser.token.Token;
import com.alibaba.qlexpress4.parser.token.TokenType;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.parser.ast.BlockNode;
import com.alibaba.qlexpress4.parser.ast.StatementNode;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Hand-written recursive descent parser for QLExpress language.
 *
 * <p>This parser consumes a stream of tokens from the lexer and produces an AST.
 * It implements:
 * <ul>
 *   <li>Token stream management with lookahead</li>
 *   <li>Error handling with line/column information</li>
 *   <li>Basic parsing methods (peek, consume, expect, match)</li>
 * </ul>
 */
public class QLexpressParser {
    private final List<Token> tokens;
    private int position;

    /**
     * Creates a new parser for the given token stream.
     *
     * @param tokens the token stream from the lexer
     */
    public QLexpressParser(List<Token> tokens) {
        this.tokens = tokens != null ? tokens : Collections.emptyList();
        this.position = 0;
    }

    /**
     * Parses the entire token stream as a program.
     *
     * @return the root ProgramNode of the AST
     * @throws ParseException if parsing fails
     */
    public ProgramNode parseProgram() throws ParseException {
        int line = 1, column = 1;
        String source = null;
        if (!tokens.isEmpty()) {
            Token first = tokens.get(0);
            line = first.getLine();
            column = first.getColumn();
            source = first.getSource();
        }

        // Parse statements until EOF
        // This is a placeholder - full implementation in later US
        List<StatementNode> statements = new ArrayList<>();

        return new ProgramNode(line, column, source, statements);
    }

    /**
     * Returns the current token without consuming it.
     *
     * @return the current token, or null if at end of stream
     */
    public Token peek() {
        return peek(0);
    }

    /**
     * Returns the token at offset from current position without consuming it.
     *
     * @param offset the offset from current position (0 = current, 1 = next, etc.)
     * @return the token at offset, or null if at end of stream
     */
    public Token peek(int offset) {
        int pos = position + offset;
        if (pos >= 0 && pos < tokens.size()) {
            return tokens.get(pos);
        }
        return null;
    }

    /**
     * Consumes and returns the current token.
     *
     * @return the consumed token
     * @throws ParseException if at end of stream
     */
    public Token consume() throws ParseException {
        if (position >= tokens.size()) {
            throw error("Unexpected end of input");
        }
        return tokens.get(position++);
    }

    /**
     * Checks if the current token is of the expected type and consumes it.
     *
     * @param expected the expected token type
     * @return the consumed token
     * @throws ParseException if the current token is not of the expected type
     */
    public Token expect(TokenType expected) throws ParseException {
        Token current = peek();
        if (current == null) {
            throw error("Unexpected end of input, expected " + expected);
        }
        if (current.getType() != expected) {
            throw error("Expected " + expected + " but found " + current.getType());
        }
        return consume();
    }

    /**
     * Checks if the current token matches any of the given types.
     *
     * @param types the token types to check
     * @return true if the current token matches any of the types
     */
    public boolean match(TokenType... types) {
        Token current = peek();
        if (current == null) {
            return false;
        }
        for (TokenType type : types) {
            if (current.getType() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current token is of the specified type.
     *
     * @param type the token type to check
     * @return true if the current token is of the specified type
     */
    public boolean is(TokenType type) {
        Token current = peek();
        return current != null && current.getType() == type;
    }

    /**
     * Creates a parse exception at the current position.
     *
     * @param message the error message
     * @return the parse exception
     */
    public ParseException error(String message) {
        Token current = peek();
        if (current != null) {
            return new ParseException(message, current.getLine(), current.getColumn(), current.getSource());
        }
        return new ParseException(message, -1, -1, null);
    }

    /**
     * Returns the current position in the token stream.
     *
     * @return the current position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Checks if we are at the end of the token stream.
     *
     * @return true if at end of stream
     */
    public boolean isEOF() {
        return position >= tokens.size() || (peek() != null && peek().getType() == TokenType.EOF);
    }

    /**
     * Parse exception with source location information.
     */
    public static class ParseException extends Exception {
        private final int line;
        private final int column;
        private final String source;

        public ParseException(String message, int line, int column, String source) {
            super(message);
            this.line = line;
            this.column = column;
            this.source = source;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public String getSource() {
            return source;
        }

        public String getLocationString() {
            if (source != null && !source.isEmpty()) {
                return source + ":" + line + ":" + column;
            }
            return line + ":" + column;
        }

        @Override
        public String toString() {
            return getMessage() + " at " + getLocationString();
        }
    }
}
