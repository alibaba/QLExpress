package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.operator.Operator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import org.junit.Test;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OperatorLimitTest {
    @Test
    public void testCheckWithAllowedOperators() throws QLSyntaxException {
        Set<Operator> allowedOps = new HashSet<>(Arrays.asList(PlusOperator.getInstance(), MultiplyOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().allowedOperators(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c", checkOptions);
    }

    @Test
    public void testCheckWithDisallowedOperators() {
        Set<Operator> allowedOps = new HashSet<>(Arrays.asList(PlusOperator.getInstance(), MultiplyOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().allowedOperators(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            runner.check("a = b + c", checkOptions);
            fail("应该抛出 QLSyntaxException");
        } catch (QLSyntaxException e) {
            assertTrue(e.getMessage().contains("="));
            assertTrue(e.getMessage().contains("不允许的运算符"));
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
        }
    }

    @Test
    public void testCheckWithForbiddenOperators() throws QLSyntaxException {
        Set<Operator> forbiddenOps = new HashSet<>(Arrays.asList(AssignOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().forbiddenOperators(forbiddenOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c - d / e", checkOptions);
    }

    @Test
    public void testCheckWithForbiddenOperatorUsed() {
        Set<Operator> forbiddenOps = new HashSet<>(Arrays.asList(AssignOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().forbiddenOperators(forbiddenOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            runner.check("a = b + c", checkOptions);
            fail("应该抛出 QLSyntaxException");
        } catch (QLSyntaxException e) {
            assertTrue(e.getMessage().contains("="));
            assertTrue(e.getMessage().contains("被禁止的运算符"));
            assertEquals("OPERATOR_FORBIDDEN", e.getErrorCode());
        }
    }

    @Test
    public void testCheckWithoutLimit() throws QLSyntaxException {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a = b + c - d * e / f");
        runner.check("a++");
        runner.check("++a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotSetBothWhitelistAndBlacklist() {
        Set<Operator> allowedOps = new HashSet<>(Arrays.asList(PlusOperator.getInstance()));
        Set<Operator> forbiddenOps = new HashSet<>(Arrays.asList(AssignOperator.getInstance()));
        CheckOptions.builder().allowedOperators(allowedOps).forbiddenOperators(forbiddenOps).build();
    }
}
