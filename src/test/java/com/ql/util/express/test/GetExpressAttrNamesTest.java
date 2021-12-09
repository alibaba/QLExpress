package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class GetExpressAttrNamesTest {

    @Test
    public void testABC() throws Exception {
        String express = "alias qh 100; exportAlias fff qh; int a = b; c = a;macro  惩罚    {100 + 100} 惩罚; qh ;fff;";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getOutVarNames(express);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertTrue("获取外部属性错误", names.length == 2);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("b"));
        Assert.assertTrue("获取外部属性错误", names[1].equalsIgnoreCase("c"));
    }

    @Test
    public void testABCD() throws Exception {
        String express = "if(a!=null)return a";
        ExpressRunner runner = new ExpressRunner(true, true);
        String[] names = runner.getOutVarNames(express);
        runner.execute(express, new DefaultContext<>(), null, false, false);
        for (String s : names) {
            System.out.println("var : " + s);
        }
        Assert.assertTrue("获取外部属性错误", names.length == 1);
        Assert.assertTrue("获取外部属性错误", names[0].equalsIgnoreCase("a"));
    }
}
