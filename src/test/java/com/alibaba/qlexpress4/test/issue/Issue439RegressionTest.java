package com.alibaba.qlexpress4.test.issue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.aparser.InterpolationMode;
import com.alibaba.qlexpress4.aparser.MockOpM;
import com.alibaba.qlexpress4.aparser.QLexer;
import com.alibaba.qlexpress4.aparser.Token;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import org.junit.Assert;
import org.junit.Test;

/**
 * Regression test for issue #439.
 *
 * Issue: In v4, the expression `if(oldObj!=null&&!oldObj.getLong("status").equals(5L))`
 * fails to parse because `&&!` (&& immediately followed by !, with no whitespace)
 * was not correctly tokenized by the old ANTLR4-based parser.
 *
 * The fix replaced the ANTLR4 parser with a hand-written Lexer that correctly
 * separates `&&` (OPID) from `!` (BANG) even when they are adjacent.
 */
public class Issue439RegressionTest {

    private static final Express4Runner RUNNER =
        new Express4Runner(InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).build());

    // ==================== Lexer-level tokenization tests ====================

    /**
     * `&&!` should be tokenized as OPID("&&") + BANG("!")
     */
    @Test
    public void lexerShouldSplitAndBangIntoOpidAndBang() {
        List<Token> tokens = tokenize("a&&!b");
        // a (ID) && (OPID) ! (BANG) b (ID) EOF
        Token andToken = findTokenByType(tokens, QLexer.OPID);
        Assert.assertNotNull("expected OPID token for &&", andToken);
        Assert.assertEquals("&&", andToken.getText());

        Token bangToken = findTokenByType(tokens, QLexer.BANG);
        Assert.assertNotNull("expected BANG token for !", bangToken);
        Assert.assertEquals("!", bangToken.getText());
    }

    /**
     * `||!` should be tokenized as OPID("||") + BANG("!")
     */
    @Test
    public void lexerShouldSplitOrBangIntoOpidAndBang() {
        List<Token> tokens = tokenize("a||!b");
        Token orToken = findTokenByType(tokens, QLexer.OPID);
        Assert.assertNotNull("expected OPID token for ||", orToken);
        Assert.assertEquals("||", orToken.getText());

        Token bangToken = findTokenByType(tokens, QLexer.BANG);
        Assert.assertNotNull("expected BANG token for !", bangToken);
        Assert.assertEquals("!", bangToken.getText());
    }

    /**
     * `==!` should be tokenized as OPID("==") + BANG("!")
     */
    @Test
    public void lexerShouldSplitEqBangIntoOpidAndBang() {
        List<Token> tokens = tokenize("a==!b");
        Token eqToken = findTokenByType(tokens, QLexer.OPID);
        Assert.assertNotNull("expected OPID token for ==", eqToken);
        Assert.assertEquals("==", eqToken.getText());

        Token bangToken = findTokenByType(tokens, QLexer.BANG);
        Assert.assertNotNull("expected BANG token for !", bangToken);
        Assert.assertEquals("!", bangToken.getText());
    }

    // ==================== Parser / Execution tests ====================

    /**
     * `a && !b` with spaces - baseline case that should always work
     */
    @Test
    public void executeLogicalAndNotWithSpaces() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("a", true);
        ctx.put("b", false);
        Object result = run("a && !b", ctx);
        Assert.assertEquals(true, result);
    }

    /**
     * `a&&!b` without spaces - core regression case for issue #439
     */
    @Test
    public void executeLogicalAndNotWithoutSpaces() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("a", true);
        ctx.put("b", false);
        Object result = run("a&&!b", ctx);
        Assert.assertEquals(true, result);
    }

    /**
     * `if(x!=null&&!y){return 1}` - typical pattern from issue report
     */
    @Test
    public void executeIfNotNullAndNotWithNoSpaces() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("x", "something");
        ctx.put("y", false);
        Object result = run("if(x!=null&&!y){return 1}", ctx);
        Assert.assertEquals(1, result);
    }

    /**
     * `a && !b && !c` - nested logical AND with multiple NOT operators
     */
    @Test
    public void executeNestedAndNotWithoutSpaces() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("a", true);
        ctx.put("b", false);
        ctx.put("c", false);
        Object result = run("a&&!b&&!c", ctx);
        Assert.assertEquals(true, result);
    }

    /**
     * `oldObj!=null&&!oldObj.equals("test")` - complex real-world pattern from issue
     */
    @Test
    public void executeComplexNotNullAndNotEqualsWithoutSpaces() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("oldObj", "hello");
        Object result = run("oldObj!=null&&!oldObj.equals(\"test\")", ctx);
        Assert.assertEquals(true, result);
    }

    /**
     * `oldObj!=null&&!oldObj.equals("test")` when oldObj is null - short circuit
     */
    @Test
    public void executeComplexNotNullShortCircuitWhenNull() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("oldObj", null);
        Object result = run("oldObj!=null&&!oldObj.equals(\"test\")", ctx);
        Assert.assertEquals(false, result);
    }

    /**
     * `||!` execution: `a||!b` without spaces
     */
    @Test
    public void executeLogicalOrNotWithoutSpaces() {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("a", false);
        ctx.put("b", false);
        Object result = run("a||!b", ctx);
        Assert.assertEquals(true, result);
    }

    /**
     * Full original pattern from issue #439
     */
    @Test
    public void executeOriginalIssuePattern() {
        Map<String, Object> ctx = new HashMap<>();
        Map<String, Object> oldObj = new HashMap<>();
        oldObj.put("status", 3L);
        ctx.put("oldObj", oldObj);
        // Simulates: if(oldObj!=null&&!oldObj.get("status").equals(5L)){return true}
        Object result = run(
            "if(oldObj!=null&&!oldObj.get(\"status\").equals(5L)){return true}",
            ctx);
        Assert.assertEquals(true, result);
    }

    // ==================== Helper methods ====================

    private List<Token> tokenize(String script) {
        return QLexer.tokenize(script, new MockOpM(), InterpolationMode.VARIABLE, "${", "}", true);
    }

    private Token findTokenByType(List<Token> tokens, int type) {
        for (Token token : tokens) {
            if (token.getType() == type) {
                return token;
            }
        }
        return null;
    }

    private Object run(String script, Map<String, Object> context) {
        return RUNNER.execute(script, context, QLOptions.DEFAULT_OPTIONS).getResult();
    }
}
