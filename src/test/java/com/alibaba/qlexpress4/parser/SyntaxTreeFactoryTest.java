package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.common.InterpolationMode;
import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.parser.parser.QLexpressParser.ParseException;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for SyntaxTreeFactory.
 * <p>
 * Tests the new parser-based SyntaxTreeFactory implementation.
 */
public class SyntaxTreeFactoryTest {
    
    private final OperatorManager operatorManager = new OperatorManager();
    
    @Test
    public void testBuildTree_SimpleExpression()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        
        StatementNode stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof BinaryOpNode);
        
        BinaryOpNode binaryOp = (BinaryOpNode)stmt;
        assertEquals("+", binaryOp.getOperator());
    }
    
    @Test
    public void testBuildTree_MultipleStatements()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("a = 1; b = 2; a + b", operatorManager);
        assertNotNull(program);
        assertEquals(3, program.getStatements().size());
    }
    
    @Test
    public void testBuildTree_IfStatement()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("if (true) { 1 } else { 2 }", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof IfNode);
    }
    
    @Test
    public void testBuildTree_WhileLoop()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("while (true) { break; }", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof WhileNode);
    }
    
    @Test
    public void testBuildTree_ForLoop()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("for (int i = 0; i < 10; i++) { i }", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ForNode);
    }
    
    @Test
    public void testBuildTree_ForEachLoop()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("for (int x : arr) { x }", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ForNode);
    }
    
    @Test
    public void testBuildTree_LambdaExpression()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("x -> x + 1", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof LambdaNode);
    }
    
    @Test
    public void testBuildTree_MethodCall()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("Math.max(1, 2)", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof MethodCallNode);
    }
    
    @Test
    public void testBuildTree_ConstructorCall()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("new ArrayList()", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ConstructorCallNode);
    }
    
    @Test
    public void testBuildTree_TernaryExpression()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("true ? 1 : 0", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof TernaryNode);
    }
    
    // Note: Array, map, and list literals are not yet fully implemented in the new parser
    // These tests are skipped for now - they will be added when the feature is complete
    
    @Test
    public void testBuildTree_ReturnStatement()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("return 42", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ReturnNode);
    }
    
    @Test
    public void testBuildTree_VariableDeclaration()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("int x = 10", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof VariableDeclarationNode);
    }
    
    @Test
    public void testBuildTree_EmptyProgram()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("", operatorManager);
        assertNotNull(program);
        assertEquals(0, program.getStatements().size());
    }
    
    @Test
    public void testBuildTree_WithPrintTree()
        throws ParseException {
        List<String> output = new ArrayList<>();
        ProgramNode program = SyntaxTreeFactory
            .buildTree("1 + 2", operatorManager, true, false, output::add, InterpolationMode.SCRIPT, "${", "}", false);
        
        assertNotNull(program);
        assertFalse("Should have printed output", output.isEmpty());
        assertTrue("Should contain token stream", output.get(0).contains("INTEGER"));
    }
    
    @Test
    public void testBuildTree_WithProfile()
        throws ParseException {
        List<String> output = new ArrayList<>();
        ProgramNode program = SyntaxTreeFactory
            .buildTree("1 + 2", operatorManager, false, true, output::add, InterpolationMode.SCRIPT, "${", "}", false);
        
        assertNotNull(program);
        assertFalse("Should have profile message", output.isEmpty());
        assertTrue("Should contain profiling message", output.get(0).contains("not yet supported"));
    }
    
    @Test
    public void testBuildTree_DifferentInterpolationModes()
        throws ParseException {
        // Test each interpolation mode
        for (InterpolationMode mode : InterpolationMode.values()) {
            ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager, false, false, s -> {
            }, mode, "${", "}", false);
            assertNotNull("Should work with interpolation mode: " + mode, program);
        }
    }
    
    @Test
    public void testBuildTree_CustomSelectorTokens()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager, false, false, s -> {
        }, InterpolationMode.SCRIPT, "#{", "}", false);
        
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
    }
    
    @Test
    public void testBuildTree_StrictNewlines()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 +\n2", operatorManager, false, false, s -> {
        }, InterpolationMode.SCRIPT, "${", "}", true);
        
        assertNotNull(program);
        // In strict mode, newlines are significant tokens
        // The parser should still handle the expression
    }
    
    @Test
    public void testBuildTree_ComplexExpression()
        throws ParseException {
        String script = "a = 0;\n" + "if (a > 0) {\n" + "  return 1;\n" + "} else {\n"
            + "  for (int i = 0; i < 10; i++) {\n" + "    a = a + i;\n" + "  }\n" + "  return a;\n" + "}";
        
        ProgramNode program = SyntaxTreeFactory.buildTree(script, operatorManager);
        assertNotNull(program);
        assertTrue("Should have multiple statements", program.getStatements().size() > 1);
    }
    
    @Test(expected = ParseException.class)
    public void testBuildTree_SyntaxError()
        throws ParseException {
        SyntaxTreeFactory.buildTree("if (true", operatorManager);
    }
    
    @Test
    public void testBuildTree_DefaultOverload()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
    }
    
    @Test
    public void testBuildTree_DebugOverload()
        throws ParseException {
        List<String> output = new ArrayList<>();
        ProgramNode program = SyntaxTreeFactory.buildTreeWithDebug("1 + 2", operatorManager, output::add);
        
        assertNotNull(program);
        assertFalse("Should have debug output", output.isEmpty());
    }
    
    @Test
    public void testBuildTree_SwitchStatement()
        throws ParseException {
        ProgramNode program =
            SyntaxTreeFactory.buildTree("switch (x) { case 1: break; default: break; }", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof SwitchNode);
    }
    
    @Test
    public void testBuildTree_TryCatchFinally()
        throws ParseException {
        ProgramNode program =
            SyntaxTreeFactory.buildTree("try { 1 } catch (Exception e) { 2 } finally { 3 }", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof TryCatchNode);
    }
    
    @Test
    public void testBuildTree_ThrowStatement()
        throws ParseException {
        ProgramNode program = SyntaxTreeFactory.buildTree("throw new Exception()", operatorManager);
        assertNotNull(program);
        assertEquals(1, program.getStatements().size());
        assertTrue(program.getStatements().get(0) instanceof ThrowNode);
    }
    
    @Test
    public void testBuildTree_BreakContinue()
        throws ParseException {
        ProgramNode program =
            SyntaxTreeFactory.buildTree("while (true) { if (true) { break; } else { continue; } }", operatorManager);
        assertNotNull(program);
    }
}
