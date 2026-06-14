package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.qlexpress4.aparser.InterpolationMode.DISABLE;
import static com.alibaba.qlexpress4.aparser.InterpolationMode.SCRIPT;
import static com.alibaba.qlexpress4.aparser.InterpolationMode.VARIABLE;

public class QLexer {
    public static final int FOR = 1;
    
    public static final int IF = 2;
    
    public static final int ELSE = 3;
    
    public static final int WHILE = 4;
    
    public static final int BREAK = 5;
    
    public static final int CONTINUE = 6;
    
    public static final int RETURN = 7;
    
    public static final int FUNCTION = 8;
    
    public static final int MACRO = 9;
    
    public static final int IMPORT = 10;
    
    public static final int STATIC = 11;
    
    public static final int NEW = 12;
    
    public static final int SWITCH = 13;
    
    public static final int CASE = 14;
    
    public static final int DEFAULT = 15;
    
    public static final int BYTE = 16;
    
    public static final int SHORT = 17;
    
    public static final int INT = 18;
    
    public static final int LONG = 19;
    
    public static final int FLOAT = 20;
    
    public static final int DOUBLE = 21;
    
    public static final int CHAR = 22;
    
    public static final int BOOL = 23;
    
    public static final int NULL = 24;
    
    public static final int TRUE = 25;
    
    public static final int FALSE = 26;
    
    public static final int EXTENDS = 27;
    
    public static final int SUPER = 28;
    
    public static final int TRY = 29;
    
    public static final int CATCH = 30;
    
    public static final int FINALLY = 31;
    
    public static final int THROW = 32;
    
    public static final int THEN = 33;
    
    public static final int CLASS = 34;
    
    public static final int THIS = 35;
    
    public static final int QuoteStringLiteral = 36;
    
    public static final int IntegerLiteral = 37;
    
    public static final int FloatingPointLiteral = 38;
    
    public static final int IntegerOrFloatingLiteral = 39;
    
    public static final int LPAREN = 40;
    
    public static final int RPAREN = 41;
    
    public static final int LBRACE = 42;
    
    public static final int RBRACE = 43;
    
    public static final int LBRACK = 44;
    
    public static final int RBRACK = 45;
    
    public static final int DOT = 46;
    
    public static final int ARROW = 47;
    
    public static final int SEMI = 48;
    
    public static final int COMMA = 49;
    
    public static final int QUESTION = 50;
    
    public static final int COLON = 51;
    
    public static final int DCOLON = 52;
    
    public static final int GT = 53;
    
    public static final int LT = 54;
    
    public static final int EQ = 55;
    
    public static final int NOEQ = 56;
    
    public static final int RIGHSHIFT_ASSGIN = 57;
    
    public static final int RIGHSHIFT = 58;
    
    public static final int OPTIONAL_CHAINING = 59;
    
    public static final int SPREAD_CHAINING = 60;
    
    public static final int URSHIFT_ASSGIN = 61;
    
    public static final int URSHIFT = 62;
    
    public static final int LSHIFT_ASSGIN = 63;
    
    public static final int LEFTSHIFT = 64;
    
    public static final int GE = 65;
    
    public static final int LE = 66;
    
    public static final int DOTMUL = 67;
    
    public static final int CARET = 68;
    
    public static final int ADD_ASSIGN = 69;
    
    public static final int SUB_ASSIGN = 70;
    
    public static final int AND_ASSIGN = 71;
    
    public static final int OR_ASSIGN = 72;
    
    public static final int MUL_ASSIGN = 73;
    
    public static final int MOD_ASSIGN = 74;
    
    public static final int DIV_ASSIGN = 75;
    
    public static final int XOR_ASSIGN = 76;
    
    public static final int BANG = 77;
    
    public static final int TILDE = 78;
    
    public static final int ADD = 79;
    
    public static final int SUB = 80;
    
    public static final int MUL = 81;
    
    public static final int DIV = 82;
    
    public static final int BIT_AND = 83;
    
    public static final int BIT_OR = 84;
    
    public static final int MOD = 85;
    
    public static final int INC = 86;
    
    public static final int DEC = 87;
    
    public static final int NEWLINE = 88;
    
    public static final int OPID = 89;
    
    public static final int SELECTOR_START = 90;
    
    public static final int ID = 91;
    
    public static final int DOUBLE_QUOTE = 92;
    
    public static final int StaticStringCharacters = 93;
    
    public static final int DyStrExprStart = 94;
    
    public static final int DyStrText = 95;
    
    public static final int SelectorVariable_VANME = 96;
    
    public static final int CATCH_ALL = 97;
    
    private static final Map<String, Integer> KEYWORDS = new HashMap<>();
    
    static {
        KEYWORDS.put("for", FOR);
        KEYWORDS.put("if", IF);
        KEYWORDS.put("else", ELSE);
        KEYWORDS.put("while", WHILE);
        KEYWORDS.put("break", BREAK);
        KEYWORDS.put("continue", CONTINUE);
        KEYWORDS.put("return", RETURN);
        KEYWORDS.put("function", FUNCTION);
        KEYWORDS.put("macro", MACRO);
        KEYWORDS.put("import", IMPORT);
        KEYWORDS.put("static", STATIC);
        KEYWORDS.put("new", NEW);
        KEYWORDS.put("switch", SWITCH);
        KEYWORDS.put("case", CASE);
        KEYWORDS.put("default", DEFAULT);
        KEYWORDS.put("byte", BYTE);
        KEYWORDS.put("short", SHORT);
        KEYWORDS.put("int", INT);
        KEYWORDS.put("long", LONG);
        KEYWORDS.put("float", FLOAT);
        KEYWORDS.put("double", DOUBLE);
        KEYWORDS.put("char", CHAR);
        KEYWORDS.put("boolean", BOOL);
        KEYWORDS.put("null", NULL);
        KEYWORDS.put("true", TRUE);
        KEYWORDS.put("false", FALSE);
        KEYWORDS.put("extends", EXTENDS);
        KEYWORDS.put("super", SUPER);
        KEYWORDS.put("try", TRY);
        KEYWORDS.put("catch", CATCH);
        KEYWORDS.put("finally", FINALLY);
        KEYWORDS.put("throw", THROW);
        KEYWORDS.put("then", THEN);
        KEYWORDS.put("class", CLASS);
        KEYWORDS.put("this", THIS);
    }
    
    private final String script;
    
    private final ParserOperatorManager operatorManager;
    
    private final InterpolationMode interpolationMode;
    
    private final String selectorStart;
    
    private final String selectorEnd;
    
    private final boolean strictNewLines;
    
    private final List<Token> tokens = new ArrayList<>();
    
    private int p;
    
    private int line = 1;
    
    private int col;
    
    private QLexer(String script, ParserOperatorManager operatorManager, InterpolationMode interpolationMode,
        String selectorStart, String selectorEnd, boolean strictNewLines) {
        this.script = script == null ? "" : script;
        this.operatorManager = operatorManager;
        this.interpolationMode = interpolationMode;
        this.selectorStart = selectorStart;
        this.selectorEnd = selectorEnd;
        this.strictNewLines = strictNewLines;
    }
    
    public static List<Token> tokenize(String script, ParserOperatorManager operatorManager,
        InterpolationMode interpolationMode, String selectorStart, String selectorEnd, boolean strictNewLines) {
        QLexer lexer =
            new QLexer(script, operatorManager, interpolationMode, selectorStart, selectorEnd, strictNewLines);
        lexer.lexDefault(false);
        lexer.add(Token.EOF, "<EOF>", lexer.p, lexer.p - 1, lexer.line, lexer.col);
        return lexer.tokens;
    }
    
    private void lexDefault(boolean stopAtStringExpressionBrace) {
        int braceDepth = 1;
        while (!eof()) {
            char c = ch();
            if (stopAtStringExpressionBrace && c == '}') {
                Token token = add(RBRACE, "}", p, p, line, col);
                advance();
                braceDepth--;
                if (braceDepth == 0) {
                    return;
                }
                continue;
            }
            if (stopAtStringExpressionBrace && c == '{') {
                add(LBRACE, "{", p, p, line, col);
                advance();
                braceDepth++;
                continue;
            }
            if (c == ' ' || c == '\t' || c == '\f') {
                advance();
                continue;
            }
            if (c == '\r' || c == '\n') {
                readNewline();
                continue;
            }
            if (startsWith("//")) {
                skipLineComment();
                continue;
            }
            if (startsWith("/*")) {
                skipBlockComment();
                continue;
            }
            if (startsWith(selectorStart)) {
                readSelector();
                continue;
            }
            if (c == '\'') {
                readQuoteString();
                continue;
            }
            if (c == '"') {
                readDoubleQuoteString();
                continue;
            }
            if (Character.isDigit(c)
                || (c == '.' && p + 1 < script.length() && Character.isDigit(script.charAt(p + 1)))) {
                readNumber();
                continue;
            }
            if (isIdStart(c)) {
                readIdentifier();
                continue;
            }
            readOperatorOrPunctuation();
        }
        if (stopAtStringExpressionBrace) {
            scannerError(currentToken("<EOF>"), "mismatched input '<EOF>' expecting '}'");
        }
    }
    
    private void readNewline() {
        int start = p;
        int startLine = line;
        int startCol = col;
        if (ch() == '\r') {
            advance();
            if (!eof() && ch() == '\n') {
                advance();
            }
        }
        else {
            advance();
        }
        if (strictNewLines) {
            add(NEWLINE, script.substring(start, p), start, p - 1, startLine, startCol);
        }
    }
    
    private void skipLineComment() {
        while (!eof() && ch() != '\r' && ch() != '\n') {
            advance();
        }
    }
    
    private void skipBlockComment() {
        Token startToken = currentToken("/*");
        advance();
        advance();
        while (!eof()) {
            if (startsWith("*/")) {
                advance();
                advance();
                return;
            }
            advance();
        }
        scannerError(startToken, "unterminated comment");
    }
    
    private void readSelector() {
        int start = p;
        int startLine = line;
        int startCol = col;
        add(SELECTOR_START, selectorStart, start, start + selectorStart.length() - 1, startLine, startCol);
        for (int i = 0; i < selectorStart.length(); i++) {
            advance();
        }
        int contentStart = p;
        int contentLine = line;
        int contentCol = col;
        while (!eof()) {
            if (startsWith(selectorEnd)) {
                String text = script.substring(contentStart, p);
                add(SelectorVariable_VANME, text, contentStart, p - 1, contentLine, contentCol);
                for (int i = 0; i < selectorEnd.length(); i++) {
                    advance();
                }
                return;
            }
            if (ch() == '\n' || ch() == '\r') {
                scannerError(currentToken(script.substring(contentStart, p)), "unterminated selector");
            }
            advance();
        }
        scannerError(currentToken(script.substring(contentStart)), "unterminated selector");
    }
    
    private void readQuoteString() {
        int start = p;
        int startLine = line;
        int startCol = col;
        advance();
        while (!eof()) {
            char c = ch();
            advance();
            if (c == '\\') {
                if (!eof() && ch() == '\'') {
                    advance();
                }
                continue;
            }
            if (c == '\'') {
                add(QuoteStringLiteral, script.substring(start, p), start, p - 1, startLine, startCol);
                return;
            }
        }
        scannerError(new Token(QuoteStringLiteral, script.substring(start), start, p - 1, startLine, startCol),
            "unterminated string literal");
    }
    
    private void readDoubleQuoteString() {
        int quoteStart = p;
        int quoteLine = line;
        int quoteCol = col;
        add(DOUBLE_QUOTE, "\"", quoteStart, quoteStart, quoteLine, quoteCol);
        advance();
        if (interpolationMode == DISABLE) {
            int textStart = p;
            int textLine = line;
            int textCol = col;
            while (!eof()) {
                char c = ch();
                if (c == '"') {
                    if (p > textStart) {
                        add(StaticStringCharacters,
                            script.substring(textStart, p),
                            textStart,
                            p - 1,
                            textLine,
                            textCol);
                    }
                    add(DOUBLE_QUOTE, "\"", p, p, line, col);
                    advance();
                    return;
                }
                advance();
                if (c == '\\' && !eof()) {
                    advance();
                }
            }
            scannerError(currentToken(script.substring(textStart)), "unterminated string literal");
            return;
        }
        while (!eof()) {
            int textStart = p;
            int textLine = line;
            int textCol = col;
            while (!eof() && ch() != '"' && !startsWith("${")) {
                char c = ch();
                advance();
                if (c == '\\' && !eof()) {
                    advance();
                }
            }
            if (p > textStart) {
                add(DyStrText, script.substring(textStart, p), textStart, p - 1, textLine, textCol);
            }
            if (eof()) {
                scannerError(currentToken(script.substring(quoteStart)), "unterminated string literal");
            }
            if (ch() == '"') {
                add(DOUBLE_QUOTE, "\"", p, p, line, col);
                advance();
                return;
            }
            int exprStart = p;
            int exprLine = line;
            int exprCol = col;
            add(DyStrExprStart, "${", exprStart, exprStart + 1, exprLine, exprCol);
            advance();
            advance();
            if (interpolationMode == VARIABLE) {
                readVariableStringExpression();
            }
            else if (interpolationMode == SCRIPT) {
                lexDefault(true);
            }
        }
    }
    
    private void readVariableStringExpression() {
        int contentStart = p;
        int contentLine = line;
        int contentCol = col;
        while (!eof()) {
            if (startsWith(selectorEnd)) {
                add(SelectorVariable_VANME,
                    script.substring(contentStart, p),
                    contentStart,
                    p - 1,
                    contentLine,
                    contentCol);
                for (int i = 0; i < selectorEnd.length(); i++) {
                    advance();
                }
                return;
            }
            if (ch() == '\r' || ch() == '\n') {
                scannerError(currentToken(script.substring(contentStart, p)), "unterminated selector");
            }
            advance();
        }
        scannerError(currentToken(script.substring(contentStart)), "unterminated selector");
    }
    
    private void readNumber() {
        int start = p;
        int startLine = line;
        int startCol = col;
        int type = IntegerOrFloatingLiteral;
        if (ch() == '.') {
            advance();
            readDigits();
            readExponent();
            readFloatSuffix();
            add(FloatingPointLiteral, script.substring(start, p), start, p - 1, startLine, startCol);
            return;
        }
        if (startsWith("0x") || startsWith("0X")) {
            advance();
            advance();
            readDigitsForRadix(16);
            readIntegerSuffix();
            add(IntegerLiteral, script.substring(start, p), start, p - 1, startLine, startCol);
            return;
        }
        if (startsWith("0b") || startsWith("0B")) {
            advance();
            advance();
            readDigitsForRadix(2);
            readIntegerSuffix();
            add(IntegerLiteral, script.substring(start, p), start, p - 1, startLine, startCol);
            return;
        }
        readDigits();
        boolean hasExponent = false;
        if (!eof() && ch() == '.' && shouldConsumeDecimalDot()) {
            advance();
            readDigits();
            readExponent();
            readFloatSuffix();
        }
        else if (readExponent()) {
            hasExponent = true;
            readFloatSuffix();
        }
        else if (!eof() && isFloatSuffix(ch())) {
            readFloatSuffix();
            type = FloatingPointLiteral;
        }
        else {
            readIntegerSuffix();
        }
        if (hasExponent) {
            type = FloatingPointLiteral;
        }
        add(type, script.substring(start, p), start, p - 1, startLine, startCol);
    }
    
    private boolean shouldConsumeDecimalDot() {
        if (p + 2 >= script.length()) {
            return true;
        }
        char c1 = script.charAt(p + 1);
        char c2 = script.charAt(p + 2);
        return !(isAsciiLetter(c1) && isAsciiLetter(c2));
    }
    
    private void readDigits() {
        while (!eof() && (Character.isDigit(ch()) || ch() == '_')) {
            advance();
        }
    }
    
    private void readDigitsForRadix(int radix) {
        while (!eof() && (Character.digit(ch(), radix) >= 0 || ch() == '_')) {
            advance();
        }
    }
    
    private boolean readExponent() {
        if (eof() || (ch() != 'e' && ch() != 'E')) {
            return false;
        }
        int save = p;
        advance();
        if (!eof() && (ch() == '+' || ch() == '-')) {
            advance();
        }
        if (eof() || !Character.isDigit(ch())) {
            p = save;
            return false;
        }
        readDigits();
        return true;
    }
    
    private void readIntegerSuffix() {
        if (!eof() && (ch() == 'l' || ch() == 'L')) {
            advance();
        }
    }
    
    private void readFloatSuffix() {
        if (!eof() && isFloatSuffix(ch())) {
            advance();
        }
    }
    
    private boolean isFloatSuffix(char c) {
        return c == 'f' || c == 'F' || c == 'd' || c == 'D';
    }
    
    private void readIdentifier() {
        int start = p;
        int startLine = line;
        int startCol = col;
        advance();
        while (!eof() && isIdPart(ch())) {
            advance();
        }
        String text = script.substring(start, p);
        Integer keywordType = KEYWORDS.get(text);
        int type = keywordType == null ? ID : keywordType;
        if (type == ID && operatorManager != null) {
            Integer aliasType = operatorManager.getAlias(text);
            if (aliasType != null) {
                type = aliasType;
            }
        }
        add(type, text, start, p - 1, startLine, startCol);
    }
    
    private void readOperatorOrPunctuation() {
        int start = p;
        int startLine = line;
        int startCol = col;
        String three = p + 3 <= script.length() ? script.substring(p, p + 3) : "";
        String four = p + 4 <= script.length() ? script.substring(p, p + 4) : "";
        if (">>>=".equals(four)) {
            fixed(URSHIFT_ASSGIN, 4, start, startLine, startCol);
            return;
        }
        if (">>>".equals(three)) {
            fixed(URSHIFT, 3, start, startLine, startCol);
            return;
        }
        String two = p + 2 <= script.length() ? script.substring(p, p + 2) : "";
        if (">>=".equals(three)) {
            fixed(RIGHSHIFT_ASSGIN, 3, start, startLine, startCol);
            return;
        }
        if ("<<=".equals(three)) {
            fixed(LSHIFT_ASSGIN, 3, start, startLine, startCol);
            return;
        }
        if ("->".equals(two)) {
            fixed(ARROW, 2, start, startLine, startCol);
            return;
        }
        if ("::".equals(two)) {
            fixed(DCOLON, 2, start, startLine, startCol);
            return;
        }
        if ("<>".equals(two)) {
            fixed(NOEQ, 2, start, startLine, startCol);
            return;
        }
        if (">>".equals(two)) {
            fixed(RIGHSHIFT, 2, start, startLine, startCol);
            return;
        }
        if ("<<".equals(two)) {
            fixed(LEFTSHIFT, 2, start, startLine, startCol);
            return;
        }
        if (">=".equals(two)) {
            fixed(GE, 2, start, startLine, startCol);
            return;
        }
        if ("<=".equals(two)) {
            fixed(LE, 2, start, startLine, startCol);
            return;
        }
        if ("?.".equals(two)) {
            fixed(OPTIONAL_CHAINING, 2, start, startLine, startCol);
            return;
        }
        if ("*.".equals(two)) {
            fixed(SPREAD_CHAINING, 2, start, startLine, startCol);
            return;
        }
        if (".*".equals(two)) {
            fixed(DOTMUL, 2, start, startLine, startCol);
            return;
        }
        if ("+=".equals(two)) {
            fixed(ADD_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("-=".equals(two)) {
            fixed(SUB_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("&=".equals(two)) {
            fixed(AND_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("|=".equals(two)) {
            fixed(OR_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("*=".equals(two)) {
            fixed(MUL_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("%=".equals(two)) {
            fixed(MOD_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("/=".equals(two)) {
            fixed(DIV_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("^=".equals(two)) {
            fixed(XOR_ASSIGN, 2, start, startLine, startCol);
            return;
        }
        if ("++".equals(two)) {
            fixed(INC, 2, start, startLine, startCol);
            return;
        }
        if ("--".equals(two)) {
            fixed(DEC, 2, start, startLine, startCol);
            return;
        }
        if ("==".equals(two)) {
            fixed(OPID, 2, start, startLine, startCol);
            return;
        }
        if ("!=".equals(two)) {
            fixed(OPID, 2, start, startLine, startCol);
            return;
        }
        if ("&&".equals(two)) {
            fixed(OPID, 2, start, startLine, startCol);
            return;
        }
        if ("||".equals(two)) {
            fixed(OPID, 2, start, startLine, startCol);
            return;
        }
        if (isCustomOperatorStart(ch()) && p + 1 < script.length() && isCustomOperatorPart(script.charAt(p + 1))) {
            advance();
            while (!eof() && isCustomOperatorPart(ch())) {
                advance();
            }
            add(OPID, script.substring(start, p), start, p - 1, startLine, startCol);
            return;
        }
        switch (ch()) {
            case '(':
                fixed(LPAREN, 1, start, startLine, startCol);
                return;
            case ')':
                fixed(RPAREN, 1, start, startLine, startCol);
                return;
            case '{':
                fixed(LBRACE, 1, start, startLine, startCol);
                return;
            case '}':
                fixed(RBRACE, 1, start, startLine, startCol);
                return;
            case '[':
                fixed(LBRACK, 1, start, startLine, startCol);
                return;
            case ']':
                fixed(RBRACK, 1, start, startLine, startCol);
                return;
            case '.':
                fixed(DOT, 1, start, startLine, startCol);
                return;
            case ';':
                fixed(SEMI, 1, start, startLine, startCol);
                return;
            case ',':
                fixed(COMMA, 1, start, startLine, startCol);
                return;
            case '?':
                fixed(QUESTION, 1, start, startLine, startCol);
                return;
            case ':':
                fixed(COLON, 1, start, startLine, startCol);
                return;
            case '>':
                fixed(GT, 1, start, startLine, startCol);
                return;
            case '<':
                fixed(LT, 1, start, startLine, startCol);
                return;
            case '=':
                fixed(EQ, 1, start, startLine, startCol);
                return;
            case '^':
                fixed(CARET, 1, start, startLine, startCol);
                return;
            case '!':
                fixed(BANG, 1, start, startLine, startCol);
                return;
            case '~':
                fixed(TILDE, 1, start, startLine, startCol);
                return;
            case '+':
                fixed(ADD, 1, start, startLine, startCol);
                return;
            case '-':
                fixed(SUB, 1, start, startLine, startCol);
                return;
            case '*':
                fixed(MUL, 1, start, startLine, startCol);
                return;
            case '/':
                fixed(DIV, 1, start, startLine, startCol);
                return;
            case '&':
                fixed(BIT_AND, 1, start, startLine, startCol);
                return;
            case '|':
                fixed(BIT_OR, 1, start, startLine, startCol);
                return;
            case '%':
                fixed(MOD, 1, start, startLine, startCol);
                return;
            default:
                fixed(CATCH_ALL, 1, start, startLine, startCol);
        }
    }
    
    private boolean isCustomOperatorStart(char c) {
        return c == '^' || c == '~' || c == '&' || c == '|' || c == '*' || c == '%' || c == '=' || c == '!' || c == '/'
            || c == '+' || c == '-' || c == '?' || c == '.';
    }
    
    private boolean isCustomOperatorPart(char c) {
        return isCustomOperatorStart(c) || c == '<' || c == '>' || c == ':';
    }
    
    private void fixed(int type, int length, int start, int startLine, int startCol) {
        for (int i = 0; i < length; i++) {
            advance();
        }
        add(type, script.substring(start, start + length), start, start + length - 1, startLine, startCol);
    }
    
    private Token add(int type, String text, int startIndex, int stopIndex, int line, int col) {
        Token token = new Token(type, text, startIndex, Math.max(stopIndex, startIndex), line, col);
        tokens.add(token);
        return token;
    }
    
    private boolean eof() {
        return p >= script.length();
    }
    
    private char ch() {
        return script.charAt(p);
    }
    
    private boolean startsWith(String text) {
        return script.startsWith(text, p);
    }
    
    private void advance() {
        if (eof()) {
            return;
        }
        char c = script.charAt(p++);
        if (c == '\r') {
            if (p < script.length() && script.charAt(p) == '\n') {
                p++;
            }
            line++;
            col = 0;
        }
        else if (c == '\n') {
            line++;
            col = 0;
        }
        else {
            col++;
        }
    }
    
    private boolean isIdStart(char c) {
        return c == '#' || c == '@' || c == '$' || c == '_' || Character.isLetter(c);
    }
    
    private boolean isIdPart(char c) {
        return isIdStart(c) || Character.isDigit(c) || c == '\u3001' || c == '\uFF08' || c == '\uFF09' || c == '\u3010'
            || c == '\u3011';
    }
    
    private boolean isAsciiLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    private Token currentToken(String text) {
        return new Token(CATCH_ALL, text, p, Math.max(p, p + text.length() - 1), line, col);
    }
    
    private void scannerError(Token token, String reason) {
        throw QLException.reportScannerErr(script,
            token.getStartIndex(),
            token.getLine(),
            token.getCharPositionInLine() + 1,
            token.getText(),
            QLErrorCodes.SYNTAX_ERROR.name(),
            reason);
    }
}
