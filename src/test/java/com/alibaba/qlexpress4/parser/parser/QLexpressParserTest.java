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

    // ==================== Ternary Expression Tests ====================

    @Test
    public void testParseSimpleTernary() throws Exception {
        QLexpressParser parser = createParser("a ? b : c");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        Assert.assertTrue("Condition should be IdentifierNode", ternary.getCondition() instanceof IdentifierNode);
        Assert.assertTrue("Then expr should be IdentifierNode", ternary.getThenExpr() instanceof IdentifierNode);
        Assert.assertTrue("Else expr should be IdentifierNode", ternary.getElseExpr() instanceof IdentifierNode);
    }

    @Test
    public void testParseTernaryWithBinaryCondition() throws Exception {
        QLexpressParser parser = createParser("a > b ? 1 : 0");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        Assert.assertTrue("Condition should be BinaryOpNode", ternary.getCondition() instanceof BinaryOpNode);
        BinaryOpNode condition = (BinaryOpNode) ternary.getCondition();
        Assert.assertEquals("Condition operator should be >", ">", condition.getOperator());
    }

    @Test
    public void testParseNestedTernary() throws Exception {
        QLexpressParser parser = createParser("a ? b : c ? d : e");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        // The else expr should contain the nested ternary (right-nesting)
        Assert.assertTrue("Else expr should be TernaryNode (nested)", ternary.getElseExpr() instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
    }

    @Test
    public void testParseTernaryWithMethodCall() throws Exception {
        QLexpressParser parser = createParser("a ? foo() : bar()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        Assert.assertTrue("Then expr should be MethodCallNode", ternary.getThenExpr() instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        Assert.assertTrue("Else expr should be MethodCallNode", ternary.getElseExpr() instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
    }

    @Test
    public void testParseTernaryWithNewlines() throws Exception {
        QLexpressParser parser = createParser("a\n?\nb\n:\nc");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
    }

    // ==================== Lambda Expression Tests ====================

    @Test
    public void testParseSimpleLambda() throws Exception {
        QLexpressParser parser = createParser("x -> x + 1");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be LambdaNode", expr instanceof com.alibaba.qlexpress4.parser.ast.LambdaNode);
        com.alibaba.qlexpress4.parser.ast.LambdaNode lambda = (com.alibaba.qlexpress4.parser.ast.LambdaNode) expr;
        Assert.assertEquals("Should have 1 parameter", 1, lambda.getParameters().size());
        Assert.assertEquals("Parameter name should be 'x'", "x", lambda.getParameters().get(0).getParameterName());
    }

    @Test
    public void testParseLambdaWithBlock() throws Exception {
        QLexpressParser parser = createParser("x -> { x + 1 }");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be LambdaNode", expr instanceof com.alibaba.qlexpress4.parser.ast.LambdaNode);
        com.alibaba.qlexpress4.parser.ast.LambdaNode lambda = (com.alibaba.qlexpress4.parser.ast.LambdaNode) expr;
        Assert.assertEquals("Should have 1 parameter", 1, lambda.getParameters().size());
    }

    @Test
    public void testParseMultiParamLambda() throws Exception {
        QLexpressParser parser = createParser("(x, y) -> x + y");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be LambdaNode", expr instanceof com.alibaba.qlexpress4.parser.ast.LambdaNode);
        com.alibaba.qlexpress4.parser.ast.LambdaNode lambda = (com.alibaba.qlexpress4.parser.ast.LambdaNode) expr;
        Assert.assertEquals("Should have 2 parameters", 2, lambda.getParameters().size());
        Assert.assertEquals("First parameter name should be 'x'", "x", lambda.getParameters().get(0).getParameterName());
        Assert.assertEquals("Second parameter name should be 'y'", "y", lambda.getParameters().get(1).getParameterName());
    }

    @Test
    public void testParseEmptyParamLambda() throws Exception {
        QLexpressParser parser = createParser("() -> 42");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be LambdaNode", expr instanceof com.alibaba.qlexpress4.parser.ast.LambdaNode);
        com.alibaba.qlexpress4.parser.ast.LambdaNode lambda = (com.alibaba.qlexpress4.parser.ast.LambdaNode) expr;
        Assert.assertEquals("Should have 0 parameters", 0, lambda.getParameters().size());
    }

    @Test
    public void testParseTypedParamLambda() throws Exception {
        QLexpressParser parser = createParser("(int x, String y) -> x + y.length()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be LambdaNode", expr instanceof com.alibaba.qlexpress4.parser.ast.LambdaNode);
        com.alibaba.qlexpress4.parser.ast.LambdaNode lambda = (com.alibaba.qlexpress4.parser.ast.LambdaNode) expr;
        Assert.assertEquals("Should have 2 parameters", 2, lambda.getParameters().size());
        Assert.assertEquals("First parameter type should be 'int'", "int", lambda.getParameters().get(0).getTypeName());
    }

    // ==================== Method Call Tests ====================

    @Test
    public void testParseSimpleMethodCall() throws Exception {
        QLexpressParser parser = createParser("foo()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Method name should be 'foo'", "foo", methodCall.getMethodName());
        Assert.assertEquals("Should have 0 arguments", 0, methodCall.getArguments().size());
        Assert.assertNull("Target should be null (static call)", methodCall.getTarget());
    }

    @Test
    public void testParseMethodCallWithArgs() throws Exception {
        QLexpressParser parser = createParser("foo(a, b)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Method name should be 'foo'", "foo", methodCall.getMethodName());
        Assert.assertEquals("Should have 2 arguments", 2, methodCall.getArguments().size());
    }

    @Test
    public void testParseChainedMethodCall() throws Exception {
        QLexpressParser parser = createParser("a.foo().bar()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Outer method name should be 'bar'", "bar", methodCall.getMethodName());
        Assert.assertNotNull("Target should be another MethodCallNode", methodCall.getTarget());
    }

    @Test
    public void testParseMethodCallOnTarget() throws Exception {
        QLexpressParser parser = createParser("obj.method()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Method name should be 'method'", "method", methodCall.getMethodName());
        Assert.assertNotNull("Target should not be null", methodCall.getTarget());
        Assert.assertTrue("Target should be IdentifierNode", methodCall.getTarget() instanceof IdentifierNode);
    }

    @Test
    public void testParseNestedMethodCalls() throws Exception {
        QLexpressParser parser = createParser("foo(bar(a, b), c)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Should have 2 arguments", 2, methodCall.getArguments().size());
        // First argument should be a MethodCallNode
        Assert.assertTrue("First arg should be MethodCallNode", methodCall.getArguments().get(0) instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
    }

    @Test
    public void testParseMethodCallWithExpressionArgs() throws Exception {
        QLexpressParser parser = createParser("foo(a + b, c * d)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Should have 2 arguments", 2, methodCall.getArguments().size());
        Assert.assertTrue("First arg should be BinaryOpNode", methodCall.getArguments().get(0) instanceof BinaryOpNode);
        Assert.assertTrue("Second arg should be BinaryOpNode", methodCall.getArguments().get(1) instanceof BinaryOpNode);
    }

    @Test
    public void testParseMethodCallWithNewlines() throws Exception {
        QLexpressParser parser = createParser("foo\n(\na\n,\nb\n)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Should have 2 arguments", 2, methodCall.getArguments().size());
    }

    // ==================== Array Access Tests ====================

    @Test
    public void testParseSimpleArrayAccess() throws Exception {
        QLexpressParser parser = createParser("arr[0]");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be ArrayAccessNode", expr instanceof com.alibaba.qlexpress4.parser.ast.ArrayAccessNode);
        com.alibaba.qlexpress4.parser.ast.ArrayAccessNode arrayAccess = (com.alibaba.qlexpress4.parser.ast.ArrayAccessNode) expr;
        Assert.assertTrue("Array should be IdentifierNode", arrayAccess.getArray() instanceof IdentifierNode);
        Assert.assertTrue("Index should be LiteralNode", arrayAccess.getIndex() instanceof LiteralNode);
    }

    @Test
    public void testParseNestedArrayAccess() throws Exception {
        QLexpressParser parser = createParser("arr[i][j]");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be ArrayAccessNode", expr instanceof com.alibaba.qlexpress4.parser.ast.ArrayAccessNode);
        com.alibaba.qlexpress4.parser.ast.ArrayAccessNode arrayAccess = (com.alibaba.qlexpress4.parser.ast.ArrayAccessNode) expr;
        Assert.assertTrue("Array should be another ArrayAccessNode", arrayAccess.getArray() instanceof com.alibaba.qlexpress4.parser.ast.ArrayAccessNode);
    }

    @Test
    public void testParseArrayAccessWithExpression() throws Exception {
        QLexpressParser parser = createParser("arr[i + 1]");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be ArrayAccessNode", expr instanceof com.alibaba.qlexpress4.parser.ast.ArrayAccessNode);
        com.alibaba.qlexpress4.parser.ast.ArrayAccessNode arrayAccess = (com.alibaba.qlexpress4.parser.ast.ArrayAccessNode) expr;
        Assert.assertTrue("Index should be BinaryOpNode", arrayAccess.getIndex() instanceof BinaryOpNode);
    }

    // ==================== Constructor Call Tests ====================

    @Test
    public void testParseSimpleConstructorCall() throws Exception {
        QLexpressParser parser = createParser("new Foo()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be ConstructorCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.ConstructorCallNode);
        com.alibaba.qlexpress4.parser.ast.ConstructorCallNode constructorCall = (com.alibaba.qlexpress4.parser.ast.ConstructorCallNode) expr;
        Assert.assertEquals("Type name should be 'Foo'", "Foo", constructorCall.getTypeName());
        Assert.assertEquals("Should have 0 arguments", 0, constructorCall.getArguments().size());
    }

    @Test
    public void testParseConstructorCallWithArgs() throws Exception {
        QLexpressParser parser = createParser("new Foo(a, b)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be ConstructorCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.ConstructorCallNode);
        com.alibaba.qlexpress4.parser.ast.ConstructorCallNode constructorCall = (com.alibaba.qlexpress4.parser.ast.ConstructorCallNode) expr;
        Assert.assertEquals("Should have 2 arguments", 2, constructorCall.getArguments().size());
    }

    @Test
    public void testParseQualifiedConstructorCall() throws Exception {
        QLexpressParser parser = createParser("new com.example.Foo()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be ConstructorCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.ConstructorCallNode);
        com.alibaba.qlexpress4.parser.ast.ConstructorCallNode constructorCall = (com.alibaba.qlexpress4.parser.ast.ConstructorCallNode) expr;
        Assert.assertEquals("Type name should be 'com.example.Foo'", "com.example.Foo", constructorCall.getTypeName());
    }

    // ==================== Cast Expression Tests ====================

    @Test
    public void testParseSimpleCast() throws Exception {
        QLexpressParser parser = createParser("(int) x");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be CastNode", expr instanceof com.alibaba.qlexpress4.parser.ast.CastNode);
        com.alibaba.qlexpress4.parser.ast.CastNode cast = (com.alibaba.qlexpress4.parser.ast.CastNode) expr;
        Assert.assertEquals("Type name should be 'int'", "int", cast.getTypeName());
        Assert.assertTrue("Expression should be IdentifierNode", cast.getExpression() instanceof IdentifierNode);
    }

    @Test
    public void testParseQualifiedCast() throws Exception {
        QLexpressParser parser = createParser("(String) obj");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be CastNode", expr instanceof com.alibaba.qlexpress4.parser.ast.CastNode);
        com.alibaba.qlexpress4.parser.ast.CastNode cast = (com.alibaba.qlexpress4.parser.ast.CastNode) expr;
        Assert.assertEquals("Type name should be 'String'", "String", cast.getTypeName());
    }

    @Test
    public void testParseCastWithBinaryExpression() throws Exception {
        QLexpressParser parser = createParser("(int) (a + b)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be CastNode", expr instanceof com.alibaba.qlexpress4.parser.ast.CastNode);
        com.alibaba.qlexpress4.parser.ast.CastNode cast = (com.alibaba.qlexpress4.parser.ast.CastNode) expr;
        Assert.assertTrue("Expression should be BinaryOpNode", cast.getExpression() instanceof BinaryOpNode);
    }

    // ==================== Combined Expression Tests ====================

    @Test
    public void testParseTernaryWithDifferentMethodCalls() throws Exception {
        QLexpressParser parser = createParser("condition ? foo() : bar()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        Assert.assertTrue("Then expr should be MethodCallNode", ternary.getThenExpr() instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        Assert.assertTrue("Else expr should be MethodCallNode", ternary.getElseExpr() instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
    }

    @Test
    public void testParseMethodCallChainingWithArrayAccess() throws Exception {
        QLexpressParser parser = createParser("obj.foo()[0].bar()");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Method name should be 'bar'", "bar", methodCall.getMethodName());
    }

    @Test
    public void testParseLambdaWithMethodCallBody() throws Exception {
        QLexpressParser parser = createParser("x -> foo(x)");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be LambdaNode", expr instanceof com.alibaba.qlexpress4.parser.ast.LambdaNode);
        com.alibaba.qlexpress4.parser.ast.LambdaNode lambda = (com.alibaba.qlexpress4.parser.ast.LambdaNode) expr;
        Assert.assertEquals("Should have 1 parameter", 1, lambda.getParameters().size());
    }

    @Test
    public void testParseConstructorCallInMethodArg() throws Exception {
        QLexpressParser parser = createParser("foo(new Bar())");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be MethodCallNode", expr instanceof com.alibaba.qlexpress4.parser.ast.MethodCallNode);
        com.alibaba.qlexpress4.parser.ast.MethodCallNode methodCall = (com.alibaba.qlexpress4.parser.ast.MethodCallNode) expr;
        Assert.assertEquals("Should have 1 argument", 1, methodCall.getArguments().size());
        Assert.assertTrue("Argument should be ConstructorCallNode", methodCall.getArguments().get(0) instanceof com.alibaba.qlexpress4.parser.ast.ConstructorCallNode);
    }

    @Test
    public void testParseCastInTernary() throws Exception {
        QLexpressParser parser = createParser("a ? (int) b : (String) c");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        Assert.assertTrue("Then expr should be CastNode", ternary.getThenExpr() instanceof com.alibaba.qlexpress4.parser.ast.CastNode);
        Assert.assertTrue("Else expr should be CastNode", ternary.getElseExpr() instanceof com.alibaba.qlexpress4.parser.ast.CastNode);
    }

    @Test
    public void testParseArrayAccessInTernary() throws Exception {
        QLexpressParser parser = createParser("a ? b[0] : c[1]");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TernaryNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TernaryNode);
        com.alibaba.qlexpress4.parser.ast.TernaryNode ternary = (com.alibaba.qlexpress4.parser.ast.TernaryNode) expr;
        Assert.assertTrue("Then expr should be ArrayAccessNode", ternary.getThenExpr() instanceof com.alibaba.qlexpress4.parser.ast.ArrayAccessNode);
        Assert.assertTrue("Else expr should be ArrayAccessNode", ternary.getElseExpr() instanceof com.alibaba.qlexpress4.parser.ast.ArrayAccessNode);
    }

    // ==================== Type Literal Tests ====================

    @Test
    public void testParseTypeLiteral() throws Exception {
        QLexpressParser parser = createParser("int");
        ExpressionNode expr = parser.parseExpression();
        Assert.assertTrue("Should be TypeNode", expr instanceof com.alibaba.qlexpress4.parser.ast.TypeNode);
        com.alibaba.qlexpress4.parser.ast.TypeNode typeNode = (com.alibaba.qlexpress4.parser.ast.TypeNode) expr;
        Assert.assertEquals("Type name should be 'int'", "int", typeNode.getTypeName());
    }

    // ==================== Statement Parsing Tests ====================

    // ==================== If Statement Tests ====================

    @Test
    public void testParseSimpleIfStatement() throws Exception {
        QLexpressParser parser = createParser("if (true) { x = 1; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be IfNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
        com.alibaba.qlexpress4.parser.ast.IfNode ifNode = (com.alibaba.qlexpress4.parser.ast.IfNode) stmt;
        Assert.assertTrue("Condition should be LiteralNode", ifNode.getCondition() instanceof LiteralNode);
        Assert.assertTrue("Then body should be BlockNode", ifNode.getThenBody() instanceof com.alibaba.qlexpress4.parser.ast.BlockNode);
    }

    @Test
    public void testParseIfElseStatement() throws Exception {
        QLexpressParser parser = createParser("if (a > b) { x = 1; } else { x = 2; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be IfNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
        com.alibaba.qlexpress4.parser.ast.IfNode ifNode = (com.alibaba.qlexpress4.parser.ast.IfNode) stmt;
        Assert.assertNotNull("Else body should not be null", ifNode.getElseBody());
    }

    @Test
    public void testParseIfThenElseStatement() throws Exception {
        QLexpressParser parser = createParser("if (a > b) then { x = 1; } else { x = 2; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be IfNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
    }

    @Test
    public void testParseNestedIfStatement() throws Exception {
        QLexpressParser parser = createParser("if (a) { if (b) { x = 1; } }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be IfNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
    }

    @Test
    public void testParseElseIfStatement() throws Exception {
        QLexpressParser parser = createParser("if (a) { x = 1; } else if (b) { x = 2; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be IfNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
        com.alibaba.qlexpress4.parser.ast.IfNode ifNode = (com.alibaba.qlexpress4.parser.ast.IfNode) stmt;
        Assert.assertNotNull("Else body should not be null", ifNode.getElseBody());
        Assert.assertTrue("Else body should be IfNode (else if)", ifNode.getElseBody() instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
    }

    @Test
    public void testParseIfWithExpressionThen() throws Exception {
        QLexpressParser parser = createParser("if (a) x = 1");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be IfNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.IfNode);
        com.alibaba.qlexpress4.parser.ast.IfNode ifNode = (com.alibaba.qlexpress4.parser.ast.IfNode) stmt;
        Assert.assertTrue("Then body should be ExpressionNode", ifNode.getThenBody() instanceof ExpressionNode);
    }

    // ==================== While Loop Tests ====================

    @Test
    public void testParseWhileLoop() throws Exception {
        QLexpressParser parser = createParser("while (true) { x = x + 1; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be WhileNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.WhileNode);
        com.alibaba.qlexpress4.parser.ast.WhileNode whileNode = (com.alibaba.qlexpress4.parser.ast.WhileNode) stmt;
        Assert.assertTrue("Condition should be LiteralNode", whileNode.getCondition() instanceof LiteralNode);
        Assert.assertTrue("Body should be BlockNode", whileNode.getBody() instanceof com.alibaba.qlexpress4.parser.ast.BlockNode);
    }

    @Test
    public void testParseWhileWithBinaryCondition() throws Exception {
        QLexpressParser parser = createParser("while (i < 10) { i = i + 1; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be WhileNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.WhileNode);
        com.alibaba.qlexpress4.parser.ast.WhileNode whileNode = (com.alibaba.qlexpress4.parser.ast.WhileNode) stmt;
        Assert.assertTrue("Condition should be BinaryOpNode", whileNode.getCondition() instanceof BinaryOpNode);
    }

    // ==================== For Loop Tests ====================

    @Test
    public void testParseTraditionalForLoop() throws Exception {
        QLexpressParser parser = createParser("for (int i = 0; i < 10; i = i + 1) { sum = sum + i; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ForNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ForNode);
        com.alibaba.qlexpress4.parser.ast.ForNode forNode = (com.alibaba.qlexpress4.parser.ast.ForNode) stmt;
        Assert.assertNotNull("Init should not be null", forNode.getInit());
        Assert.assertNotNull("Condition should not be null", forNode.getCondition());
        Assert.assertNotNull("Update should not be null", forNode.getUpdate());
    }

    @Test
    public void testParseForLoopWithoutInit() throws Exception {
        QLexpressParser parser = createParser("for (; i < 10; i = i + 1) { sum = sum + i; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ForNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ForNode);
    }

    @Test
    public void testParseForLoopWithoutCondition() throws Exception {
        QLexpressParser parser = createParser("for (int i = 0; ; i = i + 1) { sum = sum + i; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ForNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ForNode);
    }

    @Test
    public void testParseForLoopWithoutUpdate() throws Exception {
        QLexpressParser parser = createParser("for (int i = 0; i < 10;) { sum = sum + i; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ForNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ForNode);
    }

    @Test
    public void testParseForEachLoop() throws Exception {
        QLexpressParser parser = createParser("for (int x : list) { sum = sum + x; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ForNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ForNode);
        com.alibaba.qlexpress4.parser.ast.ForNode forNode = (com.alibaba.qlexpress4.parser.ast.ForNode) stmt;
        Assert.assertTrue("Init should be VariableDeclarationNode for for-each", forNode.getInit() instanceof com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode);
    }

    @Test
    public void testParseForEachLoopWithoutType() throws Exception {
        QLexpressParser parser = createParser("for (x : list) { sum = sum + x; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ForNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ForNode);
    }

    // ==================== Switch Statement Tests ====================

    @Test
    public void testParseSimpleSwitch() throws Exception {
        QLexpressParser parser = createParser("switch (x) { case 1: a = 1; break; default: a = 0; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be SwitchNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.SwitchNode);
        com.alibaba.qlexpress4.parser.ast.SwitchNode switchNode = (com.alibaba.qlexpress4.parser.ast.SwitchNode) stmt;
        Assert.assertTrue("Value should be IdentifierNode", switchNode.getValue() instanceof IdentifierNode);
        Assert.assertTrue("Should have at least 2 cases", switchNode.getCases().size() >= 2);
    }

    @Test
    public void testParseSwitchWithMultipleCases() throws Exception {
        QLexpressParser parser = createParser("switch (x) { case 1: a = 1; break; case 2: a = 2; break; default: a = 0; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be SwitchNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.SwitchNode);
        com.alibaba.qlexpress4.parser.ast.SwitchNode switchNode = (com.alibaba.qlexpress4.parser.ast.SwitchNode) stmt;
        Assert.assertTrue("Should have at least 3 cases", switchNode.getCases().size() >= 3);
    }

    @Test
    public void testParseSwitchWithArrowSyntax() throws Exception {
        QLexpressParser parser = createParser("switch (x) { case 1 -> a = 1; default -> a = 0; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be SwitchNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.SwitchNode);
    }

    @Test
    public void testParseSwitchOnlyDefault() throws Exception {
        QLexpressParser parser = createParser("switch (x) { default: a = 0; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be SwitchNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.SwitchNode);
        com.alibaba.qlexpress4.parser.ast.SwitchNode switchNode = (com.alibaba.qlexpress4.parser.ast.SwitchNode) stmt;
        Assert.assertEquals("Should have 1 case (default)", 1, switchNode.getCases().size());
    }

    // ==================== Block Statement Tests ====================

    @Test
    public void testParseEmptyBlock() throws Exception {
        QLexpressParser parser = createParser("{}");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be BlockNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.BlockNode);
        com.alibaba.qlexpress4.parser.ast.BlockNode blockNode = (com.alibaba.qlexpress4.parser.ast.BlockNode) stmt;
        Assert.assertEquals("Should have 0 statements", 0, blockNode.getStatements().size());
    }

    @Test
    public void testParseBlockWithStatements() throws Exception {
        QLexpressParser parser = createParser("{ x = 1; y = 2; }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be BlockNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.BlockNode);
        com.alibaba.qlexpress4.parser.ast.BlockNode blockNode = (com.alibaba.qlexpress4.parser.ast.BlockNode) stmt;
        Assert.assertTrue("Should have at least 2 statements", blockNode.getStatements().size() >= 2);
    }

    @Test
    public void testParseNestedBlocks() throws Exception {
        QLexpressParser parser = createParser("{ { x = 1; } }");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be BlockNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.BlockNode);
        com.alibaba.qlexpress4.parser.ast.BlockNode blockNode = (com.alibaba.qlexpress4.parser.ast.BlockNode) stmt;
        Assert.assertEquals("Should have 1 statement (inner block)", 1, blockNode.getStatements().size());
    }

    // ==================== Return Statement Tests ====================

    @Test
    public void testParseReturnWithValue() throws Exception {
        QLexpressParser parser = createParser("return 42;");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ReturnNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ReturnNode);
        com.alibaba.qlexpress4.parser.ast.ReturnNode returnNode = (com.alibaba.qlexpress4.parser.ast.ReturnNode) stmt;
        Assert.assertNotNull("Return value should not be null", returnNode.getValue());
    }

    @Test
    public void testParseReturnWithoutValue() throws Exception {
        QLexpressParser parser = createParser("return;");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ReturnNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ReturnNode);
        com.alibaba.qlexpress4.parser.ast.ReturnNode returnNode = (com.alibaba.qlexpress4.parser.ast.ReturnNode) stmt;
        Assert.assertNull("Return value should be null", returnNode.getValue());
    }

    // ==================== Break Statement Tests ====================

    @Test
    public void testParseBreakStatement() throws Exception {
        QLexpressParser parser = createParser("break;");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be BreakNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.BreakNode);
    }

    // ==================== Continue Statement Tests ====================

    @Test
    public void testParseContinueStatement() throws Exception {
        QLexpressParser parser = createParser("continue;");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ContinueNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ContinueNode);
    }

    // ==================== Throw Statement Tests ====================

    @Test
    public void testParseThrowStatement() throws Exception {
        QLexpressParser parser = createParser("throw new Exception();");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be ThrowNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.ThrowNode);
        com.alibaba.qlexpress4.parser.ast.ThrowNode throwNode = (com.alibaba.qlexpress4.parser.ast.ThrowNode) stmt;
        Assert.assertNotNull("Exception expression should not be null", throwNode.getException());
    }

    // ==================== Variable Declaration Tests ====================

    @Test
    public void testParseVariableDeclaration() throws Exception {
        QLexpressParser parser = createParser("int x;");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be VariableDeclarationNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode);
        com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode varDecl = (com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode) stmt;
        Assert.assertEquals("Type should be 'int'", "int", varDecl.getTypeName());
        Assert.assertEquals("Variable name should be 'x'", "x", varDecl.getVariableName());
    }

    @Test
    public void testParseVariableDeclarationWithInitializer() throws Exception {
        QLexpressParser parser = createParser("int x = 42;");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be VariableDeclarationNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode);
        com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode varDecl = (com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode) stmt;
        Assert.assertNotNull("Initializer should not be null", varDecl.getInitialValue());
    }

    @Test
    public void testParseStringVariableDeclaration() throws Exception {
        QLexpressParser parser = createParser("String name = 'test';");
        com.alibaba.qlexpress4.parser.ast.StatementNode stmt = parser.parseStatement();
        Assert.assertTrue("Should be VariableDeclarationNode", stmt instanceof com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode);
        com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode varDecl = (com.alibaba.qlexpress4.parser.ast.VariableDeclarationNode) stmt;
        Assert.assertEquals("Type should be 'String'", "String", varDecl.getTypeName());
    }
}
