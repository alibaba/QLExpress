package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class VarAreaTest {
    @Test
    public void testVarArea1() throws Exception {
        String express =
            " qh = 1; " +
                "如果 ( false)  则 {" +
                "  3 + (3) + (4 + 1)" +
                " }否则{" +
                " qh = 3;" +
                " qh = qh + 100;" +
                "}; " +
                "qh = qh + 1;";
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("则", "then", null);
        runner.addOperatorWithAlias("否则", "else", null);
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(r);
        System.out.println(context);
        Assert.assertEquals("变量定义作用域错误", "104", context.get("qh").toString());

    }

    @Test
    public void testVarArea2() throws Exception {
        String express =
            " qh = 1; " +
                "如果 ( false)  则 {" +
                "  3 + (3) + (4 + 1)" +
                " }否则{" +
                " int qh = 3;" +
                " qh = qh + 100;" +
                "}; " +
                "qh = qh + 1;";
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("则", "then", null);
        runner.addOperatorWithAlias("否则", "else", null);
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(r);
        System.out.println(context);
        Assert.assertEquals("变量定义作用域错误", "2", context.get("qh").toString());

    }
}
