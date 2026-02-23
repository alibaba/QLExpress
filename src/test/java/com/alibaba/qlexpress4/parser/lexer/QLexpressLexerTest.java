package com.alibaba.qlexpress4.parser.lexer;

import com.alibaba.qlexpress4.common.InterpolationMode;
import com.alibaba.qlexpress4.parser.token.Token;
import com.alibaba.qlexpress4.parser.token.TokenType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive unit tests for QLexpressLexer.
 * Tests all token types including identifiers, literals, operators, keywords, delimiters,
 * line/column tracking, interpolation modes, and newline handling.
 */
public class QLexpressLexerTest {
    
    // ==================== Helper Methods ====================
    
    /**
     * Creates a lexer with default settings (non-strict newlines).
     */
    private QLexpressLexer createLexer(String input) {
        return new QLexpressLexer(input, null, InterpolationMode.SCRIPT, false, "${", "}");
    }
    
    /**
     * Creates a lexer with specified newline handling.
     */
    private QLexpressLexer createLexer(String input, boolean strictNewLines) {
        return new QLexpressLexer(input, null, InterpolationMode.SCRIPT, strictNewLines, "${", "}");
    }
    
    /**
     * Creates a lexer with specified interpolation mode.
     */
    private QLexpressLexer createLexer(String input, InterpolationMode mode) {
        return new QLexpressLexer(input, null, mode, false, "${", "}");
    }
    
    /**
     * Tokenizes input and returns the token list (excluding EOF).
     */
    private List<Token> tokenize(String input) {
        QLexpressLexer lexer = createLexer(input);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF token for easier testing
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        return tokens;
    }
    
    /**
     * Asserts that the token at index has the expected type and value.
     */
    private void assertToken(List<Token> tokens, int index, TokenType type, String value) {
        assertTrue("Not enough tokens", tokens.size() > index);
        Token token = tokens.get(index);
        assertEquals("Token type mismatch at index " + index, type, token.getType());
        assertEquals("Token value mismatch at index " + index, value, token.getValue());
    }
    
    /**
     * Asserts that the token at index has the expected type and location.
     */
    private void assertTokenLocation(List<Token> tokens, int index, int line, int column) {
        assertTrue("Not enough tokens", tokens.size() > index);
        Token token = tokens.get(index);
        assertEquals("Token line mismatch at index " + index, line, token.getLine());
        assertEquals("Token column mismatch at index " + index, column, token.getColumn());
    }
    
    // ==================== Identifier Tests ====================
    
    @Test
    public void testSimpleIdentifier() {
        List<Token> tokens = tokenize("variable");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "variable");
    }
    
    @Test
    public void testIdentifierWithUnderscore() {
        List<Token> tokens = tokenize("_myVar");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "_myVar");
    }
    
    @Test
    public void testIdentifierWithDollar() {
        List<Token> tokens = tokenize("$var");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "$var");
    }
    
    @Test
    public void testIdentifierWithAtSign() {
        List<Token> tokens = tokenize("@var");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "@var");
    }
    
    @Test
    public void testIdentifierWithHash() {
        List<Token> tokens = tokenize("#var");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "#var");
    }
    
    @Test
    public void testIdentifierStartingWithKeyword() {
        List<Token> tokens = tokenize("intValue");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "intValue");
    }
    
    @Test
    public void testCamelCaseIdentifier() {
        List<Token> tokens = tokenize("myVariableName");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "myVariableName");
    }
    
    @Test
    public void testMultipleIdentifiers() {
        List<Token> tokens = tokenize("a b c");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "a");
        assertToken(tokens, 1, TokenType.ID, "b");
        assertToken(tokens, 2, TokenType.ID, "c");
    }
    
    @Test
    public void testUnicodeIdentifier() {
        List<Token> tokens = tokenize("变量");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "变量");
    }
    
    // ==================== Keyword Tests ====================
    
    @Test
    public void testControlFlowKeywords() {
        List<Token> tokens = tokenize("if else while for");
        assertEquals(4, tokens.size());
        assertToken(tokens, 0, TokenType.IF, "if");
        assertToken(tokens, 1, TokenType.ELSE, "else");
        assertToken(tokens, 2, TokenType.WHILE, "while");
        assertToken(tokens, 3, TokenType.FOR, "for");
    }
    
    @Test
    public void testJumpKeywords() {
        List<Token> tokens = tokenize("break continue return");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.BREAK, "break");
        assertToken(tokens, 1, TokenType.CONTINUE, "continue");
        assertToken(tokens, 2, TokenType.RETURN, "return");
    }
    
    @Test
    public void testSwitchKeywords() {
        List<Token> tokens = tokenize("switch case default");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.SWITCH, "switch");
        assertToken(tokens, 1, TokenType.CASE, "case");
        assertToken(tokens, 2, TokenType.DEFAULT, "default");
    }
    
    @Test
    public void testExceptionKeywords() {
        List<Token> tokens = tokenize("try catch finally throw");
        assertEquals(4, tokens.size());
        assertToken(tokens, 0, TokenType.TRY, "try");
        assertToken(tokens, 1, TokenType.CATCH, "catch");
        assertToken(tokens, 2, TokenType.FINALLY, "finally");
        assertToken(tokens, 3, TokenType.THROW, "throw");
    }
    
    @Test
    public void testPrimitiveTypeKeywords() {
        List<Token> tokens = tokenize("byte short int long float double char boolean");
        assertEquals(8, tokens.size());
        assertToken(tokens, 0, TokenType.BYTE, "byte");
        assertToken(tokens, 1, TokenType.SHORT, "short");
        assertToken(tokens, 2, TokenType.INT, "int");
        assertToken(tokens, 3, TokenType.LONG, "long");
        assertToken(tokens, 4, TokenType.FLOAT, "float");
        assertToken(tokens, 5, TokenType.DOUBLE, "double");
        assertToken(tokens, 6, TokenType.CHAR, "char");
        assertToken(tokens, 7, TokenType.BOOLEAN, "boolean");
    }
    
    @Test
    public void testLiteralKeywords() {
        List<Token> tokens = tokenize("null true false");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.NULL, "null");
        assertToken(tokens, 1, TokenType.TRUE, "true");
        assertToken(tokens, 2, TokenType.FALSE, "false");
    }
    
    @Test
    public void testObjectKeywords() {
        List<Token> tokens = tokenize("new this super extends class");
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.NEW, "new");
        assertToken(tokens, 1, TokenType.THIS, "this");
        assertToken(tokens, 2, TokenType.SUPER, "super");
        assertToken(tokens, 3, TokenType.EXTENDS, "extends");
        assertToken(tokens, 4, TokenType.CLASS, "class");
    }
    
    @Test
    public void testOtherKeywords() {
        List<Token> tokens = tokenize("import static function macro then");
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.IMPORT, "import");
        assertToken(tokens, 1, TokenType.STATIC, "static");
        assertToken(tokens, 2, TokenType.FUNCTION, "function");
        assertToken(tokens, 3, TokenType.MACRO, "macro");
        assertToken(tokens, 4, TokenType.THEN, "then");
    }
    
    // ==================== Integer Literal Tests ====================
    
    @Test
    public void testDecimalInteger() {
        List<Token> tokens = tokenize("42");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "42");
    }
    
    @Test
    public void testZero() {
        List<Token> tokens = tokenize("0");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "0");
    }
    
    @Test
    public void testLargeInteger() {
        List<Token> tokens = tokenize("1234567890");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1234567890");
    }
    
    @Test
    public void testHexInteger() {
        List<Token> tokens = tokenize("0xFF");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_LITERAL, "0xFF");
    }
    
    @Test
    public void testHexIntegerLowercase() {
        List<Token> tokens = tokenize("0xff");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_LITERAL, "0xff");
    }
    
    @Test
    public void testBinaryInteger() {
        List<Token> tokens = tokenize("0b1010");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_LITERAL, "0b1010");
    }
    
    @Test
    public void testBinaryIntegerUppercase() {
        List<Token> tokens = tokenize("0B1010");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_LITERAL, "0B1010");
    }
    
    @Test
    public void testOctalInteger() {
        List<Token> tokens = tokenize("0755");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_LITERAL, "0755");
    }
    
    @Test
    public void testLongInteger() {
        List<Token> tokens = tokenize("42L");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "42L");
    }
    
    @Test
    public void testLongIntegerLowercase() {
        List<Token> tokens = tokenize("42l");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "42l");
    }
    
    @Test
    public void testIntegerWithDigitSeparators() {
        List<Token> tokens = tokenize("1_000_000");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1000000");
    }
    
    @Test
    public void testHexWithDigitSeparators() {
        List<Token> tokens = tokenize("0xFF_FF");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_LITERAL, "0xFFFF");
    }
    
    // ==================== Float Literal Tests ====================
    
    @Test
    public void testSimpleFloat() {
        List<Token> tokens = tokenize("3.14");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "3.14");
    }
    
    @Test
    public void testFloatWithLeadingDecimal() {
        List<Token> tokens = tokenize("0.5");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "0.5");
    }
    
    @Test
    public void testFloatWithExponent() {
        List<Token> tokens = tokenize("1.5e10");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "1.5e10");
    }
    
    @Test
    public void testFloatWithExponentLowercaseE() {
        List<Token> tokens = tokenize("1.5E10");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "1.5E10");
    }
    
    @Test
    public void testFloatWithNegativeExponent() {
        List<Token> tokens = tokenize("1.5e-10");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "1.5e-10");
    }
    
    @Test
    public void testFloatWithPositiveExponent() {
        List<Token> tokens = tokenize("1.5e+10");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "1.5e+10");
    }
    
    @Test
    public void testFloatWithoutDecimalPoint() {
        List<Token> tokens = tokenize("1e10");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "1e10");
    }
    
    @Test
    public void testFloatWithFSuffix() {
        List<Token> tokens = tokenize("3.14f");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "3.14f");
    }
    
    @Test
    public void testFloatWithFSuffixUppercase() {
        List<Token> tokens = tokenize("3.14F");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "3.14F");
    }
    
    @Test
    public void testFloatWithDSuffix() {
        List<Token> tokens = tokenize("3.14d");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "3.14d");
    }
    
    @Test
    public void testFloatWithDSuffixUppercase() {
        List<Token> tokens = tokenize("3.14D");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "3.14D");
    }
    
    @Test
    public void testFloatWithDigitSeparators() {
        List<Token> tokens = tokenize("1_000.555");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.FLOATING_POINT_LITERAL, "1000.555");
    }
    
    // ==================== String Literal Tests ====================
    
    @Test
    public void testSingleQuoteString() {
        List<Token> tokens = tokenize("'hello'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "hello");
    }
    
    @Test
    public void testEmptySingleQuoteString() {
        List<Token> tokens = tokenize("''");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapeN() {
        List<Token> tokens = tokenize("'hello\\nworld'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "hello\nworld");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapeT() {
        List<Token> tokens = tokenize("'hello\\tworld'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "hello\tworld");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapeR() {
        List<Token> tokens = tokenize("'hello\\rworld'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "hello\rworld");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapeB() {
        List<Token> tokens = tokenize("'hello\\bworld'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "hello\bworld");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapeF() {
        List<Token> tokens = tokenize("'hello\\fworld'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "hello\fworld");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapedQuote() {
        List<Token> tokens = tokenize("'it\\'s'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "it's");
    }
    
    @Test
    public void testSingleQuoteStringWithEscapedDoubleQuote() {
        List<Token> tokens = tokenize("'\\\"'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "\"");
    }
    
    @Test
    public void testSingleQuoteStringWithBackslash() {
        List<Token> tokens = tokenize("'\\\\'");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "\\");
    }
    
    @Test
    public void testDoubleQuoteString() {
        List<Token> tokens = tokenize("\"hello\"");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOUBLE_QUOTE, "hello");
    }
    
    @Test
    public void testEmptyDoubleQuoteString() {
        List<Token> tokens = tokenize("\"\"");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOUBLE_QUOTE, "");
    }
    
    @Test
    public void testDoubleQuoteStringWithSpecialChars() {
        List<Token> tokens = tokenize("\"hello ${world}\"");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOUBLE_QUOTE, "hello ${world}");
    }
    
    // ==================== Arithmetic Operator Tests ====================
    
    @Test
    public void testArithmeticOperators() {
        List<Token> tokens = tokenize("+ - * / %");
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.ADD, "+");
        assertToken(tokens, 1, TokenType.SUB, "-");
        assertToken(tokens, 2, TokenType.MUL, "*");
        assertToken(tokens, 3, TokenType.DIV, "/");
        assertToken(tokens, 4, TokenType.MOD, "%");
    }
    
    @Test
    public void testIncrementDecrement() {
        List<Token> tokens = tokenize("++ --");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.INC, "++");
        assertToken(tokens, 1, TokenType.DEC, "--");
    }
    
    // ==================== Bitwise Operator Tests ====================
    
    @Test
    public void testBitwiseOperators() {
        List<Token> tokens = tokenize("& | ^ ~");
        assertEquals(4, tokens.size());
        assertToken(tokens, 0, TokenType.BIT_AND, "&");
        assertToken(tokens, 1, TokenType.BIT_OR, "|");
        assertToken(tokens, 2, TokenType.BIT_XOR, "^");
        assertToken(tokens, 3, TokenType.TILDE, "~");
    }
    
    @Test
    public void testShiftOperators() {
        List<Token> tokens = tokenize("<< >> >>>");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.LEFTSHIFT, "<<");
        assertToken(tokens, 1, TokenType.RIGHSHIFT, ">>");
        assertToken(tokens, 2, TokenType.URSHIFT, ">>>");
    }
    
    // ==================== Logical Operator Tests ====================
    
    @Test
    public void testLogicalNot() {
        List<Token> tokens = tokenize("!");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.BANG, "!");
    }
    
    @Test
    public void testLogicalAnd() {
        List<Token> tokens = tokenize("&&");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.OPID, "&&");
    }
    
    @Test
    public void testLogicalOr() {
        List<Token> tokens = tokenize("||");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.OPID, "||");
    }
    
    // ==================== Comparison Operator Tests ====================
    
    @Test
    public void testComparisonOperators() {
        List<Token> tokens = tokenize("< > <= >=");
        assertEquals(4, tokens.size());
        assertToken(tokens, 0, TokenType.LT, "<");
        assertToken(tokens, 1, TokenType.GT, ">");
        assertToken(tokens, 2, TokenType.LE, "<=");
        assertToken(tokens, 3, TokenType.GE, ">=");
    }
    
    @Test
    public void testEqualityOperators() {
        List<Token> tokens = tokenize("== !=");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.OPID, "==");
        assertToken(tokens, 1, TokenType.OPID, "!=");
    }
    
    @Test
    public void testNotEqualsOperator() {
        List<Token> tokens = tokenize("<>");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.NOEQ, "<>");
    }
    
    // ==================== Assignment Operator Tests ====================
    
    @Test
    public void testSimpleAssignment() {
        List<Token> tokens = tokenize("=");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.EQ, "=");
    }
    
    @Test
    public void testCompoundAssignments() {
        List<Token> tokens = tokenize("+= -= *= /= %=");
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.ADD_ASSIGN, "+=");
        assertToken(tokens, 1, TokenType.SUB_ASSIGN, "-=");
        assertToken(tokens, 2, TokenType.MUL_ASSIGN, "*=");
        assertToken(tokens, 3, TokenType.DIV_ASSIGN, "/=");
        assertToken(tokens, 4, TokenType.MOD_ASSIGN, "%=");
    }
    
    @Test
    public void testBitwiseAssignments() {
        List<Token> tokens = tokenize("&= |= ^=");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.AND_ASSIGN, "&=");
        assertToken(tokens, 1, TokenType.OR_ASSIGN, "|=");
        assertToken(tokens, 2, TokenType.XOR_ASSIGN, "^=");
    }
    
    @Test
    public void testShiftAssignments() {
        List<Token> tokens = tokenize("<<= >>= >>>=");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.LSHIFT_ASSIGN, "<<=");
        assertToken(tokens, 1, TokenType.RSHIFT_ASSIGN, ">>=");
        assertToken(tokens, 2, TokenType.URSHIFT_ASSIGN, ">>>=");
    }
    
    // ==================== Other Operator Tests ====================
    
    @Test
    public void testDotOperators() {
        List<Token> tokens = tokenize(". .");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.DOT, ".");
        assertToken(tokens, 1, TokenType.DOT, ".");
    }
    
    @Test
    public void testDotMul() {
        List<Token> tokens = tokenize(".*");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOTMUL, ".*");
    }
    
    @Test
    public void testOptionalChaining() {
        List<Token> tokens = tokenize("?.");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.OPTIONAL_CHAINING, "?.");
    }
    
    @Test
    public void testSpreadChaining() {
        List<Token> tokens = tokenize("*.");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.SPREAD_CHAINING, "*.");
    }
    
    @Test
    public void testDoubleColon() {
        List<Token> tokens = tokenize("::");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DCOLON, "::");
    }
    
    @Test
    public void testArrow() {
        List<Token> tokens = tokenize("->");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ARROW, "->");
    }
    
    @Test
    public void testQuestionColon() {
        List<Token> tokens = tokenize("?");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUESTION, "?");
    }
    
    @Test
    public void testColon() {
        List<Token> tokens = tokenize(":");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.COLON, ":");
    }
    
    // ==================== Delimiter Tests ====================
    
    @Test
    public void testParentheses() {
        List<Token> tokens = tokenize("()");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.LPAREN, "(");
        assertToken(tokens, 1, TokenType.RPAREN, ")");
    }
    
    @Test
    public void testBraces() {
        List<Token> tokens = tokenize("{}");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.LBRACE, "{");
        assertToken(tokens, 1, TokenType.RBRACE, "}");
    }
    
    @Test
    public void testBrackets() {
        List<Token> tokens = tokenize("[]");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.LBRACK, "[");
        assertToken(tokens, 1, TokenType.RBRACK, "]");
    }
    
    @Test
    public void testSemicolon() {
        List<Token> tokens = tokenize(";");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.SEMI, ";");
    }
    
    @Test
    public void testComma() {
        List<Token> tokens = tokenize(",");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.COMMA, ",");
    }
    
    // ==================== Selector Token Tests ====================
    
    @Test
    public void testSelectorStartDollar() {
        List<Token> tokens = tokenize("${");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.SELECTOR_START, "${");
    }
    
    @Test
    public void testSelectorStartHash() {
        List<Token> tokens = tokenize("#{");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.SELECTOR_START, "#{");
    }
    
    @Test
    public void testSelectorStartDollarBracket() {
        List<Token> tokens = tokenize("$[");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.SELECTOR_START, "$[");
    }
    
    @Test
    public void testSelectorStartHashBracket() {
        List<Token> tokens = tokenize("#[");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.SELECTOR_START, "#[");
    }
    
    // ==================== Comment Tests ====================
    
    @Test
    public void testLineComment() {
        List<Token> tokens = tokenize("42 // this is a comment\n43");
        // Line comment is skipped, newline is skipped in non-strict mode
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "42");
        assertToken(tokens, 1, TokenType.INTEGER_OR_FLOATING_LITERAL, "43");
    }
    
    @Test
    public void testBlockComment() {
        List<Token> tokens = tokenize("42 /* comment */ 43");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "42");
        assertToken(tokens, 1, TokenType.INTEGER_OR_FLOATING_LITERAL, "43");
    }
    
    @Test
    public void testMultilineBlockComment() {
        List<Token> tokens = tokenize("42 /* multi\nline\ncomment */ 43");
        assertEquals(2, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "42");
        assertToken(tokens, 1, TokenType.INTEGER_OR_FLOATING_LITERAL, "43");
    }
    
    // ==================== Line/Column Tracking Tests ====================
    
    @Test
    public void testFirstTokenLineColumn() {
        List<Token> tokens = tokenize("42");
        assertEquals(1, tokens.size());
        assertTokenLocation(tokens, 0, 1, 1);
    }
    
    @Test
    public void testMultipleTokensSameLine() {
        List<Token> tokens = tokenize("1 + 2");
        assertEquals(3, tokens.size());
        assertTokenLocation(tokens, 0, 1, 1); // 1
        assertTokenLocation(tokens, 1, 1, 3); // +
        assertTokenLocation(tokens, 2, 1, 5); // 2
    }
    
    @Test
    public void testMultipleLines() {
        // Use strict mode to see newlines
        QLexpressLexer lexer = createLexer("1\n+\n2", true);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        assertEquals(5, tokens.size()); // 1, \n, +, \n, 2
        assertTokenLocation(tokens, 0, 1, 1); // 1
        assertTokenLocation(tokens, 1, 1, 2); // \n
        assertTokenLocation(tokens, 2, 2, 1); // +
        assertTokenLocation(tokens, 3, 2, 2); // \n
        assertTokenLocation(tokens, 4, 3, 1); // 2
    }
    
    @Test
    public void testTokenPositionWithIndent() {
        List<Token> tokens = tokenize("    42");
        assertEquals(1, tokens.size());
        assertTokenLocation(tokens, 0, 1, 5);
    }
    
    @Test
    public void testTokenPositionAfterTab() {
        List<Token> tokens = tokenize("\t42");
        assertEquals(1, tokens.size());
        assertTokenLocation(tokens, 0, 1, 2);
    }
    
    // ==================== Newline Mode Tests ====================
    
    @Test
    public void testNonStrictNewlineMode() {
        QLexpressLexer lexer = createLexer("1\n2\n3", false);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        // Newlines should be skipped in non-strict mode
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1");
        assertToken(tokens, 1, TokenType.INTEGER_OR_FLOATING_LITERAL, "2");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "3");
    }
    
    @Test
    public void testStrictNewlineMode() {
        QLexpressLexer lexer = createLexer("1\n2\n3", true);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        // Newlines should be included in strict mode
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1");
        assertToken(tokens, 1, TokenType.NEWLINE, "\n");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "2");
        assertToken(tokens, 3, TokenType.NEWLINE, "\n");
        assertToken(tokens, 4, TokenType.INTEGER_OR_FLOATING_LITERAL, "3");
    }
    
    @Test
    public void testCarriageReturnNewline() {
        QLexpressLexer lexer = createLexer("1\r\n2", true);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1");
        assertToken(tokens, 1, TokenType.NEWLINE, "\r\n");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "2");
    }
    
    @Test
    public void testCarriageReturnOnly() {
        QLexpressLexer lexer = createLexer("1\r2", true);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1");
        assertToken(tokens, 1, TokenType.NEWLINE, "\r");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "2");
    }
    
    // ==================== Interpolation Mode Tests ====================
    
    @Test
    public void testScriptInterpolationMode() {
        QLexpressLexer lexer = createLexer("\"hello ${world}\"", InterpolationMode.SCRIPT);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        // The lexer includes the interpolation in the string value
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOUBLE_QUOTE, "hello ${world}");
    }
    
    @Test
    public void testVariableInterpolationMode() {
        QLexpressLexer lexer = createLexer("\"hello ${world}\"", InterpolationMode.VARIABLE);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        // The lexer includes the interpolation in the string value
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOUBLE_QUOTE, "hello ${world}");
    }
    
    @Test
    public void testDisableInterpolationMode() {
        QLexpressLexer lexer = createLexer("\"hello\\nworld\"", InterpolationMode.DISABLE);
        List<Token> tokens = lexer.tokenize();
        // Remove EOF
        if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getType() == TokenType.EOF) {
            tokens.remove(tokens.size() - 1);
        }
        // In DISABLE mode, backslash escapes work
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.DOUBLE_QUOTE, "hello\nworld");
    }
    
    // ==================== Complex Expression Tests ====================
    
    @Test
    public void testArithmeticExpression() {
        List<Token> tokens = tokenize("1 + 2 * 3");
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, "1");
        assertToken(tokens, 1, TokenType.ADD, "+");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "2");
        assertToken(tokens, 3, TokenType.MUL, "*");
        assertToken(tokens, 4, TokenType.INTEGER_OR_FLOATING_LITERAL, "3");
    }
    
    @Test
    public void testAssignmentExpression() {
        List<Token> tokens = tokenize("x = 42");
        assertEquals(3, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "x");
        assertToken(tokens, 1, TokenType.EQ, "=");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "42");
    }
    
    @Test
    public void testMethodCall() {
        List<Token> tokens = tokenize("obj.method(arg1, arg2)");
        assertEquals(8, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "obj");
        assertToken(tokens, 1, TokenType.DOT, ".");
        assertToken(tokens, 2, TokenType.ID, "method");
        assertToken(tokens, 3, TokenType.LPAREN, "(");
        assertToken(tokens, 4, TokenType.ID, "arg1");
        assertToken(tokens, 5, TokenType.COMMA, ",");
        assertToken(tokens, 6, TokenType.ID, "arg2");
        assertToken(tokens, 7, TokenType.RPAREN, ")");
    }
    
    @Test
    public void testIfStatement() {
        List<Token> tokens = tokenize("if (x > 0) { return x; }");
        assertEquals(11, tokens.size());
        assertToken(tokens, 0, TokenType.IF, "if");
        assertToken(tokens, 1, TokenType.LPAREN, "(");
        assertToken(tokens, 2, TokenType.ID, "x");
        assertToken(tokens, 3, TokenType.GT, ">");
        assertToken(tokens, 4, TokenType.INTEGER_OR_FLOATING_LITERAL, "0");
        assertToken(tokens, 5, TokenType.RPAREN, ")");
        assertToken(tokens, 6, TokenType.LBRACE, "{");
        assertToken(tokens, 7, TokenType.RETURN, "return");
        assertToken(tokens, 8, TokenType.ID, "x");
        assertToken(tokens, 9, TokenType.SEMI, ";");
        assertToken(tokens, 10, TokenType.RBRACE, "}");
    }
    
    @Test
    public void testForLoop() {
        List<Token> tokens = tokenize("for (int i = 0; i < 10; i++) {}");
        assertEquals(16, tokens.size());
        assertToken(tokens, 0, TokenType.FOR, "for");
        assertToken(tokens, 1, TokenType.LPAREN, "(");
        assertToken(tokens, 2, TokenType.INT, "int");
        assertToken(tokens, 3, TokenType.ID, "i");
        assertToken(tokens, 4, TokenType.EQ, "=");
        assertToken(tokens, 5, TokenType.INTEGER_OR_FLOATING_LITERAL, "0");
        assertToken(tokens, 6, TokenType.SEMI, ";");
        assertToken(tokens, 7, TokenType.ID, "i");
        assertToken(tokens, 8, TokenType.LT, "<");
        assertToken(tokens, 9, TokenType.INTEGER_OR_FLOATING_LITERAL, "10");
        assertToken(tokens, 10, TokenType.SEMI, ";");
        assertToken(tokens, 11, TokenType.ID, "i");
        assertToken(tokens, 12, TokenType.INC, "++");
        assertToken(tokens, 13, TokenType.RPAREN, ")");
        assertToken(tokens, 14, TokenType.LBRACE, "{");
        assertToken(tokens, 15, TokenType.RBRACE, "}");
    }
    
    @Test
    public void testLambdaExpression() {
        List<Token> tokens = tokenize("(x, y) -> x + y");
        assertEquals(9, tokens.size());
        assertToken(tokens, 0, TokenType.LPAREN, "(");
        assertToken(tokens, 1, TokenType.ID, "x");
        assertToken(tokens, 2, TokenType.COMMA, ",");
        assertToken(tokens, 3, TokenType.ID, "y");
        assertToken(tokens, 4, TokenType.RPAREN, ")");
        assertToken(tokens, 5, TokenType.ARROW, "->");
        assertToken(tokens, 6, TokenType.ID, "x");
        assertToken(tokens, 7, TokenType.ADD, "+");
        assertToken(tokens, 8, TokenType.ID, "y");
    }
    
    @Test
    public void testTernaryExpression() {
        List<Token> tokens = tokenize("x ? y : z");
        assertEquals(5, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "x");
        assertToken(tokens, 1, TokenType.QUESTION, "?");
        assertToken(tokens, 2, TokenType.ID, "y");
        assertToken(tokens, 3, TokenType.COLON, ":");
        assertToken(tokens, 4, TokenType.ID, "z");
    }
    
    @Test
    public void testArrayAccess() {
        List<Token> tokens = tokenize("arr[0]");
        assertEquals(4, tokens.size());
        assertToken(tokens, 0, TokenType.ID, "arr");
        assertToken(tokens, 1, TokenType.LBRACK, "[");
        assertToken(tokens, 2, TokenType.INTEGER_OR_FLOATING_LITERAL, "0");
        assertToken(tokens, 3, TokenType.RBRACK, "]");
    }
    
    @Test
    public void testNewExpression() {
        List<Token> tokens = tokenize("new ArrayList()");
        assertEquals(4, tokens.size());
        assertToken(tokens, 0, TokenType.NEW, "new");
        assertToken(tokens, 1, TokenType.ID, "ArrayList");
        assertToken(tokens, 2, TokenType.LPAREN, "(");
        assertToken(tokens, 3, TokenType.RPAREN, ")");
    }
    
    // ==================== Edge Cases ====================
    
    @Test
    public void testEmptyInput() {
        List<Token> tokens = tokenize("");
        assertEquals(0, tokens.size());
    }
    
    @Test
    public void testWhitespaceOnly() {
        List<Token> tokens = tokenize("   \t  \n  ");
        assertEquals(0, tokens.size());
    }
    
    @Test
    public void testVeryLongIdentifier() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append('a');
        }
        String longId = sb.toString();
        List<Token> tokens = tokenize(longId);
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.ID, longId);
    }
    
    @Test
    public void testVeryLongNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append('1');
        }
        String longNum = sb.toString();
        List<Token> tokens = tokenize(longNum);
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.INTEGER_OR_FLOATING_LITERAL, longNum);
    }
    
    @Test
    public void testUnterminatedString() {
        List<Token> tokens = tokenize("'unterminated");
        assertEquals(1, tokens.size());
        assertToken(tokens, 0, TokenType.QUOTE_STRING_LITERAL, "unterminated");
    }
}
