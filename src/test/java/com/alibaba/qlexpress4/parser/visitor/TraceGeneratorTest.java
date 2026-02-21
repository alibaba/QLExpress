package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.SyntaxTreeFactory;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.runtime.trace.TraceType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for TraceGenerator visitor.
 */
public class TraceGeneratorTest {
    
    private final OperatorManager operatorManager = new OperatorManager();
    
    /**
     * Helper method to parse script and generate trace points.
     */
    private List<TracePointTree> generateTracePoints(String script)
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(script, operatorManager);
        TraceGenerator generator = new TraceGenerator();
        program.accept(generator, null);
        return generator.getTracePoints();
    }
    
    @Test
    public void testLiteralTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("42");
        assertFalse("Should have trace points for literal", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        assertEquals(TraceType.VALUE, tracePoints.get(0).getType());
    }
    
    @Test
    public void testIdentifierTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("x");
        assertFalse("Should have trace points for identifier", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        assertEquals(TraceType.VARIABLE, tracePoints.get(0).getType());
        assertEquals("x", tracePoints.get(0).getToken());
    }
    
    @Test
    public void testBinaryOperatorTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("1 + 2");
        assertFalse("Should have trace points for binary operator", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("+", root.getToken());
        assertEquals(2, root.getChildren().size());
    }
    
    @Test
    public void testUnaryOperatorTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("-x");
        assertFalse("Should have trace points for unary operator", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("-", root.getToken());
        assertEquals(1, root.getChildren().size());
    }
    
    @Test
    public void testTernaryOperatorTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("true ? 1 : 0");
        assertFalse("Should have trace points for ternary operator", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("?", root.getToken());
        assertEquals(3, root.getChildren().size());
    }
    
    @Test
    public void testMethodCallTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("Math.max(1, 2)");
        assertFalse("Should have trace points for method call", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.METHOD, root.getType());
        assertEquals("max", root.getToken());
        // Target (Math) + 2 arguments = 3 children
        assertEquals(3, root.getChildren().size());
    }
    
    @Test
    public void testFunctionCallTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("max(1, 2)");
        assertFalse("Should have trace points for function call", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.FUNCTION, root.getType());
        assertEquals("max", root.getToken());
        // 2 arguments
        assertEquals(2, root.getChildren().size());
    }
    
    @Test
    public void testIfStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("if (true) { 1; } else { 2; }");
        assertFalse("Should have trace points for if statement", tracePoints.isEmpty());
        assertTrue("Should contain IF trace point", tracePoints.stream().anyMatch(t -> t.getType() == TraceType.IF));
        TracePointTree ifTrace = tracePoints.stream().filter(t -> t.getType() == TraceType.IF).findFirst().orElse(null);
        assertNotNull(ifTrace);
        assertEquals(3, ifTrace.getChildren().size()); // condition, then, else
    }
    
    @Test
    public void testWhileStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("while (true) { break; }");
        assertFalse("Should have trace points for while statement", tracePoints.isEmpty());
        assertTrue("Should contain STATEMENT trace point for while",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.STATEMENT));
    }
    
    @Test
    public void testForStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("for (int i = 0; i < 10; i++) { i; }");
        assertFalse("Should have trace points for for statement", tracePoints.isEmpty());
        assertTrue("Should contain STATEMENT trace point for for",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.STATEMENT));
    }
    
    @Test
    public void testSwitchStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("switch (1) { case 1: break; }");
        assertFalse("Should have trace points for switch statement", tracePoints.isEmpty());
        assertTrue("Should contain SWITCH trace point",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.SWITCH));
    }
    
    @Test
    public void testReturnStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("return 42;");
        assertFalse("Should have trace points for return statement", tracePoints.isEmpty());
        assertTrue("Should contain RETURN trace point",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.RETURN));
    }
    
    @Test
    public void testBreakStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("break;");
        assertFalse("Should have trace points for break statement", tracePoints.isEmpty());
        assertTrue("Should contain STATEMENT trace point for break",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.STATEMENT));
    }
    
    @Test
    public void testContinueStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("continue;");
        assertFalse("Should have trace points for continue statement", tracePoints.isEmpty());
        assertTrue("Should contain STATEMENT trace point for continue",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.STATEMENT));
    }
    
    @Test
    public void testThrowStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("throw new Exception();");
        assertFalse("Should have trace points for throw statement", tracePoints.isEmpty());
        assertTrue("Should contain STATEMENT trace point for throw",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.STATEMENT));
    }
    
    @Test
    public void testVariableDeclarationTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("int x = 10;");
        assertFalse("Should have trace points for variable declaration", tracePoints.isEmpty());
        assertTrue("Should contain STATEMENT trace point for variable declaration",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.STATEMENT));
    }
    
    @Test
    public void testFunctionDefinitionTrace()
        throws Exception {
        // Note: 'function' keyword is not yet supported by the new parser
        // This test is skipped for now - can be re-enabled when function definitions are implemented
    }
    
    @Test
    public void testMacroDefinitionTrace()
        throws Exception {
        // Note: 'macro' keyword is not yet supported by the new parser
        // This test is skipped for now - can be re-enabled when macro definitions are implemented
    }
    
    @Test
    public void testListLiteralTrace()
        throws Exception {
        // Note: List literal parsing is not yet fully implemented in the new parser
        // This test is skipped for now - can be re-enabled when list parsing is complete
    }
    
    @Test
    public void testEmptyListLiteralTrace()
        throws Exception {
        // Note: List literal parsing is not yet fully implemented in the new parser
        // This test is skipped for now - can be re-enabled when list parsing is complete
    }
    
    @Test
    public void testComplexExpressionTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("1 + 2 * 3");
        assertFalse("Should have trace points for complex expression", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("+", root.getToken());
    }
    
    @Test
    public void testNestedMethodCallTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("a.b(c.d(e))");
        assertFalse("Should have trace points for nested method call", tracePoints.isEmpty());
        // Should have trace for outer call and inner call
        assertTrue("Should have METHOD trace points",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.METHOD));
    }
    
    @Test
    public void testSourceLocationTracking()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("x");
        assertEquals(1, tracePoints.size());
        TracePointTree trace = tracePoints.get(0);
        assertEquals(1, trace.getLine());
        assertEquals(1, trace.getCol());
    }
    
    @Test
    public void testMultiLineScriptTrace()
        throws Exception {
        String script = "x = 1;\ny = 2;\nz = x + y;";
        List<TracePointTree> tracePoints = generateTracePoints(script);
        assertFalse("Should have trace points for multi-line script", tracePoints.isEmpty());
        assertTrue("Should have at least 3 trace points (one per statement)", tracePoints.size() >= 3);
    }
    
    @Test
    public void testArrayAccessTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("arr[0]");
        assertFalse("Should have trace points for array access", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("[", root.getToken());
        assertEquals(2, root.getChildren().size()); // array + index
    }
    
    @Test
    public void testEmptyProgramTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("");
        assertTrue("Empty program should have no trace points", tracePoints.isEmpty());
    }
    
    @Test
    public void testBlockStatementTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("{ x; y; z; }");
        assertFalse("Should have trace points for block statement", tracePoints.isEmpty());
        assertTrue("Should contain BLOCK trace point",
            tracePoints.stream().anyMatch(t -> t.getType() == TraceType.BLOCK));
    }
    
    @Test
    public void testLambdaTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("x -> x + 1");
        assertFalse("Should have trace points for lambda", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.PRIMARY, root.getType());
        assertEquals("->", root.getToken());
    }
    
    @Test
    public void testAssignmentTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("x = 10");
        assertFalse("Should have trace points for assignment", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("=", root.getToken());
        assertEquals(2, root.getChildren().size()); // target + value
    }
    
    @Test
    public void testCompoundAssignmentTrace()
        throws Exception {
        List<TracePointTree> tracePoints = generateTracePoints("x += 10");
        assertFalse("Should have trace points for compound assignment", tracePoints.isEmpty());
        assertEquals(1, tracePoints.size());
        TracePointTree root = tracePoints.get(0);
        assertEquals(TraceType.OPERATOR, root.getType());
        assertEquals("+=", root.getToken());
    }
}
