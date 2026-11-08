package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Regression tests for issue #323:
 * V4 only registers lowercase "or" and "and" as word operators.
 * Uppercase "OR" and "AND" (which worked in V3) are treated as
 * function names, causing expressions like "OR(1<2, 2>3)" to fail.
 *
 * Root cause: OperatorManager static block only registers lowercase
 * LogicOrOperator("or") and LogicAndOperator("and"), missing the
 * uppercase variants that V3 supported.
 */
public class Issue323Test {

    private final Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

    @Test
    public void uppercaseOR_infixExpression() {
        Object result = runner.execute("1 < 2 OR 2 > 3", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void uppercaseOR_bothFalse() {
        Object result = runner.execute("1 > 2 OR 3 > 4", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(false, result);
    }

    @Test
    public void uppercaseAND_infixExpression() {
        Object result = runner.execute("1 < 2 AND 3 > 2", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void uppercaseAND_oneFalse() {
        Object result = runner.execute("1 < 2 AND 3 > 4", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(false, result);
    }

    @Test
    public void uppercaseOR_withVariables() {
        HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("a", true);
        ctx.put("b", false);
        Object result = runner.execute("a OR b", ctx, QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void uppercaseAND_withVariables() {
        HashMap<String, Object> ctx = new HashMap<>();
        ctx.put("a", true);
        ctx.put("b", true);
        Object result = runner.execute("a AND b", ctx, QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void mixedCase_uppercaseOR_lowercaseAnd() {
        Object result = runner.execute("1 < 2 OR 2 < 3 and 3 < 4", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void lowercaseOr_stillWorks() {
        Object result = runner.execute("1 < 2 or 2 > 3", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void lowercaseAnd_stillWorks() {
        Object result = runner.execute("1 < 2 and 3 > 2", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void symbolicOr_stillWorks() {
        Object result = runner.execute("1 < 2 || 2 > 3", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void symbolicAnd_stillWorks() {
        Object result = runner.execute("1 < 2 && 3 > 2", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(true, result);
    }

    @Test
    public void syntaxCheck_uppercaseOR() {
        runner.check("1 < 2 OR 2 > 3");
    }

    @Test
    public void syntaxCheck_uppercaseAND() {
        runner.check("1 < 2 AND 3 > 2");
    }

    @Test
    public void combined_uppercaseORAndAND() {
        Object result = runner.execute(
            "1 > 2 AND 3 > 4 OR 5 > 4",
            new HashMap<>(),
            QLOptions.DEFAULT_OPTIONS
        ).getResult();
        assertEquals(true, result);
    }
}
