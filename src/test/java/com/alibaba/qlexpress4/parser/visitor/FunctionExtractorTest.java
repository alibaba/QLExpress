package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.SyntaxTreeFactory;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for FunctionExtractor.
 * <p>
 * Tests extraction of function calls from AST.
 */
public class FunctionExtractorTest {

    private final OperatorManager operatorManager = new OperatorManager();
    private final FunctionExtractor extractor = new FunctionExtractor();

    @Test
    public void testExtract_DirectFunctionCall() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("myFunction(1, 2)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        FunctionExtractor.FunctionCall call = calls.get(0);
        assertEquals(FunctionExtractor.FunctionCallType.DIRECT_CALL, call.getType());
        assertEquals("myFunction", call.getName());
        assertEquals(2, call.getArity());
    }

    @Test
    public void testExtract_MethodCall() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("obj.method(arg)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        FunctionExtractor.FunctionCall call = calls.get(0);
        assertEquals(FunctionExtractor.FunctionCallType.METHOD_CALL, call.getType());
        assertEquals("method", call.getName());
        assertEquals(1, call.getArity());
    }

    @Test
    public void testExtract_StaticCall() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("Math.max(1, 2)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        FunctionExtractor.FunctionCall call = calls.get(0);
        assertEquals(FunctionExtractor.FunctionCallType.STATIC_CALL, call.getType());
        assertEquals("Math.max", call.getName());
        assertEquals(2, call.getArity());
    }

    @Test
    public void testExtract_ConstructorCall() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("new ArrayList()", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        FunctionExtractor.FunctionCall call = calls.get(0);
        assertEquals(FunctionExtractor.FunctionCallType.CONSTRUCTOR_CALL, call.getType());
        assertEquals("ArrayList", call.getName());
        assertEquals(0, call.getArity());
    }

    @Test
    public void testExtract_MultipleFunctionCalls() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "a = f1();\n" +
                "b = f2(1);\n" +
                "c = f3(1, 2, 3);",
                operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(3, calls.size());
        assertEquals("f1", calls.get(0).getName());
        assertEquals("f2", calls.get(1).getName());
        assertEquals("f3", calls.get(2).getName());
    }

    @Test
    public void testExtract_NestedFunctionCalls() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("outer(inner(arg))", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(2, calls.size());
        // Note: The visitor visits the parent call before its arguments
        // So outer is visited first, then inner
        assertEquals("outer", calls.get(0).getName());
        assertEquals("inner", calls.get(1).getName());
    }

    @Test
    public void testExtract_FunctionCallInIfStatement() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "if (checkCondition()) {\n" +
                "  doSomething();\n" +
                "}",
                operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(2, calls.size());
        assertEquals("checkCondition", calls.get(0).getName());
        assertEquals("doSomething", calls.get(1).getName());
    }

    @Test
    public void testExtract_FunctionCallInWhileLoop() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "while (hasMore()) {\n" +
                "  process(next());\n" +
                "}",
                operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        // hasMore(), process(), next()
        assertTrue(calls.size() >= 2);
    }

    @Test
    public void testExtract_FunctionCallInForLoop() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "for (int i = 0; i < getCount(); i++) {\n" +
                "  processItem(i);\n" +
                "}",
                operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertTrue(calls.size() >= 1);
        boolean hasGetCount = calls.stream().anyMatch(c -> "getCount".equals(c.getName()));
        boolean hasProcessItem = calls.stream().anyMatch(c -> "processItem".equals(c.getName()));
        assertTrue("Should have getCount call", hasGetCount);
        assertTrue("Should have processItem call", hasProcessItem);
    }

    @Test
    public void testExtract_FunctionCallInLambda() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("x -> x + calculate(x)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        assertEquals("calculate", calls.get(0).getName());
    }

    @Test
    public void testExtract_FunctionCallInTernary() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "isValid() ? getValue() : getDefaultValue()",
                operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(3, calls.size());
        assertEquals("isValid", calls.get(0).getName());
        assertEquals("getValue", calls.get(1).getName());
        assertEquals("getDefaultValue", calls.get(2).getName());
    }

    @Test
    public void testExtract_FunctionCallInTryCatch() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(
                "try {\n" +
                "  doWork();\n" +
                "} catch (Exception e) {\n" +
                "  handleError();\n" +
                "}",
                operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertTrue(calls.size() >= 2);
    }

    @Test
    public void testExtract_NoFunctionCalls() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("a = 1 + 2", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(0, calls.size());
    }

    @Test
    public void testExtract_EmptyProgram() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(0, calls.size());
    }

    @Test
    public void testExtract_FunctionCallWithZeroArgs() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("noArgs()", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        assertEquals(0, calls.get(0).getArity());
    }

    @Test
    public void testExtract_FunctionCallWithManyArgs() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("manyArgs(1, 2, 3, 4, 5)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        assertEquals(5, calls.get(0).getArity());
    }

    @Test
    public void testExtract_FunctionCallLineNumber() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("func()", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        FunctionExtractor.FunctionCall call = calls.get(0);
        assertTrue("Line should be positive", call.getLine() > 0);
        assertTrue("Column should be positive", call.getColumn() > 0);
    }

    @Test
    public void testExtract_ChainedMethodCalls() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("obj.method1().method2()", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(2, calls.size());
        // Note: The visitor does depth-first traversal, so both methods should be found
        assertTrue("Should have method1 or method2", calls.stream().anyMatch(c -> "method1".equals(c.getName()) || "method2".equals(c.getName())));
    }

    @Test
    public void testExtract_ConstructorWithArgs() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("new HashMap(16)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        assertEquals(FunctionExtractor.FunctionCallType.CONSTRUCTOR_CALL, calls.get(0).getType());
        assertEquals("HashMap", calls.get(0).getName());
        assertEquals(1, calls.get(0).getArity());
    }

    @Test
    public void testExtract_FunctionCallToString() throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree("myFunc(42)", operatorManager);
        List<FunctionExtractor.FunctionCall> calls = extractor.extract(program);

        assertEquals(1, calls.size());
        String callString = calls.get(0).toString();
        assertTrue("Should contain function name", callString.contains("myFunc"));
        assertTrue("Should contain arity", callString.contains("arity=1"));
    }
}
