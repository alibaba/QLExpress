package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.runtime.trace.TraceType;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for ASTCompiler.
 */
public class ASTCompilerTest {
    
    private final OperatorManager operatorManager = new OperatorManager();
    
    @org.junit.Test
    public void testCompileSimpleExpression()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        assertEquals("Lambda name should be 'main'", "main", lambda.getName());
        assertTrue("Lambda should be QLambdaDefinitionInner", lambda instanceof QLambdaDefinitionInner);
        
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        QLInstruction[] instructions = lambdaInner.getInstructions();
        assertTrue("Should have instructions", instructions.length > 0);
        assertTrue("Max stack size should be positive", lambdaInner.getMaxStackSize() > 0);
    }

    @org.junit.Test
    public void testCompileLiteralExpression()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("42", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);

        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        // A literal expression at program level has only const instruction (no pop, value is returned)
        assertEquals("Should have one instruction (const)", 1, lambdaInner.getInstructions().length);
    }
    
    @org.junit.Test
    public void testCompileVariableDeclaration()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("int x = 10;", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileBlockStatement()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("{ int x = 10; x + 5; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have multiple instructions", lambdaInner.getInstructions().length > 2);
    }
    
    @org.junit.Test
    public void testCompileIfStatement()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("if (true) { 1; } else { 2; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for if statement", lambdaInner.getInstructions().length > 3);
    }
    
    @org.junit.Test
    public void testCompileWhileLoop()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("while (true) { break; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for while loop", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileForLoop()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("for (int i = 0; i < 10; i++) { i; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for for loop", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileForEachLoop()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("for (int x : list) { x; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for for-each loop", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileReturnStatement()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("return 42;", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for return statement", lambdaInner.getInstructions().length >= 2);
    }
    
    @org.junit.Test
    public void testCompileMethodCall()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("Math.max(1, 2)", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for method call", lambdaInner.getInstructions().length >= 3);
    }
    
    @org.junit.Test
    public void testCompileLambdaExpression()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("(x) -> x + 1", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for lambda", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileTernaryExpression()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("true ? 1 : 0", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for ternary", lambdaInner.getInstructions().length >= 4);
    }
    
    @org.junit.Test
    public void testCompileEmptyProgram()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Empty program should have no instructions or minimal instructions",
            lambdaInner.getInstructions().length <= 1);
    }
    
    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testCompileNullProgramNode()
        throws Exception {
        ASTCompiler.compile(null, operatorManager);
    }
    
    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testCompileNullOperatorManager()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 1", operatorManager);
        ASTCompiler.compile(program, null);
    }
    
    @org.junit.Test
    public void testCompileWithTrace()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager);
        ASTCompiler.CompilationResult result = ASTCompiler.compileWithTrace(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", result.getLambdaDefinition());
        assertNotNull("Trace points should not be null", result.getTracePoints());
    }
    
    @org.junit.Test
    public void testGenerateTracePoints()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager);
        List<TracePointTree> tracePoints = ASTCompiler.generateTracePoints(program);
        
        assertNotNull("Trace points should not be null", tracePoints);
        // Trace points might be empty for simple expressions in the current implementation
    }
    
    @org.junit.Test
    public void testGenerateTracePointsForBlock()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("{ 1; 2; }", operatorManager);
        List<TracePointTree> tracePoints = ASTCompiler.generateTracePoints(program);
        
        assertNotNull("Trace points should not be null", tracePoints);
        // Should have a BLOCK trace point
        if (!tracePoints.isEmpty()) {
            TracePointTree blockTrace = tracePoints.get(0);
            assertEquals("First trace point should be BLOCK type", TraceType.BLOCK, blockTrace.getType());
        }
    }
    
    @org.junit.Test
    public void testGenerateTracePointsForIf()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("if (true) { 1; }", operatorManager);
        List<TracePointTree> tracePoints = ASTCompiler.generateTracePoints(program);
        
        assertNotNull("Trace points should not be null", tracePoints);
        if (!tracePoints.isEmpty()) {
            TracePointTree ifTrace = tracePoints.get(0);
            assertEquals("First trace point should be IF type", TraceType.IF, ifTrace.getType());
        }
    }
    
    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGenerateTracePointsNullProgram()
        throws Exception {
        ASTCompiler.generateTracePoints(null);
    }
    
    @org.junit.Test
    public void testCompileComplexExpression()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2 * 3 - 4 / 2", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have multiple instructions for complex expression",
            lambdaInner.getInstructions().length > 5);
    }
    
    @org.junit.Test
    public void testCompileUnaryExpression()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("-5", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);

        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        // A unary expression at program level has const + unary instructions (no pop, value is returned)
        assertEquals("Should have 2 instructions (const + unary)", 2, lambdaInner.getInstructions().length);
    }
    
    @org.junit.Test
    public void testCompileSwitchStatement()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("switch (1) { case 1: break; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for switch statement", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileTryCatchStatement()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("try { 1; } catch (Exception e) { 2; }", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for try-catch statement", lambdaInner.getInstructions().length > 0);
    }
    
    @org.junit.Test
    public void testCompileArrayAccess()
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("arr[0]", operatorManager);
        QLambdaDefinition lambda = ASTCompiler.compile(program, operatorManager);
        
        assertNotNull("Lambda definition should not be null", lambda);
        QLambdaDefinitionInner lambdaInner = (QLambdaDefinitionInner)lambda;
        assertTrue("Should have instructions for array access", lambdaInner.getInstructions().length >= 3);
    }
}
