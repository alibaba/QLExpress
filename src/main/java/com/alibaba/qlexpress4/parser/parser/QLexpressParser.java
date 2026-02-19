package com.alibaba.qlexpress4.parser.parser;

import com.alibaba.qlexpress4.parser.token.Token;
import com.alibaba.qlexpress4.parser.token.TokenType;
import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.parser.ast.ExpressionNode;

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

    // ==================== Expression Parsing ====================

    /**
     * Parses a primary expression.
     * <p>
     * Primary expressions include:
     * <ul>
     *   <li>Literals (numbers, strings, booleans, null)</li>
     *   <li>Identifiers (variable names)</li>
     *   <li>Parenthesized expressions</li>
     * </ul>
     *
     * @return the parsed expression node
     * @throws ParseException if parsing fails
     */
    public ExpressionNode parsePrimary() throws ParseException {
        Token current = peek();
        if (current == null) {
            throw error("Unexpected end of input, expected expression");
        }

        switch (current.getType()) {
            case INTEGER_LITERAL:
            case FLOATING_POINT_LITERAL:
            case INTEGER_OR_FLOATING_LITERAL:
            case QUOTE_STRING_LITERAL:
            case TRUE:
            case FALSE:
            case NULL:
                return parseLiteral();

            case ID:
                return parseIdentifier();

            case LPAREN:
                return parseParenthesizedExpression();

            default:
                throw error("Expected expression but found " + current.getType());
        }
    }

    /**
     * Parses a literal expression.
     *
     * @return the LiteralNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseLiteral() throws ParseException {
        Token token = consume();
        Object value;

        switch (token.getType()) {
            case INTEGER_LITERAL:
                value = parseIntegerLiteral(token.getValue());
                break;
            case FLOATING_POINT_LITERAL:
                value = parseFloatLiteral(token.getValue());
                break;
            case INTEGER_OR_FLOATING_LITERAL:
                // Need to determine if this is actually an integer or float
                String strValue = token.getValue();
                if (strValue.contains(".") || strValue.contains("e") || strValue.contains("E") ||
                    strValue.endsWith("f") || strValue.endsWith("F") ||
                    strValue.endsWith("d") || strValue.endsWith("D")) {
                    value = parseFloatLiteral(strValue);
                } else {
                    value = parseIntegerLiteral(strValue);
                }
                break;
            case QUOTE_STRING_LITERAL:
                value = parseStringLiteral(token.getValue());
                break;
            case TRUE:
                value = Boolean.TRUE;
                break;
            case FALSE:
                value = Boolean.FALSE;
                break;
            case NULL:
                value = null;
                break;
            default:
                throw error("Expected literal but found " + token.getType());
        }

        return new LiteralNode(token.getLine(), token.getColumn(), token.getSource(), value);
    }

    /**
     * Parses an identifier expression.
     *
     * @return the IdentifierNode
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseIdentifier() throws ParseException {
        Token token = expect(TokenType.ID);
        return new IdentifierNode(token.getLine(), token.getColumn(), token.getSource(), token.getValue());
    }

    /**
     * Parses a parenthesized expression.
     *
     * @return the inner expression node
     * @throws ParseException if parsing fails
     */
    private ExpressionNode parseParenthesizedExpression() throws ParseException {
        Token lparen = expect(TokenType.LPAREN);

        // Skip newlines inside parentheses
        skipNewlines();

        ExpressionNode expr = parsePrimary();

        // Skip newlines before closing paren
        skipNewlines();

        expect(TokenType.RPAREN);

        return expr;
    }

    /**
     * Parses an integer literal from string value.
     * Handles hex (0x), binary (0b), octal (0), and decimal formats.
     *
     * @param value the string value from the token
     * @return the parsed integer (as Long or Integer)
     */
    private Object parseIntegerLiteral(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        value = value.replace("_", ""); // Remove digit separators

        // Check for type suffix
        boolean isLong = false;
        if (value.endsWith("l") || value.endsWith("L")) {
            isLong = true;
            value = value.substring(0, value.length() - 1);
        }

        long parsedValue;
        if (value.startsWith("0x") || value.startsWith("0X")) {
            // Hexadecimal
            parsedValue = Long.parseLong(value.substring(2), 16);
        } else if (value.startsWith("0b") || value.startsWith("0B")) {
            // Binary
            parsedValue = Long.parseLong(value.substring(2), 2);
        } else if (value.length() > 1 && value.charAt(0) == '0') {
            // Octal
            parsedValue = Long.parseLong(value, 8);
        } else {
            // Decimal
            parsedValue = Long.parseLong(value);
        }

        if (isLong) {
            return parsedValue;
        } else {
            // Return as Integer if it fits, otherwise Long
            if (parsedValue >= Integer.MIN_VALUE && parsedValue <= Integer.MAX_VALUE) {
                return (int) parsedValue;
            }
            return parsedValue;
        }
    }

    /**
     * Parses a floating point literal from string value.
     *
     * @param value the string value from the token
     * @return the parsed number (as Double or Float)
     */
    private Object parseFloatLiteral(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }

        value = value.replace("_", ""); // Remove digit separators

        // Check for type suffix
        boolean isFloat = value.endsWith("f") || value.endsWith("F");
        boolean isDouble = value.endsWith("d") || value.endsWith("D");
        if (isFloat || isDouble) {
            value = value.substring(0, value.length() - 1);
        }

        double parsedValue = Double.parseDouble(value);

        if (isFloat) {
            return (float) parsedValue;
        } else if (isDouble) {
            return parsedValue;
        } else {
            // Default to Double if no suffix
            return parsedValue;
        }
    }

    /**
     * Parses a string literal, processing escape sequences.
     *
     * @param value the string value from the token (including quotes)
     * @return the processed string without quotes
     */
    private String parseStringLiteral(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        // Remove surrounding quotes
        if (value.length() >= 2) {
            char quote = value.charAt(0);
            if (quote == '\'' || quote == '"') {
                value = value.substring(1, value.length() - 1);
            }
        }

        // Process escape sequences
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(++i);
                switch (next) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case '\'': sb.append('\''); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default:
                        // Unknown escape, keep as-is
                        sb.append('\\').append(next);
                        break;
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Skips newline tokens if present.
     */
    private void skipNewlines() {
        while (match(TokenType.NEWLINE)) {
            try {
                consume();
            } catch (ParseException e) {
                // Should not happen since we checked with match()
                break;
            }
        }
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
