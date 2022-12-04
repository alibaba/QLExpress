package com.ql.util.express.issue;

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
}
