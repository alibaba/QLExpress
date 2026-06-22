package com.alibaba.qlexpress4.test.issue;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Regression tests for issue #409: OperatorLike.matchPattern backtracking bug.
 *
 * <p>In v3 (3.3.4), {@code OperatorLike.matchPattern} used a greedy segment-matching algorithm
 * <b>without backtracking</b>. When a {@code %} wildcard consumed characters and the next literal
 * segment failed to match at the greedy position, v3 would immediately return {@code false}
 * instead of backtracking.</p>
 *
 * <p>Example: {@code "abc" like "a%c"} — v3 returned {@code false} because after matching "a"
 * and letting {@code %} consume "b", it tried to find "c" at index 2 (past the string end)
 * instead of backtracking to let {@code %} consume "b" and match "c" at the correct position.</p>
 *
 * <p>v4 fixed this by replacing the greedy algorithm with a backtracking matcher that uses
 * sRecall/pRecall pointers in {@code BaseBinaryOperator.matchPattern}.</p>
 *
 * @see <a href="https://github.com/alibaba/QLExpress/issues/409">Issue #409</a>
 */
public class Issue409RegressionTest {

    private Express4Runner runner;
    private Map<String, Object> context;
    private QLOptions options;

    @Before
    public void setUp() {
        runner = new Express4Runner(InitOptions.builder().build());
        context = new HashMap<>();
        options = QLOptions.builder().build();
    }

    // ---------------------------------------------------------------
    // Core regression: the v3 bug case
    // ---------------------------------------------------------------

    /**
     * Core v3 bug case: "abc" like "a%c" must return true.
     * v3 returned false because greedy matching consumed 'b' into '%'
     * then failed to find 'c' without backtracking.
     */
    @Test
    public void testBacktrackingBasicCase() {
        assertLikeTrue("\"abc\" like \"a%c\"");
    }

    @Test
    public void testBacktrackingViaVariable() {
        context.put("s", "abc");
        context.put("p", "a%c");
        assertLikeTrue("s like p");
    }

    // ---------------------------------------------------------------
    // Multi-segment backtracking
    // ---------------------------------------------------------------

    @Test
    public void testBacktrackingMultipleWildcards() {
        assertLikeTrue("\"aXbYc\" like \"a%b%c\"");
        assertLikeTrue("\"aab\" like \"a%a%b\"");
        assertLikeTrue("\"aaab\" like \"a%a%b\"");
        assertLikeTrue("\"hello_world_test\" like \"hello%world%test\"");
    }

    @Test
    public void testBacktrackingWithRepeatedPrefix() {
        // Pattern "a%a" on "aa" — '%' matches empty, second 'a' matches second 'a'
        assertLikeTrue("\"aa\" like \"a%a\"");
        // Pattern "a%a" on "aXa" — '%' matches "X"
        assertLikeTrue("\"aXa\" like \"a%a\"");
        // Pattern "a%a" on "aXXa" — '%' matches "XX"
        assertLikeTrue("\"aXXa\" like \"a%a\"");
    }

    // ---------------------------------------------------------------
    // Trailing/leading % edge cases (v3 issue #409 context)
    // ---------------------------------------------------------------

    @Test
    public void testTrailingPercent() {
        // From v3 issue report: "1%1" like "1%" should be true
        assertLikeTrue("\"1%1\" like \"1%\"");
        assertLikeTrue("\"anything\" like \"%\"");
        assertLikeTrue("\"test\" like \"t%\"");
        assertLikeTrue("\"1006\" like \"1%\"");
    }

    @Test
    public void testLeadingPercent() {
        assertLikeTrue("\"1006\" like \"%6\"");
        assertLikeTrue("\"test\" like \"%t\"");
        assertLikeTrue("\"hello\" like \"%lo\"");
    }

    // ---------------------------------------------------------------
    // Empty string and wildcard-only patterns
    // ---------------------------------------------------------------

    @Test
    public void testEmptyStringAndWildcards() {
        assertLikeTrue("\"\" like \"%\"");
        assertLikeTrue("\"\" like \"%%\"");
        assertLikeTrue("\"\" like \"\"");
        assertLikeFalse("\"a\" like \"\"");
        assertLikeFalse("\"\" like \"a\"");
    }

    // ---------------------------------------------------------------
    // Negative cases (should return false)
    // ---------------------------------------------------------------

    @Test
    public void testNegativeCases() {
        assertLikeFalse("\"abc\" like \"a%d\"");
        assertLikeFalse("\"abc\" like \"%x%\"");
        assertLikeFalse("\"abc\" like \"a%b%d\"");
        assertLikeFalse("\"hello\" like \"h%x\"");
    }

    // ---------------------------------------------------------------
    // not_like operator (inverse)
    // ---------------------------------------------------------------

    @Test
    public void testNotLikeInverse() {
        // not_like should be the exact inverse of like
        assertLikeFalse("\"abc\" not_like \"a%c\"");
        assertLikeTrue("\"abc\" not_like \"a%d\"");
        assertLikeFalse("\"abc\" not_like \"a%b%c\"");
    }

    // ---------------------------------------------------------------
    // Null handling
    // ---------------------------------------------------------------

    @Test
    public void testNullHandling() {
        assertLikeTrue("null like null");
        assertLikeFalse("\"a\" like null");
        assertLikeFalse("null like \"a\"");
    }

    // ---------------------------------------------------------------
    // Complex expressions mixing LIKE with other operators
    // ---------------------------------------------------------------

    @Test
    public void testLikeInConditionalExpression() {
        context.put("name", "HelloWorld");
        QLResult result = runner.execute(
            "if (name like \"Hello%\") { \"match\" } else { \"no_match\" }",
            context, options);
        Assert.assertEquals("match", result.getResult());
    }

    @Test
    public void testLikeCombinedWithLogicalOperators() {
        context.put("s", "abc123");
        QLResult result = runner.execute(
            "s like \"abc%\" && s like \"%123\"",
            context, options);
        Assert.assertEquals(true, result.getResult());
    }

    // ---------------------------------------------------------------
    // Helper methods
    // ---------------------------------------------------------------

    private void assertLikeTrue(String expression) {
        QLResult result = runner.execute(expression, context, options);
        Assert.assertTrue(
            "Expected TRUE for: " + expression + ", but got: " + result.getResult(),
            (Boolean) result.getResult());
    }

    private void assertLikeFalse(String expression) {
        QLResult result = runner.execute(expression, context, options);
        Assert.assertFalse(
            "Expected FALSE for: " + expression + ", but got: " + result.getResult(),
            (Boolean) result.getResult());
    }
}
