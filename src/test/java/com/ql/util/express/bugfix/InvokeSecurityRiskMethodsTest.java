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
        //Blacklist of methods blocked by the system by default: System.exit(1); Runtime.getRuntime().exec() two functions
        QLExpressRunStrategy.setForbiddenInvokeSecurityRiskMethods(true);

        //Users can also add some method blacklists
        QLExpressRunStrategy.addSecurityRiskMethod(InvokeSecurityRiskMethodsTest.class,"echo");
    }
    @After
    public void after()
    {
        QLExpressRunStrategy.setForbiddenInvokeSecurityRiskMethods(false);
    }
    private static String[] expressList = new String[]{
            "System.exit(1);",
            "for(i=1;i<10;i++){\nRuntime.getRuntime().exec('echo 1+1');}",
            "a = new com.ql.util.express.bugfix.InvokeSecurityRiskMethodsTest();a.echo('hello')"
    };

    public String echo(String a)
    {
        return a;
    }

    @Test
    public void test() throws Exception {

        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

        for(String express : expressList) {
            try {

                Object r = runner.execute(express, context, null, true, false, 1000);
                System.out.println(r);
                throw new Exception("Did not catch unsafe methods");
            } catch (QLException e) {
                System.out.println(e.getCause());
            }
        }
    }
}
