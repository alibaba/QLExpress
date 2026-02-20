package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.parser.parser.QLexpressParser;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void testVisitArrayAccessNode() throws Exception {
        // arr[0]
        IdentifierNode array = new IdentifierNode(1, 1, null, "arr");
        LiteralNode index = new LiteralNode(1, 1, null, 0);
        ArrayAccessNode node = new ArrayAccessNode(1, 1, null, array, index);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof IndexInstruction);
    }

    @Test
    public void testVisitArrayLiteralNode() throws Exception {
        // [1, 2, 3]
        List<ExpressionNode> elements = Arrays.asList(
                new LiteralNode(1, 1, null, 1),
                new LiteralNode(1, 1, null, 2),
                new LiteralNode(1, 1, null, 3)
        );
        ArrayLiteralNode node = new ArrayLiteralNode(1, 1, null, elements);

        GenerationResult result = generator.visit(node, context);

        assertEquals(4, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof NewArrayInstruction);
    }

    @Test
    public void testVisitListLiteralNode() throws Exception {
        // [1, 2, 3]
        List<ExpressionNode> elements = Arrays.asList(
                new LiteralNode(1, 1, null, 1),
                new LiteralNode(1, 1, null, 2),
                new LiteralNode(1, 1, null, 3)
        );
        ListLiteralNode node = new ListLiteralNode(1, 1, null, elements);

        GenerationResult result = generator.visit(node, context);

        assertEquals(4, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof NewListInstruction);
    }

    @Test
    public void testVisitMapLiteralNode() throws Exception {
        // {"a": 1, "b": 2}
        List<MapEntryNode> entries = Arrays.asList(
                new MapEntryNode(new LiteralNode(1, 1, null, "a"), new LiteralNode(1, 1, null, 1)),
                new MapEntryNode(new LiteralNode(1, 1, null, "b"), new LiteralNode(1, 1, null, 2))
        );
        MapLiteralNode node = new MapLiteralNode(1, 1, null, entries);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof NewMapInstruction);
    }

    @Test
    public void testVisitMethodCallNode() throws Exception {
        // obj.method(1, 2)
        IdentifierNode target = new IdentifierNode(1, 1, null, "obj");
        List<ExpressionNode> args = Arrays.asList(
                new LiteralNode(1, 1, null, 1),
                new LiteralNode(1, 1, null, 2)
        );
        MethodCallNode node = new MethodCallNode(1, 1, null, target, "method", args);

        GenerationResult result = generator.visit(node, context);

        assertEquals(4, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof MethodInvokeInstruction);
    }

    @Test
    public void testVisitMethodCallNode_NoTarget() throws Exception {
        // method(1, 2)
        List<ExpressionNode> args = Arrays.asList(
                new LiteralNode(1, 1, null, 1),
                new LiteralNode(1, 1, null, 2)
        );
        MethodCallNode node = new MethodCallNode(1, 1, null, null, "method", args);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof MethodInvokeInstruction);
    }

    @Test
    public void testVisitConstructorCallNode() throws Exception {
        // new MyClass(1, 2)
        List<ExpressionNode> args = Arrays.asList(
                new LiteralNode(1, 1, null, 1),
                new LiteralNode(1, 1, null, 2)
        );
        ConstructorCallNode node = new ConstructorCallNode(1, 1, null, "MyClass", args);

        GenerationResult result = generator.visit(node, context);

        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof NewInstanceInstruction);
    }

    @Test
    public void testVisitLambdaNode_Simple() throws Exception {
        // x -> x + 1
        List<ParameterNode> params = Collections.singletonList(
                new ParameterNode(null, "x")
        );
        BinaryOpNode body = new BinaryOpNode(1, 1, null,
                new IdentifierNode(1, 1, null, "x"),
                "+",
                new LiteralNode(1, 1, null, 1)
        );
        LambdaNode node = new LambdaNode(1, 1, null, params, body);

        GenerationResult result = generator.visit(node, context);

        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof LoadLambdaInstruction);
    }

    @Test
    public void testVisitLambdaNode_BlockBody() throws Exception {
        // () -> { 42 }
        List<ParameterNode> params = Collections.emptyList();
        BlockNode body = new BlockNode(1, 1, null, Collections.singletonList(
                new LiteralNode(1, 1, null, 42)
        ));
        LambdaNode node = new LambdaNode(1, 1, null, params, body);

        GenerationResult result = generator.visit(node, context);

        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadLambdaInstruction);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testVisitCastNode_NotImplemented() throws Exception {
        CastNode node = new CastNode(1, 1, null, null, null);
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
