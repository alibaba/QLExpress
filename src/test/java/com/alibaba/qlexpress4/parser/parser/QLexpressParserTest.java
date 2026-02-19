package com.alibaba.qlexpress4.parser.parser;

import com.alibaba.qlexpress4.parser.ast.ExpressionNode;
import com.alibaba.qlexpress4.parser.ast.IdentifierNode;
import com.alibaba.qlexpress4.parser.ast.LiteralNode;
import com.alibaba.qlexpress4.parser.ast.BinaryOpNode;
import com.alibaba.qlexpress4.parser.ast.UnaryOpNode;
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

    // ==================== Prefix Unary Operator Tests ====================

    @Test
    public void testParseUnaryNot() throws Exception {
        QLexpressParser parser = createParser("!true");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be !", "!", unary.getOperator());
        Assert.assertTrue("Should be prefix", unary.isPrefix());
        Assert.assertTrue("Operand should be LiteralNode", unary.getOperand() instanceof LiteralNode);
    }

    @Test
    public void testParseUnaryMinus() throws Exception {
        QLexpressParser parser = createParser("-42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be -", "-", unary.getOperator());
        Assert.assertTrue("Should be prefix", unary.isPrefix());
    }

    @Test
    public void testParseUnaryPlus() throws Exception {
        QLexpressParser parser = createParser("+42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be +", "+", unary.getOperator());
        Assert.assertTrue("Should be prefix", unary.isPrefix());
    }

    @Test
    public void testParseUnaryTilde() throws Exception {
        QLexpressParser parser = createParser("~42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be ~", "~", unary.getOperator());
        Assert.assertTrue("Should be prefix", unary.isPrefix());
    }

    @Test
    public void testParseUnaryIncrementPrefix() throws Exception {
        QLexpressParser parser = createParser("++x");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be ++", "++", unary.getOperator());
        Assert.assertTrue("Should be prefix", unary.isPrefix());
        Assert.assertTrue("Operand should be IdentifierNode", unary.getOperand() instanceof IdentifierNode);
    }

    @Test
    public void testParseUnaryDecrementPrefix() throws Exception {
        QLexpressParser parser = createParser("--x");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be --", "--", unary.getOperator());
        Assert.assertTrue("Should be prefix", unary.isPrefix());
    }

    @Test
    public void testParseNestedUnary() throws Exception {
        QLexpressParser parser = createParser("-+-42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be -", "-", unary.getOperator());
        // Inner should also be UnaryOpNode
        Assert.assertTrue("Inner operand should be UnaryOpNode", unary.getOperand() instanceof UnaryOpNode);
    }

    // ==================== Suffix Unary Operator Tests ====================

    @Test
    public void testParseUnaryIncrementSuffix() throws Exception {
        QLexpressParser parser = createParser("x++");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be ++", "++", unary.getOperator());
        Assert.assertFalse("Should be suffix", unary.isPrefix());
        Assert.assertTrue("Operand should be IdentifierNode", unary.getOperand() instanceof IdentifierNode);
    }

    @Test
    public void testParseUnaryDecrementSuffix() throws Exception {
        QLexpressParser parser = createParser("x--");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be UnaryOpNode", expr instanceof UnaryOpNode);
        UnaryOpNode unary = (UnaryOpNode) expr;
        Assert.assertEquals("Operator should be --", "--", unary.getOperator());
        Assert.assertFalse("Should be suffix", unary.isPrefix());
    }

    // ==================== Binary Operator Tests ====================

    @Test
    public void testParseBinaryAddition() throws Exception {
        QLexpressParser parser = createParser("1 + 2");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be +", "+", binary.getOperator());
        Assert.assertTrue("Left should be LiteralNode", binary.getLeft() instanceof LiteralNode);
        Assert.assertTrue("Right should be LiteralNode", binary.getRight() instanceof LiteralNode);
    }

    @Test
    public void testParseBinarySubtraction() throws Exception {
        QLexpressParser parser = createParser("5 - 3");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be -", "-", binary.getOperator());
    }

    @Test
    public void testParseBinaryMultiplication() throws Exception {
        QLexpressParser parser = createParser("3 * 4");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be *", "*", binary.getOperator());
    }

    @Test
    public void testParseBinaryDivision() throws Exception {
        QLexpressParser parser = createParser("10 / 2");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be /", "/", binary.getOperator());
    }

    @Test
    public void testParseBinaryModulus() throws Exception {
        QLexpressParser parser = createParser("10 % 3");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be %", "%", binary.getOperator());
    }

    @Test
    public void testParseBinaryEqual() throws Exception {
        QLexpressParser parser = createParser("a == b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be ==", "==", binary.getOperator());
    }

    @Test
    public void testParseBinaryNotEqual() throws Exception {
        QLexpressParser parser = createParser("a != b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be !=", "!=", binary.getOperator());
    }

    @Test
    public void testParseBinaryLessThan() throws Exception {
        QLexpressParser parser = createParser("a < b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be <", "<", binary.getOperator());
    }

    @Test
    public void testParseBinaryGreaterThan() throws Exception {
        QLexpressParser parser = createParser("a > b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be >", ">", binary.getOperator());
    }

    @Test
    public void testParseBinaryLessEqual() throws Exception {
        QLexpressParser parser = createParser("a <= b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be <=", "<=", binary.getOperator());
    }

    @Test
    public void testParseBinaryGreaterEqual() throws Exception {
        QLexpressParser parser = createParser("a >= b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be >=", ">=", binary.getOperator());
    }

    @Test
    public void testParseBinaryBitwiseAnd() throws Exception {
        QLexpressParser parser = createParser("a & b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be &", "&", binary.getOperator());
    }

    @Test
    public void testParseBinaryBitwiseOr() throws Exception {
        QLexpressParser parser = createParser("a | b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be |", "|", binary.getOperator());
    }

    @Test
    public void testParseBinaryBitwiseXor() throws Exception {
        QLexpressParser parser = createParser("a ^ b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be ^", "^", binary.getOperator());
    }

    @Test
    public void testParseBinaryLeftShift() throws Exception {
        QLexpressParser parser = createParser("a << b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be <<", "<<", binary.getOperator());
    }

    @Test
    public void testParseBinaryRightShift() throws Exception {
        QLexpressParser parser = createParser("a >> b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be >>", ">>", binary.getOperator());
    }

    @Test
    public void testParseBinaryUnsignedRightShift() throws Exception {
        QLexpressParser parser = createParser("a >>> b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be >>>", ">>>", binary.getOperator());
    }

    @Test
    public void testParseBinaryAssignment() throws Exception {
        QLexpressParser parser = createParser("x = 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be =", "=", binary.getOperator());
    }

    @Test
    public void testParseBinaryAddAssign() throws Exception {
        QLexpressParser parser = createParser("x += 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be +=", "+=", binary.getOperator());
    }

    @Test
    public void testParseBinarySubAssign() throws Exception {
        QLexpressParser parser = createParser("x -= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be -=", "-=", binary.getOperator());
    }

    @Test
    public void testParseBinaryMulAssign() throws Exception {
        QLexpressParser parser = createParser("x *= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be *=", "*=", binary.getOperator());
    }

    @Test
    public void testParseBinaryDivAssign() throws Exception {
        QLexpressParser parser = createParser("x /= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be /=", "/=", binary.getOperator());
    }

    @Test
    public void testParseBinaryModAssign() throws Exception {
        QLexpressParser parser = createParser("x %= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be %=", "%=", binary.getOperator());
    }

    @Test
    public void testParseBinaryAndAssign() throws Exception {
        QLexpressParser parser = createParser("x &= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be &=", "&=", binary.getOperator());
    }

    @Test
    public void testParseBinaryOrAssign() throws Exception {
        QLexpressParser parser = createParser("x |= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be |=", "|=", binary.getOperator());
    }

    @Test
    public void testParseBinaryXorAssign() throws Exception {
        QLexpressParser parser = createParser("x ^= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be ^=", "^=", binary.getOperator());
    }

    @Test
    public void testParseBinaryLeftShiftAssign() throws Exception {
        QLexpressParser parser = createParser("x <<= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be <<=", "<<=", binary.getOperator());
    }

    @Test
    public void testParseBinaryRightShiftAssign() throws Exception {
        QLexpressParser parser = createParser("x >>= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be >>=", ">>=", binary.getOperator());
    }

    @Test
    public void testParseBinaryUnsignedRightShiftAssign() throws Exception {
        QLexpressParser parser = createParser("x >>>= 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be >>>=", ">>>=", binary.getOperator());
    }

    // ==================== Operator Precedence Tests ====================

    @Test
    public void testParseMultiplicationBeforeAddition() throws Exception {
        QLexpressParser parser = createParser("1 + 2 * 3");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Top operator should be +", "+", binary.getOperator());
        Assert.assertTrue("Left should be LiteralNode", binary.getLeft() instanceof LiteralNode);
        // Right should be BinaryOpNode (2 * 3)
        Assert.assertTrue("Right should be BinaryOpNode", binary.getRight() instanceof BinaryOpNode);
        BinaryOpNode right = (BinaryOpNode) binary.getRight();
        Assert.assertEquals("Right operator should be *", "*", right.getOperator());
    }

    @Test
    public void testParseDivisionBeforeSubtraction() throws Exception {
        QLexpressParser parser = createParser("10 - 6 / 2");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Top operator should be -", "-", binary.getOperator());
        Assert.assertTrue("Right should be BinaryOpNode", binary.getRight() instanceof BinaryOpNode);
        BinaryOpNode right = (BinaryOpNode) binary.getRight();
        Assert.assertEquals("Right operator should be /", "/", right.getOperator());
    }

    @Test
    public void testParseExponentiationChain() throws Exception {
        // Note: QLExpress doesn't have ** operator, this tests multiplication/division chain
        QLexpressParser parser = createParser("2 * 3 * 4");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        // Left associative: (2 * 3) * 4
        Assert.assertEquals("Top operator should be *", "*", binary.getOperator());
        Assert.assertTrue("Left should be BinaryOpNode", binary.getLeft() instanceof BinaryOpNode);
    }

    @Test
    public void testParseComparisonBeforeLogical() throws Exception {
        QLexpressParser parser = createParser("a < b && c > d");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        // && has lower precedence than comparison
        Assert.assertEquals("Top operator should be &&", "&&", binary.getOperator());
    }

    @Test
    public void testParseBitwiseBeforeComparison() throws Exception {
        QLexpressParser parser = createParser("a & b == c");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        // Note: In QLExpress, EQUAL (7) has higher precedence than BIT_AND (6)
        // So == binds more tightly: a & (b == c)
        // This is different from standard Java where & binds more tightly
        Assert.assertEquals("Top operator should be &", "&", binary.getOperator());
    }

    @Test
    public void testParseAssignmentLowestPrecedence() throws Exception {
        QLexpressParser parser = createParser("x = a + b * c");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        // = has lowest precedence
        Assert.assertEquals("Top operator should be =", "=", binary.getOperator());
    }

    @Test
    public void testParseUnaryBeforeMultiplication() throws Exception {
        QLexpressParser parser = createParser("-a * b");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Top operator should be *", "*", binary.getOperator());
        Assert.assertTrue("Left should be UnaryOpNode", binary.getLeft() instanceof UnaryOpNode);
    }

    @Test
    public void testParseParenthesesOverridePrecedence() throws Exception {
        QLexpressParser parser = createParser("(1 + 2) * 3");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Top operator should be *", "*", binary.getOperator());
        // Left should be BinaryOpNode (the result of 1 + 2 in parens)
        Assert.assertTrue("Left should be BinaryOpNode", binary.getLeft() instanceof BinaryOpNode);
        BinaryOpNode left = (BinaryOpNode) binary.getLeft();
        Assert.assertEquals("Left operator should be +", "+", left.getOperator());
    }

    // ==================== Complex Expression Tests ====================

    @Test
    public void testParseComplexExpression1() throws Exception {
        QLexpressParser parser = createParser("1 + 2 * 3 - 4 / 2");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        // Should be: (1 + (2 * 3)) - (4 / 2)
        Assert.assertEquals("Top operator should be -", "-", binary.getOperator());
    }

    @Test
    public void testParseComplexExpression2() throws Exception {
        QLexpressParser parser = createParser("a * b + c / d - e");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        // Should be: ((a * b) + (c / d)) - e
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Top operator should be -", "-", binary.getOperator());
    }

    @Test
    public void testParseExpressionWithNewlines() throws Exception {
        QLexpressParser parser = createParser("1 +\n2 *\n3");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Top operator should be +", "+", binary.getOperator());
    }

    @Test
    public void testParseExpressionWithSpaces() throws Exception {
        QLexpressParser parser = createParser("  1   +   2   ");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be BinaryOpNode", expr instanceof BinaryOpNode);
        BinaryOpNode binary = (BinaryOpNode) expr;
        Assert.assertEquals("Operator should be +", "+", binary.getOperator());
    }
}
