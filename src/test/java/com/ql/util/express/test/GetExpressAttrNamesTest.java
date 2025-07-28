package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class GetExpressAttrNamesTest {
    @Test
    public void testABC() throws Exception {
        String express = ""
            + "alias qh 100;"
            + "exportAlias fff qh;"
            + "int a = b;"
            + "c = a;"
            + "macro 惩罚 {100 + 100};"
            + "惩罚;"
            + "qh;"
            + "fff;";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getOutVarNames(express);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertEquals("获取外部属性错误", 2, names.length);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("b"));
        Assert.assertTrue("获取外部属性错误", names[1].equalsIgnoreCase("c"));
    }

    @Test
    public void testABCD() throws Exception {
        String express = "if(a!=null) return a;";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getOutVarNames(express);
        runner.execute(express, new DefaultContext<>(), null, false, false);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertEquals("获取外部属性错误", 1, names.length);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("a"));
    }

    @Test
    public void testFullA() throws Exception {
        String express = "if(a!=null) return a;";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getFullOutVarNames(express);
        runner.execute(express, new DefaultContext<>(), null, false, false);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertEquals("获取外部属性错误", 1, names.length);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("a"));
    }

    @Test
    public void testFullABCD() throws Exception {
        String express = "if(a.b.c.d!=null) return a.b.c.d;";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getFullOutVarNames(express);
        // runner.execute(express, new DefaultContext<>(), null, false, false);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertEquals("获取外部属性错误", 1, names.length);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("a.b.c.d"));
    }

    @Test
    public void testFunctionFullABCD() throws Exception {
        String express = "if(func(a.b.c.d)!=null) return a.b.c.d;";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getFullOutVarNames(express);
        // runner.execute(express, new DefaultContext<>(), null, false, false);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertEquals("获取外部属性错误", 1, names.length);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("a.b.c.d"));
    }
}
