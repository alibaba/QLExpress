package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class AClassDefineTest {
    @Test
    public void testNewVClass() throws Exception {
        String express = "" +
            "int a = 100;" +
            "class ABC(){" +
            " int a = 200;" +
            " println a;" +
            "}" +
            "ABC  abc = new ABC();" +
            "println a + abc.a;" +
            "return a + abc.a;";
        ExpressRunner runner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.loadMultiExpress("ClassTest", express);

        Object r = runner.executeByExpressName("ClassTest", context,
            null, false, false, null);
        Assert.assertFalse("VClass的作用域错误", r.toString().equalsIgnoreCase("300"));
    }

    @Test
    public void testABC() throws Exception {
        String expressDefine =
            "class ABC(com.ql.util.express.test.BeanExample bean,String name){"
                + "姓名= name;"
                + "计数器 = new InnerClass();"
                + "整数值:bean.intValue;"
                + "浮点值:bean.doubleValue;"
                + "哈希值:{bean.hashCode();};"
                + "function add(int a,int b){return a + b + 整数值 + 计数器.计数;};"
                + "class InnerClass(){" +
                "int 计数 =200;" +
                "};"
                + "};";
        String express =
            "ABC example = new ABC(new com.ql.util.express.test.BeanExample(),'xuannan');"
                + "ABC example2 = new ABC(new com.ql.util.express.test.BeanExample(),'xuanyu');"
                + " example.整数值 =100;"
                + " example2.整数值 =200;"
                + " example.浮点值= 99.99;"
                + " example2.浮点值= 11.11;"
                + " example.浮点值 = example.浮点值 + example.整数值;"
                + " result = example.add(10,20) +'--'+ example2.add(10,20) +'--'+  example.姓名 +'--'+ example2.姓名 "
                + "+'--'+ example.浮点值 +'--' + example2.浮点值 ;"
                + " println result;"
                + " return result ;"
                + "";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.loadMultiExpress("", expressDefine);
        runner.loadMultiExpress("ClassTest", express);
        Object r = runner.executeByExpressName("ClassTest", context,
            null, false, false, null);
        Assert.assertTrue("VClass的作用域错误", r.toString().equalsIgnoreCase("330--430--xuannan--xuanyu--199.99--11.11"));
    }
}
