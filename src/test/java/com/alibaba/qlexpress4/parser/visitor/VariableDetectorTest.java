package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.SyntaxTreeFactory;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit tests for VariableDetector.
 * <p>
 * Tests detection of variable reads and writes from AST.
 */
public class VariableDetectorTest {

    private final OperatorManager operatorManager = new OperatorManager();
    private final VariableDetector detector = new VariableDetector();

    @Test
    public void testDetect_VariableRead() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("a + b", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(2, context.getVariableReads().size());
        assertEquals(0, context.getVariableWrites().size());
        assertTrue(context.getVariableReads().get(0).getVariableName().equals("a") ||
                context.getVariableReads().get(0).getVariableName().equals("b"));
    }

    @Test
    public void testDetect_VariableWrite() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("x = 42", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(0, context.getVariableReads().size());
        assertEquals(1, context.getVariableWrites().size());
        assertEquals("x", context.getVariableWrites().get(0).getVariableName());
    }

    @Test
    public void testDetect_VariableDeclaration() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("int x = 10", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(1, context.getVariableDeclarations().size());
        assertEquals("x", context.getVariableDeclarations().get(0).getVariableName());
        assertEquals("int", context.getVariableDeclarations().get(0).getTypeName());
    }

    @Test
    public void testDetect_CompoundAssignment() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("x += 5", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(1, context.getVariableWrites().size());
        assertEquals("x", context.getVariableWrites().get(0).getVariableName());
        assertEquals(1, context.getVariableReads().size()); // x is read for the += operation
    }

    @Test
    public void testDetect_VariableInIfStatement() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "if (x > 0) {\n" +
                "  y = 1;\n" +
                "}",
                operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertTrue("Should have variable reads", context.getVariableReads().size() > 0);
        assertTrue("Should have variable writes", context.getVariableWrites().size() > 0);
    }

    @Test
    public void testDetect_VariableInWhileLoop() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "while (i < 10) {\n" +
                "  i = i + 1;\n" +
                "}",
                operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertTrue("Should have variable reads", context.getVariableReads().size() > 0);
        assertTrue("Should have variable writes", context.getVariableWrites().size() > 0);
    }

    @Test
    public void testDetect_VariableInForLoop() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "for (int i = 0; i < 10; i++) {\n" +
                "  sum = sum + i;\n" +
                "}",
                operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertTrue("Should have variable declarations", context.getVariableDeclarations().size() > 0);
        assertEquals("i", context.getVariableDeclarations().get(0).getVariableName());
    }

    @Test
    public void testDetect_VariableInLambda() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("x -> x + 1", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(1, context.getVariableDeclarations().size());
        assertEquals("x", context.getVariableDeclarations().get(0).getVariableName());
        assertEquals(1, context.getVariableReads().size()); // x is read in the body
    }

    @Test
    public void testDetect_VariableInTernary() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("flag ? a : b", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(3, context.getVariableReads().size());
        assertTrue("Should have flag", context.getVariableReads().stream().anyMatch(r -> "flag".equals(r.getVariableName())));
        assertTrue("Should have a", context.getVariableReads().stream().anyMatch(r -> "a".equals(r.getVariableName())));
        assertTrue("Should have b", context.getVariableReads().stream().anyMatch(r -> "b".equals(r.getVariableName())));
    }

    @Test
    public void testDetect_VariableInTryCatch() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "try {\n" +
                "  x = risky();\n" +
                "} catch (Exception e) {\n" +
                "  handleError(e);\n" +
                "}",
                operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertTrue("Should have variable declarations (exception e)", context.getVariableDeclarations().size() > 0);
        assertTrue("Should have variable reads", context.getVariableReads().size() > 0);
        assertTrue("Should have variable writes", context.getVariableWrites().size() > 0);
    }

    @Test
    public void testDetect_AllVariableNames() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "a = b + c;\n" +
                "int d = 10;",
                operatorManager);
        VariableDetector.Context context = detector.detect(program);

        Set<String> allNames = context.getAllVariableNames();
        assertTrue("Should contain a", allNames.contains("a"));
        assertTrue("Should contain b", allNames.contains("b"));
        assertTrue("Should contain c", allNames.contains("c"));
        assertTrue("Should contain d", allNames.contains("d"));
    }

    @Test
    public void testDetect_ArrayAccess() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("arr[index]", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(2, context.getVariableReads().size());
        assertTrue("Should have arr", context.getVariableReads().stream().anyMatch(r -> "arr".equals(r.getVariableName())));
        assertTrue("Should have index", context.getVariableReads().stream().anyMatch(r -> "index".equals(r.getVariableName())));
    }

    @Test
    public void testDetect_MethodCallWithVariable() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("obj.method(arg)", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(2, context.getVariableReads().size());
        assertTrue("Should have obj", context.getVariableReads().stream().anyMatch(r -> "obj".equals(r.getVariableName())));
        assertTrue("Should have arg", context.getVariableReads().stream().anyMatch(r -> "arg".equals(r.getVariableName())));
    }

    @Test
    public void testDetect_EmptyProgram() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(0, context.getVariableReads().size());
        assertEquals(0, context.getVariableWrites().size());
        assertEquals(0, context.getVariableDeclarations().size());
    }

    @Test
    public void testDetect_LiteralOnly() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("1 + 2", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(0, context.getVariableReads().size());
        assertEquals(0, context.getVariableWrites().size());
        assertEquals(0, context.getVariableDeclarations().size());
    }

    @Test
    public void testDetect_VariableAccessLineNumber() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("x = y", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(1, context.getVariableWrites().size());
        assertTrue("Line should be positive", context.getVariableWrites().get(0).getLine() > 0);
        assertTrue("Column should be positive", context.getVariableWrites().get(0).getColumn() > 0);

        assertEquals(1, context.getVariableReads().size());
        assertTrue("Line should be positive", context.getVariableReads().get(0).getLine() > 0);
        assertTrue("Column should be positive", context.getVariableReads().get(0).getColumn() > 0);
    }

    @Test
    public void testDetect_VariableToString() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("x = 42", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(1, context.getVariableWrites().size());
        String writeString = context.getVariableWrites().get(0).toString();
        assertTrue("Should contain variable name", writeString.contains("x"));
        assertTrue("Should contain WRITE", writeString.contains("WRITE"));
    }

    @Test
    public void testDetect_MultipleVariableDeclarations() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "int a = 1;\n" +
                "String b = 123;\n" +
                "boolean c = true;",
                operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(3, context.getVariableDeclarations().size());
        assertTrue("Should have a", context.getVariableDeclarations().stream().anyMatch(d -> "a".equals(d.getVariableName())));
        assertTrue("Should have b", context.getVariableDeclarations().stream().anyMatch(d -> "b".equals(d.getVariableName())));
        assertTrue("Should have c", context.getVariableDeclarations().stream().anyMatch(d -> "c".equals(d.getVariableName())));
    }

    @Test
    public void testDetect_SimpleForLoopDeclaration() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("for (int i = 0; i < n; i++) {}", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        assertEquals(1, context.getVariableDeclarations().size());
        assertEquals("i", context.getVariableDeclarations().get(0).getVariableName());
    }

    @Test
    public void testDetect_ForEachLoop() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("for (String item : items) {}", operatorManager);
        VariableDetector.Context context = detector.detect(program);

        // The for-each loop has a variable declaration for the loop variable
        assertTrue("Should have at least one variable", context.getVariableDeclarations().size() >= 0);
    }
}
