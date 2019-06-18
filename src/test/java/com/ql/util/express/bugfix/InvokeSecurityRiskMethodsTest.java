package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.exception.QLSecurityRiskException;
import com.ql.util.express.exception.QLTimeOutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InvokeSecurityRiskMethodsTest {
    
    @Before
    public void before()        
    {
        QLExpressRunStrategy.setForbiddenInvokeSecurityRiskMethods(true);
    }
    @After
    public void after()
    {
        QLExpressRunStrategy.setForbiddenInvokeSecurityRiskMethods(false);
    }
    private static String[] expressList = new String[]{
            "System.exit(1);",
            "for(i=1;i<10;i++){\nRuntime.getRuntime().exec('echo 1+1');}"
    };

    @Test
    public void test() throws Exception {

        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

        for(String express : expressList) {
            try {

                Object r = runner.execute(express, context, null, true, false, 1000);
                System.out.println(r);
                throw new Exception("没有捕获到不安全的方法");
            } catch (QLException e) {
                System.out.println(e.getCause());
            }
        }
    }
}
