package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.parser.parser.QLexpressParser;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Unit tests for InstructionGenerator.
 * Tests instruction generation for expression AST nodes.
 */
public class InstructionGeneratorTest {

    private final InstructionGenerator generator = new InstructionGenerator(new OperatorManager());
    private final GenerationContext context = new GenerationContext();

    @Test
    public void testVisitLiteralNode_Integer() throws Exception {
        LiteralNode node = new LiteralNode(1, 1, null, 42);
        GenerationResult result = generator.visit(node, context);

        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        QLInstruction instruction = result.getInstructions().get(0);
        assertTrue(instruction instanceof ConstInstruction);
        assertEquals(42, ((ConstInstruction) instruction).getConstObj());
    }

    @Test
    public void testVisitLiteralNode_String() throws Exception {
        LiteralNode node = new LiteralNode(1, 1, null, "hello");
        GenerationResult result = generator.visit(node, context);

        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertEquals("hello", ((ConstInstruction) result.getInstructions().get(0)).getConstObj());
    }

    @Test
    public void testVisitLiteralNode_Null() throws Exception {
        LiteralNode node = new LiteralNode(1, 1, null, null);
        GenerationResult result = generator.visit(node, context);

        assertEquals(1, result.getInstructions().size());
        assertNull(((ConstInstruction) result.getInstructions().get(0)).getConstObj());
    }

    @Test
    public void testVisitLiteralNode_Boolean() throws Exception {
        LiteralNode trueNode = new LiteralNode(1, 1, null, true);
        GenerationResult trueResult = generator.visit(trueNode, context);
        assertEquals(true, ((ConstInstruction) trueResult.getInstructions().get(0)).getConstObj());

        LiteralNode falseNode = new LiteralNode(1, 1, null, false);
        GenerationResult falseResult = generator.visit(falseNode, context);
        assertEquals(false, ((ConstInstruction) falseResult.getInstructions().get(0)).getConstObj());
    }

    @Test
    public void testVisitIdentifierNode() throws Exception {
        IdentifierNode node = new IdentifierNode(1, 1, null, "myVar");
        GenerationResult result = generator.visit(node, context);

        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        QLInstruction instruction = result.getInstructions().get(0);
        assertTrue(instruction instanceof LoadInstruction);
        assertEquals("myVar", ((LoadInstruction) instruction).getName());
    }

    @Test
    public void testVisitBinaryOpNode_Addition() throws Exception {
        // 1 + 2
        LiteralNode left = new LiteralNode(1, 1, null, 1);
        LiteralNode right = new LiteralNode(1, 1, null, 2);
        BinaryOpNode node = new BinaryOpNode(1, 1, null, left, "+", right);

        GenerationResult result = generator.visit(node, context);

        // Should have: const 1, const 2, operator +
        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof OperatorInstruction);
    }

    @Test
    public void testVisitBinaryOpNode_Multiplication() throws Exception {
        // 3 * 4
        LiteralNode left = new LiteralNode(1, 1, null, 3);
        LiteralNode right = new LiteralNode(1, 1, null, 4);
        BinaryOpNode node = new BinaryOpNode(1, 1, null, left, "*", right);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertTrue(result.getInstructions().get(2) instanceof OperatorInstruction);

        OperatorInstruction opInstr = (OperatorInstruction) result.getInstructions().get(2);
        assertEquals("*", opInstr.getOperator().getOperator());
    }

    @Test
    public void testVisitBinaryOpNode_Comparison() throws Exception {
        // a > b
        IdentifierNode left = new IdentifierNode(1, 1, null, "a");
        IdentifierNode right = new IdentifierNode(1, 1, null, "b");
        BinaryOpNode node = new BinaryOpNode(1, 1, null, left, ">", right);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(2) instanceof OperatorInstruction);
    }

    @Test
    public void testVisitBinaryOpNode_LogicalAnd() throws Exception {
        // true && false
        LiteralNode left = new LiteralNode(1, 1, null, true);
        LiteralNode right = new LiteralNode(1, 1, null, false);
        BinaryOpNode node = new BinaryOpNode(1, 1, null, left, "&&", right);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertTrue(result.getInstructions().get(2) instanceof OperatorInstruction);

        OperatorInstruction opInstr = (OperatorInstruction) result.getInstructions().get(2);
        assertEquals("&&", opInstr.getOperator().getOperator());
    }

    @Test
    public void testVisitUnaryOpNode_PrefixMinus() throws Exception {
        // -5
        LiteralNode operand = new LiteralNode(1, 1, null, 5);
        UnaryOpNode node = new UnaryOpNode(1, 1, null, "-", operand, true);

        GenerationResult result = generator.visit(node, context);

        assertEquals(2, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof UnaryInstruction);
    }

    @Test
    public void testVisitUnaryOpNode_PrefixNot() throws Exception {
        // !true
        LiteralNode operand = new LiteralNode(1, 1, null, true);
        UnaryOpNode node = new UnaryOpNode(1, 1, null, "!", operand, true);

        GenerationResult result = generator.visit(node, context);

        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(1) instanceof UnaryInstruction);
    }

    @Test
    public void testVisitUnaryOpNode_PrefixIncrement() throws Exception {
        // ++x
        IdentifierNode operand = new IdentifierNode(1, 1, null, "x");
        UnaryOpNode node = new UnaryOpNode(1, 1, null, "++", operand, true);

        GenerationResult result = generator.visit(node, context);

        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof UnaryInstruction);
    }

    @Test
    public void testVisitTernaryNode() throws Exception {
        // true ? 1 : 2
        LiteralNode condition = new LiteralNode(1, 1, null, true);
        LiteralNode thenExpr = new LiteralNode(1, 1, null, 1);
        LiteralNode elseExpr = new LiteralNode(1, 1, null, 2);
        TernaryNode node = new TernaryNode(1, 1, null, condition, thenExpr, elseExpr);

        GenerationResult result = generator.visit(node, context);

        // Should have: const true, jumpIf, const 1, jump, const 2
        // With jump positions set correctly
        assertEquals(5, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof JumpIfInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof JumpInstruction);
        assertTrue(result.getInstructions().get(4) instanceof ConstInstruction);
    }

    @Test
    public void testVisitTernaryNode_WithIdentifiers() throws Exception {
        // flag ? a : b
        IdentifierNode condition = new IdentifierNode(1, 1, null, "flag");
        IdentifierNode thenExpr = new IdentifierNode(1, 1, null, "a");
        IdentifierNode elseExpr = new IdentifierNode(1, 1, null, "b");
        TernaryNode node = new TernaryNode(1, 1, null, condition, thenExpr, elseExpr);

        GenerationResult result = generator.visit(node, context);

        assertEquals(5, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(2) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(4) instanceof LoadInstruction);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testVisitLambdaNode_NotImplemented() throws Exception {
        LambdaNode node = new LambdaNode(1, 1, null, Collections.emptyList(), null);
        generator.visit(node, context);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testVisitMethodCallNode_NotImplemented() throws Exception {
        MethodCallNode node = new MethodCallNode(1, 1, null, null, "test", Collections.emptyList());
        generator.visit(node, context);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testVisitConstructorCallNode_NotImplemented() throws Exception {
        ConstructorCallNode node = new ConstructorCallNode(1, 1, null, null, Collections.emptyList());
        generator.visit(node, context);
    }

    @Test
    public void testVisitProgramNode_Empty() throws Exception {
        ProgramNode node = new ProgramNode(1, 1, Collections.emptyList());
        GenerationResult result = generator.visit(node, context);

        assertTrue(result.getInstructions().isEmpty());
        assertFalse(result.isExpressionValue());
        assertEquals(0, result.getStackEffect());
    }

    @Test
    public void testVisitProgramNode_SingleStatement() throws Exception {
        // Just an expression statement
        LiteralNode expr = new LiteralNode(1, 1, null, 42);
        ProgramNode node = new ProgramNode(1, 1, Collections.singletonList(expr));

        GenerationResult result = generator.visit(node, context);

        // Should have const instruction + pop instruction
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof PopInstruction);
    }

    @Test
    public void testVisitBlockNode_SingleStatement() throws Exception {
        // { 42 }
        BlockNode node = new BlockNode(1, 1, null, Collections.singletonList(
                new LiteralNode(1, 1, null, 42)
        ));

        GenerationResult result = generator.visit(node, context);

        // Should have const instruction + pop instruction
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof PopInstruction);
    }
}
