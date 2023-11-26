package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
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

        //用户还可以增加一些类的方法黑名单
        QLExpressRunStrategy.addSecureConstructor(InvokeSecurityRiskConstructorsTest.class);
        //QLExpressRunStrategy.addRiskSecureConstructor(InvokeSecurityRiskConstructorsTest.class);
    }

    private static final String[] expressList = new String[] {
        "import com.ql.util.express.bugfix.InvokeSecurityRiskConstructorsTest;" +
        "InvokeSecurityRiskConstructorsTest w = new InvokeSecurityRiskConstructorsTest();"
    ,   "import com.ql.util.express.bugfix.InvokeSecurityRiskMethodsTest;" +
        "InvokeSecurityRiskMethodsTest w = new InvokeSecurityRiskMethodsTest();"};

    @Test
    public void test() throws Exception {
        ExpressRunner expressRunner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();

        for (String express : expressList) {
            try {
                Object result = expressRunner.execute(express, context, null, true, false, 1000);
                System.out.println(result);
            } catch (QLException e) {
                System.out.println(e.getCause());
            }
        }
    }
}
