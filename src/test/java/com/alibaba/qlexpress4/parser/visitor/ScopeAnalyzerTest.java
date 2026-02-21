package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.SyntaxTreeFactory;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for ScopeAnalyzer.
 *
 * @author QLExpress Team
 */
public class ScopeAnalyzerTest {
    
    private final OperatorManager operatorManager = new OperatorManager();
    
    private final ScopeAnalyzer analyzer = new ScopeAnalyzer();
    
    /**
     * Helper method to parse a script and analyze scopes.
     */
    private ScopeAnalyzer.Context analyze(String script)
        throws Exception {
        ProgramNode program = SyntaxTreeFactory.buildTree(script, operatorManager);
        return analyzer.analyze(program);
    }
    
    @Test
    public void testEmptyProgram()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("");
        assertNotNull(context.getRootScope());
        assertEquals(ScopeAnalyzer.ScopeType.PROGRAM, context.getRootScope().getType());
        assertEquals(0, context.getRootScope().getVariables().size());
        assertEquals(0, context.getRootScope().getChildren().size());
        assertTrue(context.getAllShadowedVariables().isEmpty());
    }
    
    @Test
    public void testSingleVariableDeclaration()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 10;");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getVariables().size());
        assertTrue(root.containsVariable("x"));
        assertNotNull(root.findVariable("x"));
        assertEquals("x", root.findVariable("x").getName());
        assertEquals("int", root.findVariable("x").getTypeName());
    }
    
    @Test
    public void testMultipleVariableDeclarations()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 10; int y = 20; int z = 30;");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(3, root.getVariables().size());
        assertTrue(root.containsVariable("x"));
        assertTrue(root.containsVariable("y"));
        assertTrue(root.containsVariable("z"));
    }
    
    @Test
    public void testBlockScope()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("{ int x = 10; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(0, root.getVariables().size());
        assertEquals(1, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.BLOCK, root.getChildren().get(0).getType());
        assertEquals(1, root.getChildren().get(0).getVariables().size());
        assertTrue(root.getChildren().get(0).containsVariable("x"));
    }
    
    @Test
    public void testNestedBlockScopes()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("{ int x = 10; { int y = 20; } }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        ScopeAnalyzer.Scope outerBlock = root.getChildren().get(0);
        assertEquals(1, outerBlock.getVariables().size());
        assertEquals(1, outerBlock.getChildren().size());
        ScopeAnalyzer.Scope innerBlock = outerBlock.getChildren().get(0);
        assertEquals(1, innerBlock.getVariables().size());
        assertTrue(innerBlock.containsVariable("y"));
    }
    
    @Test
    public void testVariableShadowingInBlock()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 10; { int x = 20; }");
        assertEquals(1, context.getAllShadowedVariables().size());
        ScopeAnalyzer.ShadowedVariable shadowed = context.getAllShadowedVariables().get(0);
        assertEquals("x", shadowed.getName());
        assertEquals(0, shadowed.getShadowed().getDeclaredIn().getDepth()); // root scope depth is 0
        assertEquals(1, shadowed.getShadowing().getDeclaredIn().getDepth()); // block scope depth is 1
    }
    
    @Test
    public void testVariableShadowingInNestedBlocks()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("{ int x = 10; { int x = 20; { int x = 30; } } }");
        List<ScopeAnalyzer.ShadowedVariable> shadowed = context.getAllShadowedVariables();
        // x in middle block shadows x in outer block
        // x in innermost block shadows x in middle block
        assertTrue(shadowed.size() >= 2);
    }
    
    @Test
    public void testIfStatement()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("if (true) { int x = 10; } else { int y = 20; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(2, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.BLOCK, root.getChildren().get(0).getType());
        assertEquals(ScopeAnalyzer.ScopeType.BLOCK, root.getChildren().get(1).getType());
        assertTrue(root.getChildren().get(0).containsVariable("x"));
        assertTrue(root.getChildren().get(1).containsVariable("y"));
    }
    
    @Test
    public void testWhileLoop()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("while (true) { int x = 10; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.BLOCK, root.getChildren().get(0).getType());
        assertTrue(root.getChildren().get(0).containsVariable("x"));
    }
    
    @Test
    public void testForLoopTraditional()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("for (int i = 0; i < 10; i++) { int x = i; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.FOR_LOOP, root.getChildren().get(0).getType());
        assertTrue(root.getChildren().get(0).containsVariable("i"));
    }
    
    @Test
    public void testForEachLoop()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("for (int x : list) { int y = x; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.FOR_LOOP, root.getChildren().get(0).getType());
        assertTrue(root.getChildren().get(0).containsVariable("x"));
    }
    
    @Test
    public void testSwitchStatement()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("switch (x) { case 1: int a = 1; break; case 2: int b = 2; break; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(2, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.SWITCH_CASE, root.getChildren().get(0).getType());
        assertEquals(ScopeAnalyzer.ScopeType.SWITCH_CASE, root.getChildren().get(1).getType());
    }
    
    @Test
    public void testTryCatchFinally()
        throws Exception {
        ScopeAnalyzer.Context context =
            analyze("try { int x = 10; } catch (Exception e) { int y = 20; } finally { int z = 30; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        // The try block is one child, each catch clause is one child, finally is another child
        assertTrue(root.getChildren().size() >= 3);
        assertTrue(root.getChildren().get(0).getType() == ScopeAnalyzer.ScopeType.BLOCK); // try block
        assertTrue(root.getChildren().get(1).getType() == ScopeAnalyzer.ScopeType.CATCH_CLAUSE); // catch clause
        assertTrue(root.getChildren().get(1).containsVariable("e"));
    }
    
    @Test
    public void testCatchClauseExceptionVariable()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("try { } catch (IOException e) { }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(2, root.getChildren().size()); // try block and catch clause
        ScopeAnalyzer.Scope catchScope = root.getChildren().get(1);
        assertEquals(ScopeAnalyzer.ScopeType.CATCH_CLAUSE, catchScope.getType());
        assertTrue(catchScope.containsVariable("e"));
        assertEquals("IOException", catchScope.findVariable("e").getTypeName());
    }
    
    @Test
    public void testLambdaExpression()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("list.forEach((x, y) -> x + y);");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        assertEquals(ScopeAnalyzer.ScopeType.LAMBDA, root.getChildren().get(0).getType());
        assertTrue(root.getChildren().get(0).containsVariable("x"));
        assertTrue(root.getChildren().get(0).containsVariable("y"));
    }
    
    @Test
    public void testLambdaWithTypedParameters()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("list.forEach((int x, int y) -> x + y);");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        ScopeAnalyzer.Scope lambdaScope = root.getChildren().get(0);
        assertEquals("int", lambdaScope.findVariable("x").getTypeName());
        assertEquals("int", lambdaScope.findVariable("y").getTypeName());
    }
    
    @Test
    public void testLambdaParameterShadowing()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 10; list.apply(x -> x + 1);");
        assertEquals(1, context.getAllShadowedVariables().size());
        ScopeAnalyzer.ShadowedVariable shadowed = context.getAllShadowedVariables().get(0);
        assertEquals("x", shadowed.getName());
    }
    
    @Test
    public void testMultipleCatchClauses()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("try { } catch (IOException e) { } catch (Exception e) { }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        // try block + 2 catch clauses = 3 children
        assertEquals(3, root.getChildren().size());
        assertTrue(root.getChildren().get(1).containsVariable("e"));
        assertTrue(root.getChildren().get(2).containsVariable("e"));
        // Two catch clauses can have the same exception variable name (different scopes)
    }
    
    @Test
    public void testVariableVisibilityAcrossScopes()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 10; { int y = x; } { int z = x; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertTrue(root.containsVariable("x"));
        // Inner blocks can see x from parent
        assertNotNull(root.getChildren().get(0).findVariable("x"));
        assertNotNull(root.getChildren().get(1).findVariable("x"));
        // But x is not in their local variables
        assertFalse(root.getChildren().get(0).containsVariable("x"));
        assertFalse(root.getChildren().get(1).containsVariable("x"));
    }
    
    @Test
    public void testScopeDepth()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("{ { { int x = 10; } } }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(0, root.getDepth());
        assertEquals(1, root.getChildren().get(0).getDepth());
        assertEquals(2, root.getChildren().get(0).getChildren().get(0).getDepth());
        assertEquals(3, root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getDepth());
    }
    
    @Test
    public void testFindAllShadowedVariables()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 1; { int x = 2; { int y = 3; { int x = 4; } } }");
        List<ScopeAnalyzer.ShadowedVariable> shadowed = context.findAllShadowedVariables();
        assertTrue(shadowed.size() >= 2);
        // Check that at least one is 'x'
        boolean foundX = false;
        for (ScopeAnalyzer.ShadowedVariable sv : shadowed) {
            if (sv.getName().equals("x")) {
                foundX = true;
                break;
            }
        }
        assertTrue(foundX);
    }
    
    @Test
    public void testNoShadowingWithDifferentNames()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 10; { int y = 20; { int z = 30; } }");
        assertTrue(context.getAllShadowedVariables().isEmpty());
        assertTrue(context.findAllShadowedVariables().isEmpty());
    }
    
    @Test
    public void testLambdaBlockBody()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("list.apply((x) -> { int y = x + 1; return y; });");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        ScopeAnalyzer.Scope lambdaScope = root.getChildren().get(0);
        assertEquals(ScopeAnalyzer.ScopeType.LAMBDA, lambdaScope.getType());
        assertTrue(lambdaScope.containsVariable("x"));
        assertEquals(1, lambdaScope.getChildren().size());
        assertTrue(lambdaScope.getChildren().get(0).containsVariable("y"));
    }
    
    @Test
    public void testForLoopWithBlock()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("for (int i = 0; i < 10; i++) { int j = i * 2; }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        ScopeAnalyzer.Scope forScope = root.getChildren().get(0);
        assertEquals(ScopeAnalyzer.ScopeType.FOR_LOOP, forScope.getType());
        assertTrue(forScope.containsVariable("i"));
        assertEquals(1, forScope.getChildren().size());
        assertTrue(forScope.getChildren().get(0).containsVariable("j"));
    }
    
    @Test
    public void testComplexNestedScopes()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int x = 1;" + "if (true) { int y = 2; while (true) { int z = 3; } }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertTrue(root.containsVariable("x"));
        assertEquals(1, root.getChildren().size());
        ScopeAnalyzer.Scope ifBlock = root.getChildren().get(0);
        assertTrue(ifBlock.containsVariable("y"));
        assertEquals(1, ifBlock.getChildren().size());
        ScopeAnalyzer.Scope whileBlock = ifBlock.getChildren().get(0);
        assertTrue(whileBlock.containsVariable("z"));
    }
    
    @Test
    public void testParentScopeLinkage()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("{ { int x = 10; } }");
        ScopeAnalyzer.Scope root = context.getRootScope();
        ScopeAnalyzer.Scope outerBlock = root.getChildren().get(0);
        ScopeAnalyzer.Scope innerBlock = outerBlock.getChildren().get(0);
        assertSame(root, outerBlock.getParent());
        assertSame(outerBlock, innerBlock.getParent());
        // Inner block can find x in its own scope
        assertNotNull(innerBlock.findVariable("x"));
        // Outer block cannot find x (it's in inner)
        assertNull(outerBlock.getVariables().get("x"));
    }
    
    @Test
    public void testEmptyBlock()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("{}");
        ScopeAnalyzer.Scope root = context.getRootScope();
        assertEquals(1, root.getChildren().size());
        assertEquals(0, root.getChildren().get(0).getVariables().size());
    }
    
    @Test
    public void testVariableInfoDetails()
        throws Exception {
        ScopeAnalyzer.Context context = analyze("int count = 42;");
        ScopeAnalyzer.Scope root = context.getRootScope();
        ScopeAnalyzer.VariableInfo var = root.findVariable("count");
        assertEquals("count", var.getName());
        assertEquals("int", var.getTypeName());
        assertSame(root, var.getDeclaredIn());
    }
}
