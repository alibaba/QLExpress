package com.ql.util.express.test.issue;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/12/4 上午9:20
 */
public class issue_179 extends IssueBase {
    @Test
    public void test0() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '1006'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test1() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '6%'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test2() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%6'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test3() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%0%6%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test4() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%6%1%'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test5() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%2%6%'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test6() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '1%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test7() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%1'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test8() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '[1006]' like '[1%]'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test9() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'acc' like 'a%c'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test10() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'acdc' like 'a%c'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test11() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'ABE'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test12() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'ABCD'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test13() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'AB%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test14() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'ABC%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test15() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like '%AB'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test16() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'A%B'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test17() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABCD' like 'A%B%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test18() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABCD' like 'A%B%D'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test19() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CCDD' like '%B%D'";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test20() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBD' like '%B%D'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test21() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%B%D'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test22() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test23() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%%%%%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test24() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%%%%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test25() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%E%%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test26() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%E%D'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test27() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%B%E%D%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test28() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%CBE%'";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
}
