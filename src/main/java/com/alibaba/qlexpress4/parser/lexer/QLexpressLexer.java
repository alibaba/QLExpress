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
                        return createToken(TokenType.SELECTOR_START, selectorContent);
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
                    return createToken(TokenType.URSHIFT_ASSIGN, fourChar);
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
                    return createToken(TokenType.URSHIFT, threeChar);
                case "<<=":
                    consume();
                    consume();
                    consume();
                    return createToken(TokenType.LSHIFT_ASSIGN, threeChar);
                case ">>=":
                    consume();
                    consume();
                    consume();
                    return createToken(TokenType.RSHIFT_ASSIGN, threeChar);
            }
        }
        
        // Two-character operators
        if (twoChar != null) {
            switch (twoChar) {
                case "++":
                    consume();
                    consume();
                    return createToken(TokenType.INC, twoChar);
                case "--":
                    consume();
                    consume();
                    return createToken(TokenType.DEC, twoChar);
                case "+=":
                    consume();
                    consume();
                    return createToken(TokenType.ADD_ASSIGN, twoChar);
                case "-=":
                    consume();
                    consume();
                    return createToken(TokenType.SUB_ASSIGN, twoChar);
                case "*=":
                    consume();
                    consume();
                    return createToken(TokenType.MUL_ASSIGN, twoChar);
                case "/=":
                    consume();
                    consume();
                    return createToken(TokenType.DIV_ASSIGN, twoChar);
                case "%=":
                    consume();
                    consume();
                    return createToken(TokenType.MOD_ASSIGN, twoChar);
                case "&=":
                    consume();
                    consume();
                    return createToken(TokenType.AND_ASSIGN, twoChar);
                case "|=":
                    consume();
                    consume();
                    return createToken(TokenType.OR_ASSIGN, twoChar);
                case "^=":
                    consume();
                    consume();
                    return createToken(TokenType.XOR_ASSIGN, twoChar);
                case ">>":
                    consume();
                    consume();
                    return createToken(TokenType.RIGHSHIFT, twoChar);
                case "<<":
                    consume();
                    consume();
                    return createToken(TokenType.LEFTSHIFT, twoChar);
                case ">=":
                    consume();
                    consume();
                    return createToken(TokenType.GE, twoChar);
                case "<=":
                    consume();
                    consume();
                    return createToken(TokenType.LE, twoChar);
                case "<>":
                    consume();
                    consume();
                    return createToken(TokenType.NOEQ, twoChar);
                case ".*":
                    consume();
                    consume();
                    return createToken(TokenType.DOTMUL, twoChar);
                case "?.":
                    consume();
                    consume();
                    return createToken(TokenType.OPTIONAL_CHAINING, twoChar);
                case "*.":
                    consume();
                    consume();
                    return createToken(TokenType.SPREAD_CHAINING, twoChar);
                case "::":
                    consume();
                    consume();
                    return createToken(TokenType.DCOLON, twoChar);
                case "->":
                    consume();
                    consume();
                    return createToken(TokenType.ARROW, twoChar);
                case "==":
                    consume();
                    consume();
                    return createToken(TokenType.OPID, twoChar);
                case "!=":
                    consume();
                    consume();
                    return createToken(TokenType.OPID, twoChar);
                case "&&":
                    consume();
                    consume();
                    return createToken(TokenType.OPID, twoChar);
                case "||":
                    consume();
                    consume();
                    return createToken(TokenType.OPID, twoChar);
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
                return createToken(TokenType.ADD, "+");
            case '-':
                return createToken(TokenType.SUB, "-");
            case '*':
                return createToken(TokenType.MUL, "*");
            case '/':
                return createToken(TokenType.DIV, "/");
            case '%':
                return createToken(TokenType.MOD, "%");
            
            // Bitwise operators
            case '&':
                return createToken(TokenType.BIT_AND, "&");
            case '|':
                return createToken(TokenType.BIT_OR, "|");
            case '^':
                return createToken(TokenType.BIT_XOR, "^");
            case '~':
                return createToken(TokenType.TILDE, "~");
            
            // Logical operators
            case '!':
                return createToken(TokenType.BANG, "!");
            
            // Comparison operators
            case '>':
                return createToken(TokenType.GT, ">");
            case '<':
                return createToken(TokenType.LT, "<");
            case '=':
                return createToken(TokenType.EQ, "=");
            
            // Other operators
            case '.':
                return createToken(TokenType.DOT, ".");
            case '?':
                return createToken(TokenType.QUESTION, "?");
            case ':':
                return createToken(TokenType.COLON, ":");
            
            // Delimiters
            case '(':
                return createToken(TokenType.LPAREN, "(");
            case ')':
                return createToken(TokenType.RPAREN, ")");
            case '{':
                return createToken(TokenType.LBRACE, "{");
            case '}':
                return createToken(TokenType.RBRACE, "}");
            case '[':
                return createToken(TokenType.LBRACK, "[");
            case ']':
                return createToken(TokenType.RBRACK, "]");
            case ';':
                return createToken(TokenType.SEMI, ";");
            case ',':
                return createToken(TokenType.COMMA, ",");
            
            // Check for OPID (custom operator) - starts with certain chars
            // These are operators like ==, !=, &&, ||, or longer custom operators
            default:
                // Check if this could be an OPID (custom operator)
                if (isOpIdStartChar(ch)) {
                    return readOpId();
                }
                return createToken(TokenType.CATCH_ALL, String.valueOf(ch));
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
        return createToken(TokenType.OPID, text);
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
        
        return createToken(TokenType.NEWLINE, sb.toString());
    }
    
    /**
     * Reads a single-quoted string literal.
     * Processes escape sequences like \n, \r, \t, \b, \f, \\, \', \".
     */
    private Token readSingleQuoteString() {
        StringBuilder sb = new StringBuilder();
        consume(); // opening quote

        while (position < input.length()) {
            char ch = peek();

            // Handle escape sequences: \n, \r, \t, \b, \f, \\, \', \"
            // Single-quoted strings support all standard escape sequences
            if (ch == '\\') {
                consume(); // consume the backslash
                if (position < input.length()) {
                    char escaped = consume();
                    sb.append(escapeChar(escaped));
                }
            }
            else if (ch == '\'') {
                consume(); // closing quote
                return createToken(TokenType.QUOTE_STRING_LITERAL, sb.toString());
            }
            else {
                sb.append(consume());
            }
        }

        // Unterminated string - error, but return what we have
        return createToken(TokenType.QUOTE_STRING_LITERAL, sb.toString());
    }
    
    /**
     * Reads a double-quoted string literal (with potential interpolation).
     * <p>
     * Double-quoted strings always support escape sequences like \", \n, \t, etc.
     * The interpolation mode controls whether ${...} expressions are processed.
     */
    private Token readDoubleQuoteString() {
        StringBuilder sb = new StringBuilder();
        consume(); // opening quote

        int braceDepth = 0; // Track nesting depth of ${...} blocks (only when interpolation enabled)
        boolean interpolationEnabled = interpolationMode != InterpolationMode.DISABLE;

        while (position < input.length()) {
            char ch = peek();

            if (ch == '"' && braceDepth == 0) {
                consume(); // closing quote
                return createToken(TokenType.DOUBLE_QUOTE, sb.toString());
            }

            // Track ${...} blocks to handle nested strings correctly (only when interpolation enabled)
            if (interpolationEnabled && ch == '$' && position + 1 < input.length() && peek(1) == '{') {
                braceDepth++;
                sb.append(consume()); // $
                sb.append(consume()); // {
                continue;
            }
            if (interpolationEnabled && ch == '}' && braceDepth > 0) {
                braceDepth--;
                sb.append(consume());
                continue;
            }

            // Always handle escape sequences in double-quoted strings
            // The interpolation mode controls ${...} processing
            if (ch == '\\') {
                consume(); // backslash
                if (position < input.length()) {
                    char escaped = consume();
                    // Special case: \${ should be preserved in the string content
                    // so that the parser can detect it as an escaped interpolation
                    // BUT only when interpolation is enabled
                    if (escaped == '$' && position < input.length() && peek() == '{'
                            && interpolationEnabled) {
                        sb.append("\\${");
                        consume(); // consume the '{'
                    }
                    else {
                        sb.append(escapeChar(escaped));
                    }
                }
            }
            else {
                sb.append(consume());
            }
        }

        // Unterminated string
        return createToken(TokenType.DOUBLE_QUOTE, sb.toString());
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
            return createToken(type, text);
        }
        
        return createToken(TokenType.ID, text);
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
        // The ANTLR grammar uses a lookahead predicate to avoid treating .d as a decimal point
        // when followed by letters (e.g., 1.doubleValue() should be parsed as 1 .doubleValue() not 1.d)
        if (position < input.length() && peek() == '.') {
            // Lookahead check: if the next 2+ chars are letters, this is likely a method call, not a decimal point
            // This implements the ANTLR grammar's predicate: !((LA(2) is letter) && (LA(3) is letter))
            boolean isMethodCall = false;
            if (position + 2 < input.length()) {
                char la2 = input.charAt(position + 1);
                char la3 = input.charAt(position + 2);
                if (Character.isLetter(la2) && Character.isLetter(la3)) {
                    isMethodCall = true;
                }
            }

            if (!isMethodCall) {
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
            return createToken(TokenType.FLOATING_POINT_LITERAL, text);
        }
        
        return createToken(TokenType.INTEGER_OR_FLOATING_LITERAL, text);
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
        
        return createToken(TokenType.INTEGER_LITERAL, sb.toString());
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
        
        return createToken(TokenType.INTEGER_LITERAL, sb.toString());
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
        
        return createToken(TokenType.INTEGER_LITERAL, sb.toString());
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
            // For interpolation, we preserve $ when escaped (\${ stays as \${ in the string content)
            // This is handled by the string reading logic, not here
            case '$':
                return '$';
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
     * Creates a token with the current token start position information.
     *
     * @param type the token type
     * @param value the token value
     * @return a new token with position information
     */
    private Token createToken(TokenType type, String value) {
        return new Token(type, value, tokenStartLine, tokenStartColumn, tokenStartPosition, source);
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
