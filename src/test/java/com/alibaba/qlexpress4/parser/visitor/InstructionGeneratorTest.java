package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.parser.parser.QLexpressParser;
import com.alibaba.qlexpress4.runtime.QResult;
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
    public void testVisitLiteralNode_Integer()
        throws Exception {
        LiteralNode node = new LiteralNode(1, 1, 0, null, 42);
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        QLInstruction instruction = result.getInstructions().get(0);
        assertTrue(instruction instanceof ConstInstruction);
        assertEquals(42, ((ConstInstruction)instruction).getConstObj());
    }
    
    @Test
    public void testVisitLiteralNode_String()
        throws Exception {
        LiteralNode node = new LiteralNode(1, 1, 0, null, "hello");
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertEquals("hello", ((ConstInstruction)result.getInstructions().get(0)).getConstObj());
    }
    
    @Test
    public void testVisitLiteralNode_Null()
        throws Exception {
        LiteralNode node = new LiteralNode(1, 1, 0, null, null);
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertNull(((ConstInstruction)result.getInstructions().get(0)).getConstObj());
    }
    
    @Test
    public void testVisitLiteralNode_Boolean()
        throws Exception {
        LiteralNode trueNode = new LiteralNode(1, 1, 0, null, true);
        GenerationResult trueResult = generator.visit(trueNode, context);
        assertEquals(true, ((ConstInstruction)trueResult.getInstructions().get(0)).getConstObj());
        
        LiteralNode falseNode = new LiteralNode(1, 1, 0, null, false);
        GenerationResult falseResult = generator.visit(falseNode, context);
        assertEquals(false, ((ConstInstruction)falseResult.getInstructions().get(0)).getConstObj());
    }
    
    @Test
    public void testVisitIdentifierNode()
        throws Exception {
        IdentifierNode node = new IdentifierNode(1, 1, 0, null, "myVar");
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        QLInstruction instruction = result.getInstructions().get(0);
        assertTrue(instruction instanceof LoadInstruction);
        assertEquals("myVar", ((LoadInstruction)instruction).getName());
    }
    
    @Test
    public void testVisitBinaryOpNode_Addition()
        throws Exception {
        // 1 + 2
        LiteralNode left = new LiteralNode(1, 1, 0, null, 1);
        LiteralNode right = new LiteralNode(1, 1, 0, null, 2);
        BinaryOpNode node = new BinaryOpNode(1, 1, 0, null, left, "+", right);
        
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
    public void testVisitBinaryOpNode_Multiplication()
        throws Exception {
        // 3 * 4
        LiteralNode left = new LiteralNode(1, 1, 0, null, 3);
        LiteralNode right = new LiteralNode(1, 1, 0, null, 4);
        BinaryOpNode node = new BinaryOpNode(1, 1, 0, null, left, "*", right);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(3, result.getInstructions().size());
        assertTrue(result.getInstructions().get(2) instanceof OperatorInstruction);
        
        OperatorInstruction opInstr = (OperatorInstruction)result.getInstructions().get(2);
        assertEquals("*", opInstr.getOperator().getOperator());
    }
    
    @Test
    public void testVisitBinaryOpNode_Comparison()
        throws Exception {
        // a > b
        IdentifierNode left = new IdentifierNode(1, 1, 0, null, "a");
        IdentifierNode right = new IdentifierNode(1, 1, 0, null, "b");
        BinaryOpNode node = new BinaryOpNode(1, 1, 0, null, left, ">", right);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(3, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(2) instanceof OperatorInstruction);
    }
    
    @Test
    public void testVisitBinaryOpNode_LogicalAnd()
        throws Exception {
        // true && false
        LiteralNode left = new LiteralNode(1, 1, 0, null, true);
        LiteralNode right = new LiteralNode(1, 1, 0, null, false);
        BinaryOpNode node = new BinaryOpNode(1, 1, 0, null, left, "&&", right);
        
        GenerationResult result = generator.visit(node, context);
        
        // Short-circuit AND generates: Const(left), JumpIf, Const(right), Operator
        assertEquals(4, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof JumpIfInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof OperatorInstruction);
        
        OperatorInstruction opInstr = (OperatorInstruction)result.getInstructions().get(3);
        assertEquals("&&", opInstr.getOperator().getOperator());
    }
    
    @Test
    public void testVisitUnaryOpNode_PrefixMinus()
        throws Exception {
        // -5
        LiteralNode operand = new LiteralNode(1, 1, 0, null, 5);
        UnaryOpNode node = new UnaryOpNode(1, 1, 0, null, "-", operand, true);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(2, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof UnaryInstruction);
    }
    
    @Test
    public void testVisitUnaryOpNode_PrefixNot()
        throws Exception {
        // !true
        LiteralNode operand = new LiteralNode(1, 1, 0, null, true);
        UnaryOpNode node = new UnaryOpNode(1, 1, 0, null, "!", operand, true);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(1) instanceof UnaryInstruction);
    }
    
    @Test
    public void testVisitUnaryOpNode_PrefixIncrement()
        throws Exception {
        // ++x
        IdentifierNode operand = new IdentifierNode(1, 1, 0, null, "x");
        UnaryOpNode node = new UnaryOpNode(1, 1, 0, null, "++", operand, true);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof UnaryInstruction);
    }
    
    @Test
    public void testVisitTernaryNode()
        throws Exception {
        // true ? 1 : 2
        LiteralNode condition = new LiteralNode(1, 1, 0, null, true);
        LiteralNode thenExpr = new LiteralNode(1, 1, 0, null, 1);
        LiteralNode elseExpr = new LiteralNode(1, 1, 0, null, 2);
        TernaryNode node = new TernaryNode(1, 1, 0, null, condition, thenExpr, elseExpr);
        
        GenerationResult result = generator.visit(node, context);
        
        // Should have: const true, jumpIf, const 1, jump, const 2, tracePeek
        // TracePeekInstruction was added for trace position tracking (US-034)
        assertEquals(6, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof JumpIfInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof JumpInstruction);
        assertTrue(result.getInstructions().get(4) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(5) instanceof TracePeekInstruction);
    }
    
    @Test
    public void testVisitTernaryNode_WithIdentifiers()
        throws Exception {
        // flag ? a : b
        IdentifierNode condition = new IdentifierNode(1, 1, 0, null, "flag");
        IdentifierNode thenExpr = new IdentifierNode(1, 1, 0, null, "a");
        IdentifierNode elseExpr = new IdentifierNode(1, 1, 0, null, "b");
        TernaryNode node = new TernaryNode(1, 1, 0, null, condition, thenExpr, elseExpr);
        
        GenerationResult result = generator.visit(node, context);
        
        // Should have: load flag, jumpIf, load a, jump, load b, tracePeek
        // TracePeekInstruction was added for trace position tracking (US-034)
        assertEquals(6, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(2) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(4) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(5) instanceof TracePeekInstruction);
    }
    
    @Test
    public void testVisitArrayAccessNode()
        throws Exception {
        // arr[0]
        IdentifierNode array = new IdentifierNode(1, 1, 0, null, "arr");
        LiteralNode index = new LiteralNode(1, 1, 0, null, 0);
        ArrayAccessNode node = new ArrayAccessNode(1, 1, 0, null, array, index);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof IndexInstruction);
    }
    
    @Test
    public void testVisitArrayLiteralNode()
        throws Exception {
        // [1, 2, 3]
        List<ExpressionNode> elements = Arrays.asList(new LiteralNode(1, 1, 0, null, 1),
            new LiteralNode(1, 1, 0, null, 2),
            new LiteralNode(1, 1, 0, null, 3));
        ArrayLiteralNode node = new ArrayLiteralNode(1, 1, 0, null, elements);
        
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
    public void testVisitListLiteralNode()
        throws Exception {
        // [1, 2, 3]
        List<ExpressionNode> elements = Arrays.asList(new LiteralNode(1, 1, 0, null, 1),
            new LiteralNode(1, 1, 0, null, 2),
            new LiteralNode(1, 1, 0, null, 3));
        ListLiteralNode node = new ListLiteralNode(1, 1, 0, null, elements);
        
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
    public void testVisitMapLiteralNode()
        throws Exception {
        // {"a": 1, "b": 2}
        List<MapEntryNode> entries =
            Arrays.asList(new MapEntryNode(new LiteralNode(1, 1, 0, null, "a"), new LiteralNode(1, 1, 0, null, 1)),
                new MapEntryNode(new LiteralNode(1, 1, 0, null, "b"), new LiteralNode(1, 1, 0, null, 2)));
        MapLiteralNode node = new MapLiteralNode(1, 1, 0, null, entries);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof NewMapInstruction);
    }
    
    @Test
    public void testVisitMethodCallNode()
        throws Exception {
        // obj.method(1, 2)
        IdentifierNode target = new IdentifierNode(1, 1, 0, null, "obj");
        List<ExpressionNode> args = Arrays.asList(new LiteralNode(1, 1, 0, null, 1), new LiteralNode(1, 1, 0, null, 2));
        MethodCallNode node = new MethodCallNode(1, 1, 0, null, target, "method", args);
        
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
    public void testVisitMethodCallNode_NoTarget()
        throws Exception {
        // method(1, 2) - direct function call (no target)
        List<ExpressionNode> args = Arrays.asList(new LiteralNode(1, 1, 0, null, 1), new LiteralNode(1, 1, 0, null, 2));
        MethodCallNode node = new MethodCallNode(1, 1, 0, null, null, "method", args);
        
        GenerationResult result = generator.visit(node, context);
        
        // Should have: arg1, arg2, CallFunctionInstruction (no target instruction)
        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof CallFunctionInstruction);
    }
    
    @Test
    public void testVisitConstructorCallNode()
        throws Exception {
        // new MyClass(1, 2)
        List<ExpressionNode> args = Arrays.asList(new LiteralNode(1, 1, 0, null, 1), new LiteralNode(1, 1, 0, null, 2));
        ConstructorCallNode node = new ConstructorCallNode(1, 1, 0, null, "MyClass", args);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(3, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(2) instanceof NewInstanceInstruction);
    }
    
    @Test
    public void testVisitLambdaNode_Simple()
        throws Exception {
        // x -> x + 1
        List<ParameterNode> params = Collections.singletonList(new ParameterNode(null, "x"));
        BinaryOpNode body = new BinaryOpNode(1, 1, 0, null, new IdentifierNode(1, 1, 0, null, "x"), "+",
            new LiteralNode(1, 1, 0, null, 1));
        LambdaNode node = new LambdaNode(1, 1, 0, null, params, body);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof LoadLambdaInstruction);
    }
    
    @Test
    public void testVisitLambdaNode_BlockBody()
        throws Exception {
        // () -> { 42 }
        List<ParameterNode> params = Collections.emptyList();
        BlockNode body = new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 42)));
        LambdaNode node = new LambdaNode(1, 1, 0, null, params, body);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadLambdaInstruction);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitCastNode_NotImplemented()
        throws Exception {
        CastNode node = new CastNode(1, 1, 0, null, null, null);
        generator.visit(node, context);
    }
    
    @Test
    public void testVisitProgramNode_Empty()
        throws Exception {
        ProgramNode node = new ProgramNode(1, 1, 0, null, Collections.emptyList());
        GenerationResult result = generator.visit(node, context);
        
        assertTrue(result.getInstructions().isEmpty());
        assertFalse(result.isExpressionValue());
        assertEquals(0, result.getStackEffect());
    }
    
    @Test
    public void testVisitProgramNode_SingleStatement()
        throws Exception {
        // Just an expression statement
        LiteralNode expr = new LiteralNode(1, 1, 0, null, 42);
        ProgramNode node = new ProgramNode(1, 1, 0, null, Collections.singletonList(expr));
        
        GenerationResult result = generator.visit(node, context);
        
        // Should have const instruction only (last statement's value is kept on stack)
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
    }
    
    @Test
    public void testVisitBlockNode_SingleStatement()
        throws Exception {
        // { 42 }
        BlockNode node = new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 42)));

        GenerationResult result = generator.visit(node, context);

        // Should have const instruction and trace peek instruction (block produces a value)
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof TracePeekInstruction);
    }
    
    @Test
    public void testVisitReturnNode_WithValue()
        throws Exception {
        // return 42;
        ReturnNode node = new ReturnNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, 42));
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(2, result.getInstructions().size());
        assertEquals(0, result.getStackEffect());
        assertFalse(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ReturnInstruction);
    }
    
    @Test
    public void testVisitReturnNode_NoValue()
        throws Exception {
        // return;
        ReturnNode node = new ReturnNode(1, 1, 0, null, null);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ReturnInstruction);
    }
    
    @Test
    public void testVisitBreakNode()
        throws Exception {
        // break;
        BreakNode node = new BreakNode(1, 1, 0, null);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertEquals(0, result.getStackEffect());
        assertFalse(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof BreakContinueInstruction);
        assertEquals(QResult.LOOP_BREAK_RESULT,
            ((BreakContinueInstruction)result.getInstructions().get(0)).getResult());
    }
    
    @Test
    public void testVisitContinueNode()
        throws Exception {
        // continue;
        ContinueNode node = new ContinueNode(1, 1, 0, null);
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertEquals(0, result.getStackEffect());
        assertFalse(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof BreakContinueInstruction);
        assertEquals(QResult.LOOP_CONTINUE_RESULT,
            ((BreakContinueInstruction)result.getInstructions().get(0)).getResult());
    }
    
    @Test
    public void testVisitThrowNode()
        throws Exception {
        // throw "error";
        ThrowNode node = new ThrowNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, "error"));
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(2, result.getInstructions().size());
        assertEquals(0, result.getStackEffect());
        assertFalse(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ThrowInstruction);
    }
    
    @Test
    public void testVisitVariableDeclarationNode_WithInitialValue()
        throws Exception {
        // int x = 42;
        VariableDeclarationNode node =
            new VariableDeclarationNode(1, 1, 0, null, "int", "x", new LiteralNode(1, 1, 0, null, 42));

        GenerationResult result = generator.visit(node, context);

        // Instructions: ConstInstruction(42), DefineLocalInstruction, ConstInstruction(null), TracePeekInstruction, PopInstruction
        assertEquals(5, result.getInstructions().size());
        assertEquals(0, result.getStackEffect());
        assertFalse(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof DefineLocalInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof TracePeekInstruction);
        assertTrue(result.getInstructions().get(4) instanceof PopInstruction);
    }

    @Test
    public void testVisitVariableDeclarationNode_NoInitialValue()
        throws Exception {
        // int x;
        VariableDeclarationNode node = new VariableDeclarationNode(1, 1, 0, null, "int", "x", null);

        GenerationResult result = generator.visit(node, context);

        // Instructions: ConstInstruction(null), DefineLocalInstruction, ConstInstruction(null), TracePeekInstruction, PopInstruction
        assertEquals(5, result.getInstructions().size());
        assertEquals(0, result.getStackEffect());
        assertFalse(result.isExpressionValue());

        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof DefineLocalInstruction);
        assertTrue(result.getInstructions().get(2) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(3) instanceof TracePeekInstruction);
        assertTrue(result.getInstructions().get(4) instanceof PopInstruction);
    }
    
    @Test
    public void testVisitAssignmentNode_Simple()
        throws Exception {
        // x = 42;
        AssignmentNode node = new AssignmentNode(1, 1, 0, null, new IdentifierNode(1, 1, 0, null, "x"), "=",
            new LiteralNode(1, 1, 0, null, 42));
        
        GenerationResult result = generator.visit(node, context);
        
        assertEquals(1, result.getInstructions().size());
        assertEquals(1, result.getStackEffect());
        assertTrue(result.isExpressionValue());
        
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
    }
    
    @Test
    public void testVisitIfNode_Simple()
        throws Exception {
        // if (true) { 42 }
        IfNode node = new IfNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, true),
            new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 42))), null);
        
        GenerationResult result = generator.visit(node, context);
        
        // In QLExpress, if statements can be used as expressions
        // When the then body produces a value (like a literal), the if node also produces a value
        // When there's no else body, null is pushed as the default else value
        assertTrue(result.isExpressionValue());
        // Verify it generates instructions (at minimum: condition + jump + then body + null)
        assertTrue(result.getInstructions().size() >= 4);
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof JumpIfPopInstruction);
    }
    
    @Test
    public void testVisitIfNode_WithElse()
        throws Exception {
        // if (false) { 1 } else { 2 }
        IfNode node = new IfNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, false),
            new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 1))),
            new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 2))));
        
        GenerationResult result = generator.visit(node, context);
        
        // In QLExpress, if statements can be used as expressions
        // When both branches produce values, the if node produces a value
        assertTrue(result.isExpressionValue());
        // Should have: condition, jumpIfPop, then body, jump, else body
        assertTrue(result.getInstructions().size() >= 4);
        assertTrue(result.getInstructions().get(0) instanceof ConstInstruction);
        assertTrue(result.getInstructions().get(1) instanceof JumpIfPopInstruction);
    }
    
    @Test
    public void testVisitWhileNode()
        throws Exception {
        // while (true) { 42 }
        WhileNode node = new WhileNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, true),
            new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 42))));
        
        GenerationResult result = generator.visit(node, context);
        
        assertFalse(result.isExpressionValue());
        // Should have a WhileInstruction
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof WhileInstruction);
    }
    
    @Test
    public void testVisitForNode_ForEach()
        throws Exception {
        // for (x : items) { }
        VariableDeclarationNode varDecl =
            new VariableDeclarationNode(1, 1, 0, null, "int", "x", new IdentifierNode(1, 1, 0, null, "items"));
        ForNode node =
            new ForNode(1, 1, 0, null, varDecl, null, null, new BlockNode(1, 1, 0, null, Collections.emptyList()));
        
        GenerationResult result = generator.visit(node, context);
        
        assertFalse(result.isExpressionValue());
        // Should have iterable load + ForEachInstruction
        assertEquals(2, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof LoadInstruction);
        assertTrue(result.getInstructions().get(1) instanceof ForEachInstruction);
    }
    
    @Test
    public void testVisitForNode_Traditional()
        throws Exception {
        // for (int i = 0; i < 10; i++) { }
        VariableDeclarationNode init =
            new VariableDeclarationNode(1, 1, 0, null, "int", "i", new LiteralNode(1, 1, 0, null, 0));
        BinaryOpNode condition = new BinaryOpNode(1, 1, 0, null, new IdentifierNode(1, 1, 0, null, "i"), "<",
            new LiteralNode(1, 1, 0, null, 10));
        UnaryOpNode update = new UnaryOpNode(1, 1, 0, null, "++", new IdentifierNode(1, 1, 0, null, "i"), false);
        ForNode node =
            new ForNode(1, 1, 0, null, init, condition, update, new BlockNode(1, 1, 0, null, Collections.emptyList()));
        
        GenerationResult result = generator.visit(node, context);
        
        assertFalse(result.isExpressionValue());
        // Should have a ForInstruction
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof ForInstruction);
    }
    
    @Test
    public void testVisitSwitchNode_Simple()
        throws Exception {
        // switch (1) { case 1: { 42 } }
        List<SwitchCaseNode> cases = Collections.singletonList(new SwitchCaseNode(new LiteralNode(1, 1, 0, null, 1),
            Collections.singletonList(new LiteralNode(1, 1, 0, null, 42))));
        SwitchNode node = new SwitchNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, 1), cases);
        
        GenerationResult result = generator.visit(node, context);
        
        assertFalse(result.isExpressionValue());
        // Should generate switch instructions
        assertTrue(result.getInstructions().size() >= 2);
    }
    
    @Test
    public void testVisitSwitchNode_WithDefault()
        throws Exception {
        // switch (x) { default: { 42 } }
        List<SwitchCaseNode> cases = Collections.singletonList(new SwitchCaseNode(null, // default case
            Collections.singletonList(new LiteralNode(1, 1, 0, null, 42))));
        SwitchNode node = new SwitchNode(1, 1, 0, null, new IdentifierNode(1, 1, 0, null, "x"), cases);
        
        GenerationResult result = generator.visit(node, context);
        
        assertFalse(result.isExpressionValue());
        assertTrue(result.getInstructions().size() >= 1);
    }
    
    @Test
    public void testVisitTryCatchNode_Simple()
        throws Exception {
        // try { 42 } catch (Exception e) { }
        List<CatchClauseNode> catchClauses =
            Collections.singletonList(new CatchClauseNode(Collections.singletonList("Exception"), "e",
                new BlockNode(1, 1, 0, null, Collections.emptyList())));
        TryCatchNode node = new TryCatchNode(1, 1, 0, null,
            new BlockNode(1, 1, 0, null, Collections.singletonList(new LiteralNode(1, 1, 0, null, 42))), catchClauses,
            null);
        
        GenerationResult result = generator.visit(node, context);
        
        // In QLExpress, try-catch can be used as an expression
        // When the try block produces a value, the try-catch node also produces a value
        assertTrue(result.isExpressionValue());
        // Should have a TryCatchInstruction
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof TryCatchInstruction);
    }
    
    @Test
    public void testVisitTryCatchNode_WithFinally()
        throws Exception {
        // try { } finally { }
        TryCatchNode node = new TryCatchNode(1, 1, 0, null, new BlockNode(1, 1, 0, null, Collections.emptyList()),
            Collections.emptyList(), new BlockNode(1, 1, 0, null, Collections.emptyList()));
        
        GenerationResult result = generator.visit(node, context);
        
        // In QLExpress, try-catch can be used as an expression
        // Even empty blocks produce a value (null in this case)
        assertTrue(result.isExpressionValue());
        assertEquals(1, result.getInstructions().size());
        assertTrue(result.getInstructions().get(0) instanceof TryCatchInstruction);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitTypeDeclarationNode_NotImplemented()
        throws Exception {
        TypeDeclarationNode node = new TypeDeclarationNode(1, 1, -1, null, "MyType");
        generator.visit(node, context);
    }
    
    @Test
    public void testVisitImportNode_NotImplemented()
        throws Exception {
        ImportNode node = new ImportNode(1, 1, -1, null, "java.util.List", false);
        GenerationResult result = generator.visit(node, context);
        // Import statements don't generate runtime instructions - they're handled by ImportManager
        assertTrue(result.getInstructions().isEmpty());
    }
    
    @Test
    public void testVisitFunctionDefinitionNode_NotImplemented()
        throws Exception {
        FunctionDefinitionNode node = new FunctionDefinitionNode(1, 1, 0, null, "myFunc", Collections.emptyList(),
            new BlockNode(1, 1, 0, null, Collections.emptyList()));
        GenerationResult result = generator.visit(node, context);
        // Function definition is now implemented
        assertTrue(result.getInstructions().get(0) instanceof DefineFunctionInstruction);
    }
    
    @Test
    public void testVisitMacroDefinitionNode_NotImplemented()
        throws Exception {
        MacroDefinitionNode node =
            new MacroDefinitionNode(1, 1, 0, null, "myMacro", new BlockNode(1, 1, 0, null, Collections.emptyList()));
        GenerationResult result = generator.visit(node, context);
        // Macro definition is now implemented but returns empty instructions (compile-time only)
        assertTrue(result.getInstructions().isEmpty());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitInstanceOfNode_NotImplemented()
        throws Exception {
        InstanceOfNode node = new InstanceOfNode(1, 1, 0, null, new LiteralNode(1, 1, 0, null, "test"), "String");
        generator.visit(node, context);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitTypeNode_NotImplemented()
        throws Exception {
        TypeNode node = new TypeNode(1, 1, 0, null, "String");
        generator.visit(node, context);
    }
}
