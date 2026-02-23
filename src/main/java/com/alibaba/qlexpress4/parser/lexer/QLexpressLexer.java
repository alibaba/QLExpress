package com.alibaba.qlexpress4.parser.lexer;

import com.alibaba.qlexpress4.parser.token.Token;
import com.alibaba.qlexpress4.parser.token.TokenType;
import com.alibaba.qlexpress4.common.InterpolationMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Hand-written lexer for QLExpress language.
 *
 * <p>This lexer tokenizes the input source code into a stream of tokens for parsing.
 * It supports:
 * <ul>
 *   <li>Identifiers (variable names, function names)</li>
 *   <li>Literals (integers, floats, strings, booleans)</li>
 *   <li>Line and column tracking for error reporting</li>
 *   <li>Interpolation modes (SCRIPT, VARIABLE, DISABLE)</li>
 *   <li>Custom selector tokens (${}, #{})</li>
 *   <li>Strict/non-strict newline modes</li>
 * </ul>
 */
public class QLexpressLexer {
    private final String input;
    
    private final String source;
    
    private final InterpolationMode interpolationMode;
    
    private final boolean strictNewLines;
    
    private final String selectorStart;
    
    private final String selectorEnd;
    
    private int position;
    
    private int line;
    
    private int column;
    
    private List<Token> tokens;
    
    private Token currentToken;
    
    private int tokenIndex;
    
    // For tracking token start position
    private int tokenStartLine;
    
    private int tokenStartColumn;
    
    private int tokenStartPosition;
    
    // Lexer mode stack for handling different contexts (e.g., string interpolation)
    private enum LexerMode {
        DEFAULT, STATIC_STRING, DYNAMIC_STRING, SELECTOR_VARIABLE
    }
    
    private java.util.Stack<LexerMode> modeStack;
    
    /**
     * Creates a new lexer for the given input.
     *
     * @param input the input source code
     * @param source the source identifier (e.g., file name)
     * @param interpolationMode the interpolation mode
     * @param strictNewLines whether to emit NEWLINE tokens
     * @param selectorStart the selector start token (e.g., "${")
     * @param selectorEnd the selector end token (e.g., "}")
     */
    public QLexpressLexer(String input, String source, InterpolationMode interpolationMode, boolean strictNewLines,
        String selectorStart, String selectorEnd) {
        this.input = input != null ? input : "";
        this.source = source;
        this.interpolationMode = interpolationMode != null ? interpolationMode : InterpolationMode.SCRIPT;
        this.strictNewLines = strictNewLines;
        this.selectorStart = selectorStart != null ? selectorStart : "${";
        this.selectorEnd = selectorEnd != null ? selectorEnd : "}";
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.modeStack = new java.util.Stack<>();
        this.modeStack.push(LexerMode.DEFAULT);
    }
    
    /**
     * Creates a new lexer with default settings.
     *
     * @param input the input source code
     */
    public QLexpressLexer(String input) {
        this(input, null, InterpolationMode.SCRIPT, true, "${", "}");
    }
    
    /**
     * Creates a new lexer with specified interpolation mode and newline handling.
     *
     * @param input the input source code
     * @param source the source identifier
     * @param interpolationMode the interpolation mode
     * @param strictNewLines whether to emit NEWLINE tokens
     */
    public QLexpressLexer(String input, String source, InterpolationMode interpolationMode, boolean strictNewLines) {
        this(input, source, interpolationMode, strictNewLines, "${", "}");
    }
    
    /**
     * Tokenizes the entire input and returns the list of tokens.
     *
     * @return the list of tokens
     */
    public List<Token> tokenize() {
        tokens = new ArrayList<>();
        position = 0;
        line = 1;
        column = 1;
        
        while (position < input.length()) {
            skipWhitespace();
            if (position >= input.length()) {
                break;
            }
            
            Token token = readToken();
            if (token != null) {
                // Skip NEWLINE tokens in non-strict mode
                if (!strictNewLines && token.getType() == TokenType.NEWLINE) {
                    continue;
                }
                tokens.add(token);
            }
        }
        
        // Add EOF token
        tokens.add(Token.eof(line, column, source));
        
        return tokens;
    }
    
    /**
     * Reads the next token from the input.
     *
     * @return the next token, or null if no token can be read
     */
    private Token readToken() {
        markTokenStart();
        
        char ch = peek();
        
        // Check for end of input
        if (ch == '\0') {
            return null;
        }
        
        // Check for selector start (${ or #{ or $[ or #[)
        if (ch == '$' || ch == '#') {
            if (position + 1 < input.length()) {
                char nextCh = peek(1);
                if ((nextCh == '{' || nextCh == '[')) {
                    String selector = String.valueOf(ch) + nextCh;
                    // Check if this matches the configured selector start
                    if (selector.equals(selectorStart) || selector.equals("${") || selector.equals("#{")
                        || selector.equals("$[") || selector.equals("#[")) {
                        // Read the selector variable content (everything until selector end)
                        String selectorContent = readSelectorVariable(selector);
                        return new Token(TokenType.SELECTOR_START, selectorContent, tokenStartLine, tokenStartColumn,
                            source);
                    }
                }
            }
        }
        
        // Newline
        if (ch == '\r' || ch == '\n') {
            return readNewline();
        }
        
        // Single quote string
        if (ch == '\'') {
            return readSingleQuoteString();
        }
        
        // Double quote string
        if (ch == '"') {
            return readDoubleQuoteString();
        }
        
        // Identifier or keyword
        if (isIdStart(ch)) {
            return readIdentifier();
        }
        
        // Number
        if (Character.isDigit(ch) || (ch == '.' && Character.isDigit(peek(1)))) {
            return readNumber();
        }
        
        // Operator or delimiter
        return readOperatorOrDelimiter();
    }
    
    /**
     * Reads an operator or delimiter token.
     */
    private Token readOperatorOrDelimiter() {
        char ch = peek();
        
        // Check for multi-character operators first (longest match)
        String twoChar = null;
        if (position + 1 < input.length()) {
            twoChar = String.valueOf(ch) + peek(1);
        }
        
        String threeChar = null;
        if (position + 2 < input.length()) {
            threeChar = twoChar + peek(2);
        }
        
        String fourChar = null;
        if (position + 3 < input.length()) {
            fourChar = threeChar + peek(3);
        }
        
        // Four-character operators
        if (fourChar != null) {
            switch (fourChar) {
                case ">>>=":
                    consume();
                    consume();
                    consume();
                    consume();
                    return new Token(TokenType.URSHIFT_ASSIGN, fourChar, tokenStartLine, tokenStartColumn, source);
                case "<<=":
                    // This is actually 4 characters: < < = =
                    // But we handle it as 3 characters since << is one token
                    // Actually, <<= is 3 characters, handled below
                    break;
                case ">>=":
                    // This is actually 4 characters: > > = =
                    // But we handle it as 3 characters since >> is one token
                    // Actually, >>= is 3 characters, handled below
                    break;
            }
        }
        
        // Three-character operators
        if (threeChar != null) {
            switch (threeChar) {
                case ">>>":
                    consume();
                    consume();
                    consume();
                    return new Token(TokenType.URSHIFT, threeChar, tokenStartLine, tokenStartColumn, source);
                case "<<=":
                    consume();
                    consume();
                    consume();
                    return new Token(TokenType.LSHIFT_ASSIGN, threeChar, tokenStartLine, tokenStartColumn, source);
                case ">>=":
                    consume();
                    consume();
                    consume();
                    return new Token(TokenType.RSHIFT_ASSIGN, threeChar, tokenStartLine, tokenStartColumn, source);
            }
        }
        
        // Two-character operators
        if (twoChar != null) {
            switch (twoChar) {
                case "++":
                    consume();
                    consume();
                    return new Token(TokenType.INC, twoChar, tokenStartLine, tokenStartColumn, source);
                case "--":
                    consume();
                    consume();
                    return new Token(TokenType.DEC, twoChar, tokenStartLine, tokenStartColumn, source);
                case "+=":
                    consume();
                    consume();
                    return new Token(TokenType.ADD_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "-=":
                    consume();
                    consume();
                    return new Token(TokenType.SUB_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "*=":
                    consume();
                    consume();
                    return new Token(TokenType.MUL_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "/=":
                    consume();
                    consume();
                    return new Token(TokenType.DIV_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "%=":
                    consume();
                    consume();
                    return new Token(TokenType.MOD_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "&=":
                    consume();
                    consume();
                    return new Token(TokenType.AND_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "|=":
                    consume();
                    consume();
                    return new Token(TokenType.OR_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case "^=":
                    consume();
                    consume();
                    return new Token(TokenType.XOR_ASSIGN, twoChar, tokenStartLine, tokenStartColumn, source);
                case ">>":
                    consume();
                    consume();
                    return new Token(TokenType.RIGHSHIFT, twoChar, tokenStartLine, tokenStartColumn, source);
                case "<<":
                    consume();
                    consume();
                    return new Token(TokenType.LEFTSHIFT, twoChar, tokenStartLine, tokenStartColumn, source);
                case ">=":
                    consume();
                    consume();
                    return new Token(TokenType.GE, twoChar, tokenStartLine, tokenStartColumn, source);
                case "<=":
                    consume();
                    consume();
                    return new Token(TokenType.LE, twoChar, tokenStartLine, tokenStartColumn, source);
                case "<>":
                    consume();
                    consume();
                    return new Token(TokenType.NOEQ, twoChar, tokenStartLine, tokenStartColumn, source);
                case ".*":
                    consume();
                    consume();
                    return new Token(TokenType.DOTMUL, twoChar, tokenStartLine, tokenStartColumn, source);
                case "?.":
                    consume();
                    consume();
                    return new Token(TokenType.OPTIONAL_CHAINING, twoChar, tokenStartLine, tokenStartColumn, source);
                case "*.":
                    consume();
                    consume();
                    return new Token(TokenType.SPREAD_CHAINING, twoChar, tokenStartLine, tokenStartColumn, source);
                case "::":
                    consume();
                    consume();
                    return new Token(TokenType.DCOLON, twoChar, tokenStartLine, tokenStartColumn, source);
                case "->":
                    consume();
                    consume();
                    return new Token(TokenType.ARROW, twoChar, tokenStartLine, tokenStartColumn, source);
                case "==":
                    consume();
                    consume();
                    return new Token(TokenType.OPID, twoChar, tokenStartLine, tokenStartColumn, source);
                case "!=":
                    consume();
                    consume();
                    return new Token(TokenType.OPID, twoChar, tokenStartLine, tokenStartColumn, source);
                case "&&":
                    consume();
                    consume();
                    return new Token(TokenType.OPID, twoChar, tokenStartLine, tokenStartColumn, source);
                case "||":
                    consume();
                    consume();
                    return new Token(TokenType.OPID, twoChar, tokenStartLine, tokenStartColumn, source);
                default:
                    // Check for OPID patterns (like +&, |*, etc.) that are not explicitly handled
                    // Only check if both characters are valid OPID characters
                    if (isOpIdStartChar(ch) && isOpIdChar(twoChar.charAt(1))) {
                        return readOpId();
                    }
                    break;
            }
        }
        
        // Single-character operators and delimiters
        consume();
        switch (ch) {
            // Arithmetic operators
            case '+':
                return new Token(TokenType.ADD, "+", tokenStartLine, tokenStartColumn, source);
            case '-':
                return new Token(TokenType.SUB, "-", tokenStartLine, tokenStartColumn, source);
            case '*':
                return new Token(TokenType.MUL, "*", tokenStartLine, tokenStartColumn, source);
            case '/':
                return new Token(TokenType.DIV, "/", tokenStartLine, tokenStartColumn, source);
            case '%':
                return new Token(TokenType.MOD, "%", tokenStartLine, tokenStartColumn, source);
            
            // Bitwise operators
            case '&':
                return new Token(TokenType.BIT_AND, "&", tokenStartLine, tokenStartColumn, source);
            case '|':
                return new Token(TokenType.BIT_OR, "|", tokenStartLine, tokenStartColumn, source);
            case '^':
                return new Token(TokenType.BIT_XOR, "^", tokenStartLine, tokenStartColumn, source);
            case '~':
                return new Token(TokenType.TILDE, "~", tokenStartLine, tokenStartColumn, source);
            
            // Logical operators
            case '!':
                return new Token(TokenType.BANG, "!", tokenStartLine, tokenStartColumn, source);
            
            // Comparison operators
            case '>':
                return new Token(TokenType.GT, ">", tokenStartLine, tokenStartColumn, source);
            case '<':
                return new Token(TokenType.LT, "<", tokenStartLine, tokenStartColumn, source);
            case '=':
                return new Token(TokenType.EQ, "=", tokenStartLine, tokenStartColumn, source);
            
            // Other operators
            case '.':
                return new Token(TokenType.DOT, ".", tokenStartLine, tokenStartColumn, source);
            case '?':
                return new Token(TokenType.QUESTION, "?", tokenStartLine, tokenStartColumn, source);
            case ':':
                return new Token(TokenType.COLON, ":", tokenStartLine, tokenStartColumn, source);
            
            // Delimiters
            case '(':
                return new Token(TokenType.LPAREN, "(", tokenStartLine, tokenStartColumn, source);
            case ')':
                return new Token(TokenType.RPAREN, ")", tokenStartLine, tokenStartColumn, source);
            case '{':
                return new Token(TokenType.LBRACE, "{", tokenStartLine, tokenStartColumn, source);
            case '}':
                return new Token(TokenType.RBRACE, "}", tokenStartLine, tokenStartColumn, source);
            case '[':
                return new Token(TokenType.LBRACK, "[", tokenStartLine, tokenStartColumn, source);
            case ']':
                return new Token(TokenType.RBRACK, "]", tokenStartLine, tokenStartColumn, source);
            case ';':
                return new Token(TokenType.SEMI, ";", tokenStartLine, tokenStartColumn, source);
            case ',':
                return new Token(TokenType.COMMA, ",", tokenStartLine, tokenStartColumn, source);
            
            // Check for OPID (custom operator) - starts with certain chars
            // These are operators like ==, !=, &&, ||, or longer custom operators
            default:
                // Check if this could be an OPID (custom operator)
                if (isOpIdStartChar(ch)) {
                    return readOpId();
                }
                return new Token(TokenType.CATCH_ALL, String.valueOf(ch), tokenStartLine, tokenStartColumn, source);
        }
    }
    
    /**
     * Checks if a character is a valid start character for an OPID (custom operator).
     * Based on the ANTLR grammar: OpIdItemStart
     */
    private boolean isOpIdStartChar(char ch) {
        return ch == '^' || ch == '~' || ch == '&' || ch == '|' || ch == '*' || ch == '%' || ch == '=' || ch == '!'
            || ch == '/' || ch == '+' || ch == '-' || ch == '?' || ch == '.';
    }
    
    /**
     * Checks if a character is a valid continuation character for an OPID.
     * Based on the ANTLR grammar: OpIdItem
     * Note: OpIdItem does NOT include ADD (+) or SUB (-), only continuation chars
     */
    private boolean isOpIdChar(char ch) {
        // OpIdItem from grammar: CARET, TILDE, BIT_AND, BIT_OR, MUL, MOD, EQ, BANG,
        //                       LT, GT, DIV, COLON, QUESTION, DOT
        // Note: ADD (+) and SUB (-) are NOT in OpIdItem, only in OpIdItemStart
        return ch == '^' || ch == '~' || ch == '&' || ch == '|' || ch == '*' || ch == '%' || ch == '=' || ch == '!'
            || ch == '<' || ch == '>' || ch == '/' || ch == ':' || ch == '?' || ch == '.';
    }
    
    /**
     * Reads an OPID (custom operator) token.
     * OPID tokens are multi-character operators like ==, !=, &&, ||, etc.
     */
    private Token readOpId() {
        StringBuilder sb = new StringBuilder();
        sb.append(consume());
        
        // Continue reading while we have valid OPID characters
        while (position < input.length() && isOpIdChar(peek())) {
            sb.append(consume());
        }
        
        String text = sb.toString();
        return new Token(TokenType.OPID, text, tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads a newline token.
     */
    private Token readNewline() {
        StringBuilder sb = new StringBuilder();
        char ch = peek();
        
        if (ch == '\r') {
            consume();
            sb.append('\r');
            if (peek() == '\n') {
                consume();
                sb.append('\n');
            }
        }
        else {
            consume();
            sb.append('\n');
        }
        
        return new Token(TokenType.NEWLINE, sb.toString(), tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads a single-quoted string literal.
     */
    private Token readSingleQuoteString() {
        StringBuilder sb = new StringBuilder();
        consume(); // opening quote
        
        while (position < input.length()) {
            char ch = peek();
            
            if (ch == '\'') {
                consume(); // closing quote
                return new Token(TokenType.QUOTE_STRING_LITERAL, sb.toString(), tokenStartLine, tokenStartColumn,
                    source);
            }
            
            if (ch == '\\') {
                consume(); // backslash
                if (position < input.length()) {
                    char escaped = consume();
                    sb.append(escapeChar(escaped));
                }
            }
            else {
                sb.append(consume());
            }
        }
        
        // Unterminated string - error, but return what we have
        return new Token(TokenType.QUOTE_STRING_LITERAL, sb.toString(), tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads a double-quoted string literal (with potential interpolation).
     */
    private Token readDoubleQuoteString() {
        StringBuilder sb = new StringBuilder();
        consume(); // opening quote
        
        while (position < input.length()) {
            char ch = peek();
            
            if (ch == '"') {
                consume(); // closing quote
                return new Token(TokenType.DOUBLE_QUOTE, sb.toString(), tokenStartLine, tokenStartColumn, source);
            }
            
            if (ch == '\\' && interpolationMode == InterpolationMode.DISABLE) {
                consume(); // backslash
                if (position < input.length()) {
                    char escaped = consume();
                    sb.append(escapeChar(escaped));
                }
            }
            else {
                sb.append(consume());
            }
        }
        
        // Unterminated string
        return new Token(TokenType.DOUBLE_QUOTE, sb.toString(), tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads an identifier or keyword.
     */
    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        sb.append(consume());
        
        while (position < input.length() && isIdPart(peek())) {
            sb.append(consume());
        }
        
        String text = sb.toString();
        
        // Check if it's a keyword
        TokenType type = getKeywordType(text);
        if (type != null) {
            return new Token(type, text, tokenStartLine, tokenStartColumn, source);
        }
        
        return new Token(TokenType.ID, text, tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads a number (integer or float).
     */
    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        boolean isFloat = false;
        boolean hasExponent = false;
        
        // Handle potential leading sign (but this is usually an operator)
        // We'll just read the digits
        
        // Check for hex literal (0x or 0X)
        if (peek() == '0' && (peek(1) == 'x' || peek(1) == 'X')) {
            return readHexNumber();
        }
        
        // Check for binary literal (0b or 0B)
        if (peek() == '0' && (peek(1) == 'b' || peek(1) == 'B')) {
            return readBinaryNumber();
        }
        
        // Check for octal literal (starts with 0)
        if (peek() == '0' && Character.isDigit(peek(1))) {
            return readOctalNumber();
        }
        
        // Decimal number
        while (position < input.length() && (Character.isDigit(peek()) || peek() == '_')) {
            char ch = peek();
            if (ch == '_') {
                consume(); // Skip underscore
            }
            else {
                sb.append(consume());
            }
        }
        
        // Check for decimal point
        if (position < input.length() && peek() == '.') {
            // Need to check if this is actually a decimal point or something else
            // (e.g., method call on an integer literal)
            isFloat = true;
            sb.append(consume());
            while (position < input.length() && (Character.isDigit(peek()) || peek() == '_')) {
                char ch = peek();
                if (ch == '_') {
                    consume(); // Skip underscore
                }
                else {
                    sb.append(consume());
                }
            }
        }
        
        // Check for exponent
        if (position < input.length() && (peek() == 'e' || peek() == 'E')) {
            hasExponent = true;
            sb.append(consume());
            if (position < input.length() && (peek() == '+' || peek() == '-')) {
                sb.append(consume());
            }
            while (position < input.length() && (Character.isDigit(peek()) || peek() == '_')) {
                char ch = peek();
                if (ch == '_') {
                    consume(); // Skip underscore
                }
                else {
                    sb.append(consume());
                }
            }
        }
        
        // Check for type suffix
        if (position < input.length()
            && (peek() == 'l' || peek() == 'L' || peek() == 'f' || peek() == 'F' || peek() == 'd' || peek() == 'D')) {
            sb.append(consume());
        }
        
        String text = sb.toString();
        
        // Determine token type
        if (isFloat || hasExponent) {
            return new Token(TokenType.FLOATING_POINT_LITERAL, text, tokenStartLine, tokenStartColumn, source);
        }
        
        return new Token(TokenType.INTEGER_OR_FLOATING_LITERAL, text, tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads a hexadecimal number.
     */
    private Token readHexNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(consume()); // 0
        sb.append(consume()); // x or X
        
        while (position < input.length() && (isHexDigit(peek()) || peek() == '_')) {
            char ch = peek();
            if (ch == '_') {
                consume(); // Skip underscore
            }
            else {
                sb.append(consume());
            }
        }
        
        // Check for type suffix
        if (position < input.length() && (peek() == 'l' || peek() == 'L')) {
            sb.append(consume());
        }
        
        return new Token(TokenType.INTEGER_LITERAL, sb.toString(), tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads a binary number.
     */
    private Token readBinaryNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(consume()); // 0
        sb.append(consume()); // b or B
        
        while (position < input.length() && ((peek() == '0' || peek() == '1') || peek() == '_')) {
            char ch = peek();
            if (ch == '_') {
                consume(); // Skip underscore
            }
            else {
                sb.append(consume());
            }
        }
        
        // Check for type suffix
        if (position < input.length() && (peek() == 'l' || peek() == 'L')) {
            sb.append(consume());
        }
        
        return new Token(TokenType.INTEGER_LITERAL, sb.toString(), tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Reads an octal number.
     */
    private Token readOctalNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(consume()); // 0
        
        while (position < input.length() && (isOctalDigit(peek()) || peek() == '_')) {
            char ch = peek();
            if (ch == '_') {
                consume(); // Skip underscore
            }
            else {
                sb.append(consume());
            }
        }
        
        // Check for type suffix
        if (position < input.length() && (peek() == 'l' || peek() == 'L')) {
            sb.append(consume());
        }
        
        return new Token(TokenType.INTEGER_LITERAL, sb.toString(), tokenStartLine, tokenStartColumn, source);
    }
    
    /**
     * Skips whitespace (but not newlines) and comments.
     */
    private void skipWhitespace() {
        while (position < input.length()) {
            skipWhitespaceInternal();
            if (position < input.length() && peek() == '/') {
                // Check for comments
                if (peek(1) == '/') {
                    skipLineComment();
                    continue;
                }
                else if (peek(1) == '*') {
                    skipBlockComment();
                    continue;
                }
            }
            if (position < input.length()) {
                char ch = peek();
                if (ch == ' ' || ch == '\t' || ch == '\f') {
                    consume();
                }
                else {
                    break;
                }
            }
        }
    }
    
    /**
     * Skips internal whitespace.
     */
    private void skipWhitespaceInternal() {
        while (position < input.length()) {
            char ch = peek();
            if (ch == ' ' || ch == '\t' || ch == '\f') {
                consume();
            }
            else {
                break;
            }
        }
    }
    
    /**
     * Skips a line comment (//...).
     */
    private void skipLineComment() {
        if (peek() == '/' && peek(1) == '/') {
            consume();
            consume(); // //
            while (position < input.length() && peek() != '\r' && peek() != '\n') {
                consume();
            }
        }
    }
    
    /**
     * Skips a block comment (/* ... * /).
     */
    private void skipBlockComment() {
        if (peek() == '/' && peek(1) == '*') {
            consume();
            consume(); // /*
            while (position < input.length()) {
                if (peek() == '*' && peek(1) == '/') {
                    consume();
                    consume();
                    break;
                }
                consume();
            }
        }
    }
    
    /**
     * Checks if a character can start an identifier.
     */
    private boolean isIdStart(char ch) {
        // Match ANTLR IdStart from grammar
        return ch == '#' || ch == '_' || ch == '@' || ch == '$' || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
            || (ch >= '\u00A2' && ch <= '\u00A5') || (ch >= '\u00C0' && ch <= '\u00D6')
            || (ch >= '\u00D8' && ch <= '\u00F6') || (ch >= '\u00F8' && ch <= '\u02C1')
            || (ch >= '\u4E00' && ch <= '\u9FEA') || // CJK
            Character.isJavaIdentifierStart(ch);
    }
    
    /**
     * Checks if a character can be part of an identifier.
     */
    private boolean isIdPart(char ch) {
        // Match ANTLR IdPart from grammar
        return isIdStart(ch) || ch == '\u3001' || (ch >= '\uFF08' && ch <= '\uFF09') || // Full-width parens
            (ch >= '\u3010' && ch <= '\u3011') || // Full-width brackets
            (ch >= '0' && ch <= '9') || Character.isJavaIdentifierPart(ch);
    }
    
    /**
     * Checks if a character is a hex digit.
     */
    private boolean isHexDigit(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
    
    /**
     * Checks if a character is an octal digit.
     */
    private boolean isOctalDigit(char ch) {
        return ch >= '0' && ch <= '7';
    }
    
    /**
     * Returns the keyword type for a given identifier, or null if not a keyword.
     */
    private TokenType getKeywordType(String text) {
        switch (text) {
            case "for":
                return TokenType.FOR;
            case "if":
                return TokenType.IF;
            case "else":
                return TokenType.ELSE;
            case "while":
                return TokenType.WHILE;
            case "break":
                return TokenType.BREAK;
            case "continue":
                return TokenType.CONTINUE;
            case "return":
                return TokenType.RETURN;
            case "switch":
                return TokenType.SWITCH;
            case "case":
                return TokenType.CASE;
            case "default":
                return TokenType.DEFAULT;
            case "function":
                return TokenType.FUNCTION;
            case "macro":
                return TokenType.MACRO;
            case "import":
                return TokenType.IMPORT;
            case "static":
                return TokenType.STATIC;
            case "new":
                return TokenType.NEW;
            case "byte":
                return TokenType.BYTE;
            case "short":
                return TokenType.SHORT;
            case "int":
                return TokenType.INT;
            case "long":
                return TokenType.LONG;
            case "float":
                return TokenType.FLOAT;
            case "double":
                return TokenType.DOUBLE;
            case "char":
                return TokenType.CHAR;
            case "boolean":
                return TokenType.BOOLEAN;
            case "null":
                return TokenType.NULL;
            case "true":
                return TokenType.TRUE;
            case "false":
                return TokenType.FALSE;
            case "extends":
                return TokenType.EXTENDS;
            case "super":
                return TokenType.SUPER;
            case "try":
                return TokenType.TRY;
            case "catch":
                return TokenType.CATCH;
            case "finally":
                return TokenType.FINALLY;
            case "throw":
                return TokenType.THROW;
            case "then":
                return TokenType.THEN;
            case "class":
                return TokenType.CLASS;
            case "this":
                return TokenType.THIS;
            default:
                return null;
        }
    }
    
    /**
     * Returns the escaped character.
     */
    private char escapeChar(char ch) {
        switch (ch) {
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case '\'':
                return '\'';
            case '"':
                return '"';
            case '\\':
                return '\\';
            default:
                return ch;
        }
    }
    
    /**
     * Marks the start of a new token.
     */
    private void markTokenStart() {
        tokenStartLine = line;
        tokenStartColumn = column;
        tokenStartPosition = position;
    }
    
    /**
     * Returns the current character without consuming it.
     */
    private char peek() {
        return peek(0);
    }
    
    /**
     * Returns the character at offset from current position.
     */
    private char peek(int offset) {
        int pos = position + offset;
        if (pos >= 0 && pos < input.length()) {
            return input.charAt(pos);
        }
        return '\0';
    }
    
    /**
     * Consumes and returns the current character.
     */
    private char consume() {
        if (position >= input.length()) {
            return '\0';
        }
        char ch = input.charAt(position);
        position++;
        
        if (ch == '\n' || (ch == '\r' && peek() != '\n')) {
            line++;
            column = 1;
        }
        else {
            column++;
        }
        
        return ch;
    }
    
    /**
     * Reads a selector variable expression.
     * <p>
     * This reads everything between the selector start (${) and the selector end (}).
     * The content is treated as a single string (e.g., "/a/aa" in ${/a/aa}).
     *
     * @param selectorStart the selector start string (e.g., "${", "#{")
     * @return the full selector expression (start + content + end)
     */
    private String readSelectorVariable(String selectorStart) {
        // Consume the selector start
        consume(); // $ or #
        consume(); // { or [
        
        StringBuilder sb = new StringBuilder();
        sb.append(selectorStart);
        
        // Determine the selector end based on selector start
        String selectorEnd;
        if (selectorStart.endsWith("{")) {
            selectorEnd = this.selectorEnd != null ? this.selectorEnd : "}";
        }
        else { // [ selector
            selectorEnd = "]";
        }
        
        // Read until we find the selector end
        int selectorEndLength = selectorEnd.length();
        char lastCharOfSelector = selectorEnd.charAt(selectorEndLength - 1);
        sb.ensureCapacity(selectorEndLength * 2);
        
        while (true) {
            if (position >= input.length()) {
                // Unterminated selector - error, but return what we have
                break;
            }
            
            char curCh = peek();
            if (curCh == '\n') {
                // Unterminated selector - newline before closing
                break;
            }
            
            sb.append(curCh);
            consume();
            
            if (curCh == lastCharOfSelector && sb.length() >= selectorEndLength) {
                if (endsWith(sb, selectorEnd)) {
                    // Found the selector end
                    break;
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Checks if the StringBuilder ends with the given suffix.
     */
    private boolean endsWith(StringBuilder sb, String suffix) {
        int suffixLength = suffix.length();
        int sbLength = sb.length();
        
        if (sbLength < suffixLength) {
            return false;
        }
        
        for (int i = 0; i < suffixLength; i++) {
            if (sb.charAt(sbLength - suffixLength + i) != suffix.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Resets the lexer for re-tokenization.
     */
    public void reset() {
        this.position = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
        this.tokenIndex = 0;
        this.currentToken = null;
    }
    
    /**
     * Returns all tokens from tokenization.
     */
    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}
