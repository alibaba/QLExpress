package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NullCompareTest {
    
    @Before
    public void before()        
    {
        QLExpressRunStrategy.setCompareNullLessMoreAsFalse(true);
    }
    @After
    public void after()
    {
        QLExpressRunStrategy.setCompareNullLessMoreAsFalse(false);
    }
    @Test
    public void testNullCompar() throws Exception{
        
        ExpressRunner runner = new ExpressRunner();
        String[] explist = new String[]{
                "x < 1",
                "y > 1",
                "x != 2",
        };
        for(String exp:explist) {
            IExpressContext<String, Object> context = new DefaultContext<String, Object>();
            System.out.println(exp);
            ((DefaultContext<String, Object>) context).put("x",2);
            Object result = runner.execute(exp, context, null, true, false);
            Assert.assertTrue((Boolean)result==false);
            System.out.println(result);
        }

        explist = new String[]{
                "x > 1",
                "y == null",
                "x == 2",
        };
        for(String exp:explist) {
            IExpressContext<String, Object> context = new DefaultContext<String, Object>();
            System.out.println(exp);
            ((DefaultContext<String, Object>) context).put("x",2);
            Object result = runner.execute(exp, context, null, true, false);
            Assert.assertTrue((Boolean)result==true);
            System.out.println(result);
        }
    }
}
