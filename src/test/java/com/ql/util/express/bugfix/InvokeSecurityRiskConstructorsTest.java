package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.example.CustBean;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.exception.QLSecurityRiskException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InvokeSecurityRiskConstructorsTest {
    public InvokeSecurityRiskConstructorsTest(){

    }
    private boolean preForbidInvokeSecurityRiskConstructors;

    @Before
    public void before() {
        preForbidInvokeSecurityRiskConstructors = QLExpressRunStrategy.isForbidInvokeSecurityRiskConstructors();

        //系统默认阻止的方法黑名单:System.exit(1);Runtime.getRuntime().exec()两个函数
        QLExpressRunStrategy.setForbidInvokeSecurityRiskConstructors(true);

        //白名单
        QLExpressRunStrategy.addSecureConstructor(InvokeSecurityRiskConstructorsTest.class);
        QLExpressRunStrategy.addSecureConstructor(CustBean.class);
        QLExpressRunStrategy.addSecureConstructor(java.util.Date.class);
        QLExpressRunStrategy.addSecureConstructor(java.util.LinkedList.class);

        //QLExpressRunStrategy.addRiskSecureConstructor(InvokeSecurityRiskConstructorsTest.class);
    }

    @Test
    public void test() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();

        String[] expressList = new String[] {
                "import com.ql.util.express.bugfix.InvokeSecurityRiskConstructorsTest;" +
                        "InvokeSecurityRiskConstructorsTest w = new InvokeSecurityRiskConstructorsTest();return w;"
                ,   "import com.ql.util.express.bugfix.InvokeSecurityRiskMethodsTest;" +
                "InvokeSecurityRiskMethodsTest w = new InvokeSecurityRiskMethodsTest();"};

        Object result = expressRunner.execute(expressList[0], context, null, true, false, 1000);
        Assert.assertTrue(result instanceof InvokeSecurityRiskConstructorsTest);

        try {
            result = expressRunner.execute(expressList[1], context, null, true, false, 1000);
            Assert.fail();
        }catch (QLException e) {
            //预期内走这里
            Assert.assertEquals(e.getCause().getMessage(), "使用QLExpress调用了不安全的系统构造函數:public com.ql.util.express.bugfix.InvokeSecurityRiskMethodsTest()");
        }
    }

    @Test
    public void testDefault() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        String[] expressList = new String[] {
                "import java.net.Socket;" +
                        "return new Socket();"};

        try {
            Object result = expressRunner.execute(expressList[0], context, null, false, false, 1000);
            Assert.fail();
        }catch (QLException e) {
            //预期内走这里
            Assert.assertEquals(e.getCause().getMessage(), "使用QLExpress调用了不安全的系统构造函數:public java.net.Socket()");
        }
        QLExpressRunStrategy.addSecureConstructor(java.net.Socket.class);

        Object result = expressRunner.execute(expressList[0], context, null, true, false, 1000);
        Assert.assertTrue(result instanceof java.net.Socket);
    }
}
