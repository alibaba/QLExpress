package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/20.
 */
public class ThrowExceptionTest {
    
    public String testParseLong(String a) throws Exception {
        try {
            return Long.valueOf(a).toString();
        }catch (Exception e) {
//            e.printStackTrace();
            throw new Exception("Exception happened!",e);
        }
    }
    
    @Test
    public void testException()
    {
        try {
            ExpressRunner runner = new ExpressRunner();
            String exp = "a = new com.ql.util.express.bugfix.ThrowExceptionTest();a.testParseLong('11112LL')";
            IExpressContext<String, Object> context = new DefaultContext<String, Object>();
            Object result = runner.execute(exp, context, null, false, false);
            System.out.println(result);
        }catch (Exception e){
//            e.printStackTrace();
    
            String keywords = "run QlExpress Exception at line 1";
            String exceptionString = e.toString();
            Assert.assertTrue(exceptionString.contains(keywords));
    
            keywords = "java.lang.reflect.InvocationTargetException";
            exceptionString = e.getCause().toString();
            Assert.assertTrue(exceptionString.contains(keywords));
            
            keywords = "Exception happened";
            exceptionString = e.getCause().getCause().toString();
            Assert.assertTrue(exceptionString.contains(keywords));
            
            keywords = "java.lang.NumberFormatException";
            exceptionString = e.getCause().getCause().getCause().toString();
            Assert.assertTrue(exceptionString.contains(keywords));
        }
    }
}
