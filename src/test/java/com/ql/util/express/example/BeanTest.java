package com.ql.util.express.example;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class BeanTest {
    @Test
    public void test1() throws Exception {
        String exp = ""
            + "import com.ql.util.express.example.CustBean;"
            + "CustBean cust = new CustBean(1);"
            + "cust.setName(\"小强\");"
            + "return cust.getName();";
        ExpressRunner runner = new ExpressRunner();
        String r = (String)runner.execute(exp, null, null, false, false);
        System.out.println(r);
        Assert.assertEquals("操作符执行错误", "小强", r);
    }

    @Test
    public void test2() throws Exception {
        String exp = ""
            + "cust.setName(\"小强\");"
            + "return cust.getName();";
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("cust", new CustBean(1));
        ExpressRunner runner = new ExpressRunner();
        String r = (String)runner.execute(exp, expressContext, null, false, false);
        System.out.println(r);
        Assert.assertEquals("操作符执行错误", "小强", r);
    }

    @Test
    public void test3() throws Exception {
        String exp = "首字母大写(\"abcd\")";
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfClassMethod("首字母大写", CustBean.class.getName(), "firstToUpper", new String[] {"String"},
            null);
        String r = (String)runner.execute(exp, null, null, false, false);
        System.out.println(r);
        Assert.assertEquals("操作符执行错误", "Abcd", r);
    }

    /**
     * 使用别名
     *
     * @throws Exception
     */
    @Test
    public void testAlias() throws Exception {
        String exp = ""
            + "cust.setName(\"小强\");"
            + "定义别名 custName cust.name;"
            + "return custName;";
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("cust", new CustBean(1));
        ExpressRunner runner = new ExpressRunner();

        runner.addOperatorWithAlias("定义别名", "alias", null);
        String r = (String)runner.execute(exp, expressContext, null, false, false);
        System.out.println(r);
        Assert.assertEquals("操作符执行错误", "小强", r);
    }

    /**
     * 使用宏
     *
     * @throws Exception
     */
    @Test
    public void testMacro() throws Exception {
        String exp = ""
            + "cust.setName(\"小强\");"
            + "定义宏 custName {cust.name};"
            + "return custName;";
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        expressContext.put("cust", new CustBean(1));
        ExpressRunner runner = new ExpressRunner();

        runner.addOperatorWithAlias("定义宏", "macro", null);
        String r = (String)runner.execute(exp, expressContext, null, false, false);
        System.out.println(r);
        Assert.assertEquals("操作符执行错误", "小强", r);
    }
}
