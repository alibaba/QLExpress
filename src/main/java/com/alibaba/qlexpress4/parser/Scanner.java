package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.QLException;

import java.math.BigDecimal;
import java.util.LinkedList;

public class Scanner {

    private final String script;

    private final QLOptions qlOptions;

    private int currentLine = 1;

    private int currentCol = 1;

    /**
     * col number in last line
     */
    private int preCol;

    private int pos;

    private LinkedList<Token> lookAheadQueue = new LinkedList<>();

    private LinkedList<Token> backQueue = new LinkedList<>();

    public Scanner(String script, QLOptions qlOptions) {
        this.script = script;
        this.qlOptions = qlOptions;
    }

    public Token next() {
        if (!backQueue.isEmpty()) {
            return backQueue.remove();
        }

        // skip blank char
        while (!isEnd()) {
            char curChar = advance();
            if (isInVisible(curChar)) {
                // skip blank
                continue;
            }
            switch (curChar) {
                // single-char-tokens
                case '^':
                    return newToken(TokenType.CARET, "^");
                case '~':
                    return newToken(TokenType.TILDE, "~");
                case '(':
                    return newToken(TokenType.LPAREN, "(");
                case ')':
                    return newToken(TokenType.RPAREN, ")");
                case '{':
                    return newToken(TokenType.LBRACE, "{");
                case '}':
                    return newToken(TokenType.RBRACE, "}");
                case '[':
                    return newToken(TokenType.LBRACK, "[");
                case ']':
                    return newToken(TokenType.RBRACK, "]");
                case '?':
                    return newToken(TokenType.QUESTION, "?");
                case ':':
                    return newToken(TokenType.COLON, ":");
                case ';':
                    return newToken(TokenType.SEMI, ";");
                case ',':
                    return newToken(TokenType.COMMA, ",");
                case '.':
                    return newToken(TokenType.DOT, ".");
                // two-char-tokens
                case '+':
                    if (matchAndAdvance('+')) {
                        return newToken(TokenType.INC, "++");
                    } else if (matchAndAdvance('=')) {
                        return newToken(TokenType.ADD_ASSIGN, "+=");
                    } else {
                        return newToken(TokenType.ADD, "+");
                    }
                case '-':
                    if (matchAndAdvance('-')) {
                        return newToken(TokenType.DEC, "--");
                    } else if (matchAndAdvance('=')) {
                        return newToken(TokenType.SUB_ASSIGN, "-=");
                    } else if (matchAndAdvance('>')) {
                        return newToken(TokenType.ARROW, "->");
                    } else {
                        return newToken(TokenType.SUB, "-");
                    }
                case '&':
                    if (matchAndAdvance('&')) {
                        return newToken(TokenType.AND, "&&");
                    } else if (matchAndAdvance('=')) {
                        return newToken(TokenType.AND_ASSIGN, "&=");
                    } else {
                        return newToken(TokenType.BITAND, "&");
                    }
                case '|':
                    if (matchAndAdvance('|')) {
                        return newToken(TokenType.OR, "||");
                    } else if (matchAndAdvance('=')) {
                        return newToken(TokenType.OR_ASSIGN, "|=");
                    } else {
                        return newToken(TokenType.BITOR, "|");
                    }
                case '*':
                    return matchAndAdvance('=')? newToken(TokenType.MUL_ASSIGN, "*="):
                            newToken(TokenType.MUL, "*");
                case '%':
                    return matchAndAdvance('=')? newToken(TokenType.MOD_ASSIGN, "%="):
                            newToken(TokenType.MOD, "%");
                case '=':
                    return matchAndAdvance('=')? newToken(TokenType.EQUAL, "=="):
                            newToken(TokenType.ASSIGN, "=");
                case '!':
                    return matchAndAdvance('=')? newToken(TokenType.NOTEQUAL, "!="):
                            newToken(TokenType.BANG, "!");
                case '<':
                    Token ltToken = newToken(TokenType.LT, "<");
                    if (matchAndAdvance('=')) {
                        return newToken(TokenType.LE, "<=");
                    } else if (matchAndAdvance('<')) {
                        if (matchAndAdvance('=')) {
                            return newToken(TokenType.LSHIFT_ASSIGN, "<<=");
                        } else {
                            backQueue.addLast(newToken(TokenType.LT, "<"));
                            return ltToken;
                        }
                    } else {
                        return newToken(TokenType.LT, "<");
                    }
                case '>':
                    Token gtToken = newToken(TokenType.GT, ">");
                    if (matchAndAdvance('=')) {
                        return newToken(TokenType.GE, ">=");
                    } else if (matchAndAdvance('>')) {
                        Token gtGtToken = newToken(TokenType.GT, ">");
                        if (matchAndAdvance('=')) {
                            return newToken(TokenType.RSHIFT_ASSIGN, ">>=");
                        } else if (matchAndAdvance('>')) {
                            // >>>
                            if (matchAndAdvance('=')) {
                                return newToken(TokenType.URSHIFT_ASSIGN, ">>>=");
                            } else {
                                backQueue.addLast(gtGtToken);
                                backQueue.addLast(newToken(TokenType.GT, ">"));
                                return gtToken;
                            }
                        } else {
                            backQueue.addLast(gtGtToken);
                            return gtToken;
                        }
                    } else {
                        return newToken(TokenType.GT, ">");
                    }
                // compose tokens
                // comments
                case '/':
                    if (matchAndAdvance('=')) {
                        return newToken(TokenType.DIV_ASSIGN, "/=");
                    } else if (matchAndAdvance('/')) {
                        // ignore line comment
                        advanceLineComment();
                    } else if (matchAndAdvance('*')) {
                        // ignore multi line comment
                        advanceMultiLineComment();
                    } else {
                        // divide
                        return newToken(TokenType.DIV, "/");
                    }
                    continue;
                // strings
                case '\'':
                    // raw string
                    return rawString();
                case '"':
                    // string
                    return string();
            }

            // positive number
            if (isDigit(curChar)) {
                return number();
            }
            // keyword and identifier
            return identifierOrKeywordOrType();
        }

        return null;
    }

    public Token lookAhead() {
        Token lookAheadToken = next();
        lookAheadQueue.add(lookAheadToken);
        return lookAheadToken;
    }

    /**
     * give back all lookAhead Token
     */
    public void back() {
        while (!lookAheadQueue.isEmpty()) {
            backQueue.addFirst(lookAheadQueue.removeLast());
        }
    }

    public String getScript() {
        return script;
    }

    private Token identifierOrKeywordOrType() {
        int startPos = pos;
        while (!isEnd()) {
            char cur = peek();
            if (isInVisible(cur) || SplitCharsSet.isSplitChar(cur)) {
                return newIdOrKeywordOrTypeToken(script.substring(startPos - 1, pos));
            }
            advance();
        }
        return newIdOrKeywordOrTypeToken(script.substring(startPos - 1, pos));
    }

    private Token newIdOrKeywordOrTypeToken(String tokenCharSeq) {
        String typeLiteral = BuiltInTypesSet.getLiteral(tokenCharSeq);
        if (typeLiteral != null) {
            return newTokenWithLiteral(TokenType.TYPE, tokenCharSeq, typeLiteral);
        } else
        // key word with literal
        if (KeyWordsSet.TRUE.equals(tokenCharSeq)) {
            return newTokenWithLiteral(TokenType.KEY_WORD, tokenCharSeq, true);
        } else if (KeyWordsSet.FALSE.equals(tokenCharSeq)) {
            return newTokenWithLiteral(TokenType.KEY_WORD, tokenCharSeq, false);
        } else if (KeyWordsSet.NULL.equals(tokenCharSeq)) {
            return newTokenWithLiteral(TokenType.KEY_WORD, tokenCharSeq, null);
        } else {
            // normal key word without literal
            return newToken(KeyWordsSet.isKeyWord(tokenCharSeq)? TokenType.KEY_WORD: TokenType.ID, tokenCharSeq);
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isInVisible(char c) {
        return c <= ' ';
    }

    private Token rawString() {
        StringBuilder lexemeBuilder = new StringBuilder().append('\'');
        StringBuilder literalBuilder = new StringBuilder();
        while (!isEnd()) {
            char cur = advance();
            lexemeBuilder.append(cur);
            if (cur == '\'') {
                return newTokenWithLiteral(TokenType.STRING, lexemeBuilder.toString(),
                        literalBuilder.toString());
            } else if (cur == '\n') {
                // not support line break in raw string
                throw QLException.reportScannerErr(script, pos, currentLine-1,
                        preCol, lexemeBuilder.toString(), "RAW_STRING_LINE_BREAK",
                        "''(raw string) line break");
            } else {
                literalBuilder.append(cur);
            }
        }

        throw QLException.reportScannerErr(script, pos, currentLine, currentCol,
                lexemeBuilder.toString(), "RAW_STRING_NOT_CLOSE", "''(raw string) not close");
    }

    private Token string() {
        StringBuilder lexemeBuilder = new StringBuilder().append('"');
        StringBuilder literalBuilder = new StringBuilder();

        final byte init = 0;
        final byte escape = 1;
        byte state = 0;

        while (!isEnd()) {
            char cur = advance();
            switch (state) {
                case init:
                    lexemeBuilder.append(cur);
                    if (cur == '"') {
                        return newTokenWithLiteral(TokenType.STRING,
                                lexemeBuilder.toString(),
                                literalBuilder.toString());
                    } else if (cur == '\\') {
                        // escape
                        state = escape;
                    } else if (cur == '\n') {
                        // not support line break
                        throw QLException.reportScannerErr(script, pos,
                                currentLine - 1, preCol,
                                lexemeBuilder.toString(), "STRING_LINE_BREAK",
                                "\"\"(string) line break");
                    } else {
                        literalBuilder.append(cur);
                    }
                    continue;
                case escape:
                    lexemeBuilder.append(cur);
                    state = init;
                    switch (cur) {
                        case '"':
                            literalBuilder.append('"');
                            break;
                        case 't':
                            literalBuilder.append('\t');
                            break;
                        case 'r':
                            literalBuilder.append('\r');
                            break;
                        case 'n':
                            literalBuilder.append('\n');
                            break;
                        case '\\':
                            literalBuilder.append('\\');
                            break;
                        case 'b':
                            literalBuilder.append('\b');
                            break;
                        case 'f':
                            literalBuilder.append('\f');
                            break;
                    }
            }
        }
        throw QLException.reportScannerErr(script, pos, currentLine, currentCol,
                lexemeBuilder.toString(), "STRING_NOT_CLOSE", "\"\"(string) not close");
    }

    private Token number() {
        final byte init = 0;
        final byte decimalPart = 1;
        final byte numTypePart = 2;
        final byte end = 3;

        byte state = init;
        StringBuilder lexemeBuilder = new StringBuilder().append(previous());
        StringBuilder numberBuilder = new StringBuilder().append(previous());
        char numType = 0;
        while (!isEnd() && state != end) {
            char cur = peek();
            switch (state) {
                case init:
                    if (isDigit(cur)) {
                        lexemeBuilder.append(cur);
                        numberBuilder.append(cur);
                        advance();
                    } else if (cur == '.') {
                        lexemeBuilder.append('.');
                        numberBuilder.append('.');
                        advance();
                        numType = 'd';
                        state = decimalPart;
                    } else if (isInVisible(cur) || SplitCharsSet.isSplitChar(cur)) {
                        state = end;
                    } else if (isNumberTypeFlag(cur)) {
                        lexemeBuilder.append(cur);
                        advance();
                        numType = cur;
                        state = numTypePart;
                    } else {
                        lexemeBuilder.append(cur);
                        advance();
                        throw QLException.reportScannerErr(script, pos, currentLine, currentCol,
                                lexemeBuilder.toString(), "INVALID_NUMBER", "invalid number");
                    }
                    continue;
                case decimalPart:
                    if (isDigit(cur)) {
                        lexemeBuilder.append(cur);
                        numberBuilder.append(cur);
                        advance();
                    } else if (isInVisible(cur) || SplitCharsSet.isSplitChar(cur)) {
                        state = end;
                    } else if (isNumberTypeFlag(cur)) {
                        lexemeBuilder.append(cur);
                        advance();
                        numType = cur;
                        state = numTypePart;
                    } else {
                        lexemeBuilder.append(cur);
                        advance();
                        throw QLException.reportScannerErr(script, pos, currentLine, currentCol,
                                lexemeBuilder.toString(), "INVALID_NUMBER", "invalid number");
                    }
                    continue;
                case numTypePart:
                    if (isInVisible(cur)) {
                        state = end;
                    } else {
                        lexemeBuilder.append(cur);
                        advance();
                        throw QLException.reportScannerErr(script, pos, currentLine, currentCol,
                                lexemeBuilder.toString(), "INVALID_NUMBER", "invalid number");
                    }
                    continue;
            }
        }

        String lexeme = lexemeBuilder.toString();
        String numberStr = numberBuilder.toString();
        Number literal;
        if (qlOptions.isPrecise()) {
            literal = new BigDecimal(numberStr);
        } else if (numType == 0) {
            literal = Integer.parseInt(numberStr);
        } else {
            switch (numType) {
                case 'f':
                case 'F':
                    literal = Float.parseFloat(numberStr);
                    break;
                case 'd':
                case 'D':
                    literal = Double.parseDouble(numberStr);
                    break;
                case 'l':
                case 'L':
                    literal = Long.parseLong(numberStr);
                    break;
                default:
                    throw QLException.reportScannerErr(script, pos, currentLine, currentCol,
                            lexeme, "INVALID_NUMBER", "invalid number");
            }
        }

        return newTokenWithLiteral(TokenType.NUMBER, lexeme, literal);
    }

    private boolean isNumberTypeFlag(char c) {
        return c == 'd' || c == 'D' ||
                c == 'f' || c == 'F' ||
                  c == 'l' || c == 'L';
    }

    private void advanceLineComment() {
        while (!isEnd() && advance() != '\n');
    }

    private void advanceMultiLineComment() {
        // keep init state to report error
        int startPos = pos;
        int startLine = currentLine;
        int startCol = currentCol;

        final byte init = 0;
        // *
        final byte preEnd = 1;
        // /
        final byte end = 2;

        byte state = init;
        while (!isEnd() && state != end) {
            char cur = advance();
            switch (state) {
                case init:
                    if (cur == '*') {
                        state = preEnd;
                    }
                    continue;
                case preEnd:
                    state = cur == '/'? end: init;
                    continue;
            }
        }
        if (state != end) {
            // multiline comment not close error
            throw QLException.reportScannerErr(script, startPos, startLine,
                    startCol, "/*",
                    "MULTILINE_COMMENT_NOT_CLOSE",
                    "multiline comment not close, please close it by /* ... */");
        }
    }

    private boolean matchAndAdvance(char expected) {
        if (isEnd() || script.charAt(pos) != expected) {
            return false;
        }
        advance();
        return true;
    }

    private Token newToken(TokenType tokenType, String lexeme) {
        return new Token(tokenType, lexeme, pos, currentLine, currentCol);
    }

    private Token newTokenWithLiteral(TokenType tokenType, String lexeme, Object literal) {
        return new Token(tokenType, lexeme, literal, pos, currentLine, currentCol);
    }

    private boolean isEnd() {
        return pos > script.length() - 1;
    }

    private char previous() {
        return script.charAt(pos-1);
    }

    private char peek() {
        return script.charAt(pos);
    }

    private char advance() {
        char curChar = script.charAt(pos++);
        if (curChar == '\n') {
            currentLine++;
            preCol = currentCol + 1;
            currentCol = 1;
        } else {
            currentCol++;
        }
        return curChar;
    }
}
