package com.alibaba.qlexpress4.parser.parser;

import com.alibaba.qlexpress4.parser.ast.ExpressionNode;
import com.alibaba.qlexpress4.parser.ast.IdentifierNode;
import com.alibaba.qlexpress4.parser.ast.LiteralNode;
import com.alibaba.qlexpress4.parser.lexer.QLexpressLexer;
import com.alibaba.qlexpress4.parser.token.Token;
import com.alibaba.qlexpress4.parser.token.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for QLexpressParser primary expression parsing.
 * Tests parsing of literals, identifiers, and parenthesized expressions.
 */
public class QLexpressParserTest {

    /**
     * Helper method to create a parser from a source string.
     */
    private QLexpressParser createParser(String source) {
        QLexpressLexer lexer = new QLexpressLexer(source);
        List<Token> tokens = lexer.tokenize();
        return new QLexpressParser(tokens);
    }

    /**
     * Helper method to parse a primary expression from source.
     */
    private ExpressionNode parsePrimary(String source) throws QLexpressParser.ParseException {
        QLexpressParser parser = createParser(source);
        return parser.parsePrimary();
    }

    // ==================== Integer Literal Tests ====================

    @Test
    public void testParseDecimalInteger() throws Exception {
        ExpressionNode expr = parsePrimary("42");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 42", 42, literal.getValue());
    }

    @Test
    public void testParseNegativeDecimalInteger() throws Exception {
        // Note: Negative numbers are parsed with unary minus, so this will be handled later
        // For now, we just test that the lexer tokenizes it correctly
        ExpressionNode expr = parsePrimary("42");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 42", 42, literal.getValue());
    }

    @Test
    public void testParseHexInteger() throws Exception {
        ExpressionNode expr = parsePrimary("0xFF");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 255", 255, literal.getValue());
    }

    @Test
    public void testParseBinaryInteger() throws Exception {
        ExpressionNode expr = parsePrimary("0b1010");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 10", 10, literal.getValue());
    }

    @Test
    public void testParseOctalInteger() throws Exception {
        ExpressionNode expr = parsePrimary("0755");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        // 0755 octal = 493 decimal
        Assert.assertEquals("Value should be 493", 493, literal.getValue());
    }

    @Test
    public void testParseLongInteger() throws Exception {
        ExpressionNode expr = parsePrimary("42L");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 42L", 42L, literal.getValue());
        Assert.assertTrue("Value should be Long", literal.getValue() instanceof Long);
    }

    @Test
    public void testParseIntegerWithUnderscores() throws Exception {
        ExpressionNode expr = parsePrimary("1_000_000");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 1000000", 1000000, literal.getValue());
    }

    // ==================== Float Literal Tests ====================

    @Test
    public void testParseFloatLiteral() throws Exception {
        ExpressionNode expr = parsePrimary("3.14");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 3.14", 3.14, ((Number)literal.getValue()).doubleValue(), 0.001);
    }

    @Test
    public void testParseFloatWithFSuffix() throws Exception {
        ExpressionNode expr = parsePrimary("3.14f");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 3.14f", 3.14f, ((Number)literal.getValue()).floatValue(), 0.001);
        Assert.assertTrue("Value should be Float", literal.getValue() instanceof Float);
    }

    @Test
    public void testParseFloatWithDSuffix() throws Exception {
        ExpressionNode expr = parsePrimary("3.14d");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 3.14d", 3.14d, ((Number)literal.getValue()).doubleValue(), 0.001);
        Assert.assertTrue("Value should be Double", literal.getValue() instanceof Double);
    }

    @Test
    public void testParseScientificNotation() throws Exception {
        ExpressionNode expr = parsePrimary("1.5e10");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 1.5e10", 1.5e10, ((Number)literal.getValue()).doubleValue(), 1e6);
    }

    @Test
    public void testParseFloatWithUnderscores() throws Exception {
        ExpressionNode expr = parsePrimary("1_000.123_456");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 1000.123456", 1000.123456, ((Number)literal.getValue()).doubleValue(), 0.000001);
    }

    // ==================== String Literal Tests ====================

    @Test
    public void testParseSingleQuoteString() throws Exception {
        ExpressionNode expr = parsePrimary("'hello'");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 'hello'", "hello", literal.getValue());
        Assert.assertTrue("Value should be String", literal.getValue() instanceof String);
    }

    @Test
    public void testParseEmptyString() throws Exception {
        ExpressionNode expr = parsePrimary("''");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be empty string", "", literal.getValue());
    }

    @Test
    public void testParseStringWithEscapes() throws Exception {
        ExpressionNode expr = parsePrimary("'hello\\nworld'");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should contain newline", "hello\nworld", literal.getValue());
    }

    @Test
    public void testParseStringWithTabEscape() throws Exception {
        ExpressionNode expr = parsePrimary("'hello\\tworld'");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should contain tab", "hello\tworld", literal.getValue());
    }

    @Test
    public void testParseStringWithBackslashEscape() throws Exception {
        ExpressionNode expr = parsePrimary("'hello\\\\world'");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should contain backslash", "hello\\world", literal.getValue());
    }

    @Test
    public void testParseStringWithQuoteEscape() throws Exception {
        ExpressionNode expr = parsePrimary("'it\\'s great'");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should contain apostrophe", "it's great", literal.getValue());
    }

    // ==================== Boolean Literal Tests ====================

    @Test
    public void testParseTrueLiteral() throws Exception {
        ExpressionNode expr = parsePrimary("true");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be true", Boolean.TRUE, literal.getValue());
    }

    @Test
    public void testParseFalseLiteral() throws Exception {
        ExpressionNode expr = parsePrimary("false");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be false", Boolean.FALSE, literal.getValue());
    }

    // ==================== Null Literal Tests ====================

    @Test
    public void testParseNullLiteral() throws Exception {
        ExpressionNode expr = parsePrimary("null");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertNull("Value should be null", literal.getValue());
    }

    // ==================== Identifier Tests ====================

    @Test
    public void testParseSimpleIdentifier() throws Exception {
        ExpressionNode expr = parsePrimary("foo");
        Assert.assertTrue("Should be IdentifierNode", expr instanceof IdentifierNode);
        IdentifierNode identifier = (IdentifierNode) expr;
        Assert.assertEquals("Name should be 'foo'", "foo", identifier.getName());
    }

    @Test
    public void testParseIdentifierWithUnderscore() throws Exception {
        ExpressionNode expr = parsePrimary("_foo");
        Assert.assertTrue("Should be IdentifierNode", expr instanceof IdentifierNode);
        IdentifierNode identifier = (IdentifierNode) expr;
        Assert.assertEquals("Name should be '_foo'", "_foo", identifier.getName());
    }

    @Test
    public void testParseIdentifierWithDollarSign() throws Exception {
        ExpressionNode expr = parsePrimary("$foo");
        Assert.assertTrue("Should be IdentifierNode", expr instanceof IdentifierNode);
        IdentifierNode identifier = (IdentifierNode) expr;
        Assert.assertEquals("Name should be '$foo'", "$foo", identifier.getName());
    }

    @Test
    public void testParseCamelCaseIdentifier() throws Exception {
        ExpressionNode expr = parsePrimary("myVariable");
        Assert.assertTrue("Should be IdentifierNode", expr instanceof IdentifierNode);
        IdentifierNode identifier = (IdentifierNode) expr;
        Assert.assertEquals("Name should be 'myVariable'", "myVariable", identifier.getName());
    }

    // ==================== Parenthesized Expression Tests ====================

    @Test
    public void testParseParenthesizedInteger() throws Exception {
        ExpressionNode expr = parsePrimary("(42)");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 42", 42, literal.getValue());
    }

    @Test
    public void testParseParenthesizedString() throws Exception {
        ExpressionNode expr = parsePrimary("('hello')");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 'hello'", "hello", literal.getValue());
    }

    @Test
    public void testParseParenthesizedIdentifier() throws Exception {
        ExpressionNode expr = parsePrimary("(foo)");
        Assert.assertTrue("Should be IdentifierNode", expr instanceof IdentifierNode);
        IdentifierNode identifier = (IdentifierNode) expr;
        Assert.assertEquals("Name should be 'foo'", "foo", identifier.getName());
    }

    @Test
    public void testParseParenthesizedWithNewlines() throws Exception {
        ExpressionNode expr = parsePrimary("(\n42\n)");
        Assert.assertTrue("Should be LiteralNode", expr instanceof LiteralNode);
        LiteralNode literal = (LiteralNode) expr;
        Assert.assertEquals("Value should be 42", 42, literal.getValue());
    }

    // ==================== Error Handling Tests ====================

    @Test(expected = QLexpressParser.ParseException.class)
    public void testParseEmptyInput() throws Exception {
        QLexpressParser parser = createParser("");
        parser.parsePrimary();
    }

    @Test(expected = QLexpressParser.ParseException.class)
    public void testParseUnclosedParenthesis() throws Exception {
        QLexpressParser parser = createParser("(42");
        parser.parsePrimary();
    }

    @Test(expected = QLexpressParser.ParseException.class)
    public void testParseUnexpectedOperator() throws Exception {
        QLexpressParser parser = createParser("+");
        parser.parsePrimary();
    }
}
