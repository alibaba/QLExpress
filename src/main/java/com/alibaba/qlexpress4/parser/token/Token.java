package com.alibaba.qlexpress4.parser.token;

import java.util.Objects;

/**
 * Represents a lexed token with source location tracking.
 *
 * <p>A token is the smallest unit of meaning in the source code, produced by the lexer
 * and consumed by the parser. Each token contains:
 * <ul>
 *   <li>type: The TokenType enum value categorizing this token</li>
 *   <li>value: The actual text content of the token</li>
 *   <li>line: The starting line number in the source (1-based)</li>
 *   <li>column: The starting column number in the source (1-based)</li>
 *   <li>startIndex: The starting character position in the source (0-based)</li>
 *   <li>source: The source file or string identifier (optional)</li>
 * </ul>
 */
public class Token {
    private final TokenType type;

    private final String value;

    private final int line;

    private final int column;

    private final int startIndex;

    private final String source;

    private final int endLine;

    private final int endColumn;

    private final int length;
    
    /**
     * Creates a new token with full location information.
     *
     * @param type the token type
     * @param value the actual text content of the token
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     * @param source the source file or string identifier (may be null)
     */
    public Token(TokenType type, String value, int line, int column, String source) {
        this(type, value, line, column, -1, source, line, column + (value != null ? value.length() : 0));
    }

    /**
     * Creates a new token with full location information including character position.
     *
     * @param type the token type
     * @param value the actual text content of the token
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     * @param startIndex the starting character position in source (0-based)
     * @param source the source file or string identifier (may be null)
     */
    public Token(TokenType type, String value, int line, int column, int startIndex, String source) {
        this(type, value, line, column, startIndex, source, line, column + (value != null ? value.length() : 0));
    }
    
    /**
     * Creates a new token with full start and end location information.
     *
     * @param type the token type
     * @param value the actual text content of the token
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     * @param startIndex the starting character position in source (0-based)
     * @param source the source file or string identifier (may be null)
     * @param endLine the ending line number (1-based)
     * @param endColumn the ending column number (1-based, exclusive)
     */
    public Token(TokenType type, String value, int line, int column, int startIndex, String source, int endLine, int endColumn) {
        this.type = Objects.requireNonNull(type, "Token type cannot be null");
        this.value = value;
        this.line = line;
        this.column = column;
        this.startIndex = startIndex;
        this.source = source;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.length = value != null ? value.length() : 0;
    }
    
    /**
     * Creates a new token without source information.
     *
     * @param type the token type
     * @param value the actual text content of the token
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     */
    public Token(TokenType type, String value, int line, int column) {
        this(type, value, line, column, -1, null);
    }

    /**
     * Creates a new token with position but without source information.
     *
     * @param type the token type
     * @param value the actual text content of the token
     * @param line the starting line number (1-based)
     * @param column the starting column number (1-based)
     * @param startIndex the starting character position in source (0-based)
     */
    public Token(TokenType type, String value, int line, int column, int startIndex) {
        this(type, value, line, column, startIndex, null);
    }
    
    /**
     * Returns the token type.
     *
     * @return the token type
     */
    public TokenType getType() {
        return type;
    }
    
    /**
     * Returns the actual text content of the token.
     *
     * @return the token text, or null if not applicable
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Returns the starting line number of this token.
     *
     * @return the starting line number (1-based)
     */
    public int getLine() {
        return line;
    }
    
    /**
     * Returns the starting column number of this token.
     *
     * @return the starting column number (1-based)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the starting character position of this token in the source.
     *
     * @return the starting character position (0-based), or -1 if not available
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Returns the source file or string identifier.
     *
     * @return the source identifier, or null if not available
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Returns the ending line number of this token.
     *
     * @return the ending line number (1-based)
     */
    public int getEndLine() {
        return endLine;
    }
    
    /**
     * Returns the ending column number of this token (exclusive).
     *
     * @return the ending column number (1-based, exclusive)
     */
    public int getEndColumn() {
        return endColumn;
    }
    
    /**
     * Returns the length of this token in characters.
     *
     * @return the token length
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Returns a human-readable location string for this token.
     *
     * @return a string like "source:1:5" or "1:5" if no source is set
     */
    public String getLocationString() {
        if (source != null && !source.isEmpty()) {
            return source + ":" + line + ":" + column;
        }
        return line + ":" + column;
    }
    
    /**
     * Returns true if this token is of the specified type.
     *
     * @param type the token type to check
     * @return true if this token is of the specified type
     */
    public boolean is(TokenType type) {
        return this.type == type;
    }
    
    /**
     * Returns true if this token is a keyword.
     *
     * @return true if this token is a keyword
     */
    public boolean isKeyword() {
        return type.isKeyword();
    }
    
    /**
     * Returns true if this token is an operator.
     *
     * @return true if this token is an operator
     */
    public boolean isOperator() {
        return type.isOperator();
    }
    
    /**
     * Returns true if this token is a literal.
     *
     * @return true if this token is a literal
     */
    public boolean isLiteral() {
        return type.isLiteral();
    }
    
    /**
     * Returns true if this token is a delimiter.
     *
     * @return true if this token is a delimiter
     */
    public boolean isDelimiter() {
        return type.isDelimiter();
    }
    
    /**
     * Returns true if this token is the EOF token.
     *
     * @return true if this token is EOF
     */
    public boolean isEOF() {
        return type == TokenType.EOF;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Token token = (Token)o;
        return line == token.line && column == token.column && startIndex == token.startIndex && endLine == token.endLine && endColumn == token.endColumn
            && type == token.type && Objects.equals(value, token.value) && Objects.equals(source, token.source);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, value, line, column, startIndex, source, endLine, endColumn);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token{");
        sb.append(type);
        if (value != null) {
            sb.append(" '").append(value).append("'");
        }
        sb.append(" @ ").append(getLocationString());
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Creates a new Token with the same type and value but different position.
     *
     * @param line the new line number
     * @param column the new column number
     * @return a new token with the updated position
     */
    public Token withPosition(int line, int column) {
        return new Token(type, value, line, column, -1, source, endLine + (line - this.line), endColumn);
    }

    /**
     * Creates a new Token with the same type and value but different position including character index.
     *
     * @param line the new line number
     * @param column the new column number
     * @param startIndex the new character position in source
     * @return a new token with the updated position
     */
    public Token withPosition(int line, int column, int startIndex) {
        return new Token(type, value, line, column, startIndex, source, endLine + (line - this.line), endColumn);
    }
    
    /**
     * Creates a new Token with the same type, value, and position but different source.
     *
     * @param source the new source identifier
     * @return a new token with the updated source
     */
    public Token withSource(String source) {
        return new Token(type, value, line, column, startIndex, source, endLine, endColumn);
    }

    /**
     * Creates a new Token with a different value but same type and position.
     *
     * @param value the new value
     * @return a new token with the updated value
     */
    public Token withValue(String value) {
        return new Token(type, value, line, column, startIndex, source, endLine,
            endColumn + (value != null ? value.length() - this.length : 0));
    }
    
    /**
     * Creates an EOF token.
     *
     * @param line the line number where EOF occurs
     * @param column the column number where EOF occurs
     * @param source the source identifier
     * @return an EOF token
     */
    public static Token eof(int line, int column, String source) {
        return new Token(TokenType.EOF, null, line, column, source);
    }
    
    /**
     * Creates an EOF token without source.
     *
     * @param line the line number where EOF occurs
     * @param column the column number where EOF occurs
     * @return an EOF token
     */
    public static Token eof(int line, int column) {
        return eof(line, column, null);
    }
}
