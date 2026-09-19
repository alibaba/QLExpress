package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Regression tests for issue #473:
 * V4 fails to compile expressions like "a=-1L" where assignment operator '='
 * is immediately followed by unary minus without spaces.
 *
 * Root cause: QLexer.isCustomOperatorStart() included '=', causing the lexer
 * to greedily consume "=-" as a single custom operator token (OPID) instead of
 * separate EQ and SUB tokens.
 */
public class Issue473Test {

    private final Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

    @Test
    public void assignNegativeLong_noSpaces() {
        // The exact case from the issue: a=-1L
        Object result = runner.execute("a=-1L; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(-1L, result);
    }

    @Test
    public void assignNegativeInt_noSpaces() {
        Object result = runner.execute("a=-1; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(-1, result);
    }

    @Test
    public void assignNegativeDouble_noSpaces() {
        Object result = runner.execute("a=-3.14; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(-3.14, (double) result, 0.001);
    }

    @Test
    public void assignNegativeBigDecimal_noSpaces() {
        Object result = runner.execute("a=-1.5; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertTrue(result instanceof BigDecimal);
        assertEquals(new BigDecimal("-1.5"), result);
    }

    @Test
    public void assignPositive_noSpaces() {
        // a=+1 should also work (same root cause)
        Object result = runner.execute("a=+1; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(1, result);
    }

    @Test
    public void assignWithSpaces_stillWorks() {
        // Verify that spaced assignment still works (should not regress)
        Object result = runner.execute("a = -1L; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(-1L, result);
    }

    @Test
    public void compoundSubAssign_stillWorks() {
        // Verify -= still works correctly
        Object result = runner.execute("a=10; a-=3; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(7, result);
    }

    @Test
    public void compoundAddAssign_stillWorks() {
        Object result = runner.execute("a=10; a+=3; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(13, result);
    }

    @Test
    public void equalityOperator_stillWorks() {
        // Verify == still works
        Object result = runner.execute("a=1; a==1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void assignNegatedBoolean_noSpaces() {
        // a=!false should work (same root cause as =-)
        Object result = runner.execute("a=!false; a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void multipleAssignments_noSpaces() {
        Object result = runner.execute("a=-1; b=-2; a+b;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(-3, result);
    }
}
