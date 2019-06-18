package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.exception.QLBizException;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.exception.QLException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/20.
 */
public class ThrowExceptionTest {
    
    public String testParseLong(String a) throws Exception {
        return Long.valueOf(a).toString();
    }

    public void runExpress(String exp) throws Exception {

        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);
    }


    @Test
    public void testQLCompileException() throws Exception {

        String[] expList = new String[]{
                "a = 0;\nfor(i=0;){a=a+1;}return a;",
                "1a>1"
        };

        for(String exp : expList) {
            try {
                runExpress(exp);
            } catch (QLCompileException e) {
                System.out.println("捕获QLCompileException类型的异常成功.");
                System.out.println(e.getCause());
//                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("捕获到系统异常");
            }
        }
    }

    @Test
    public void testQLException() throws Exception {

        String[] expList = new String[]{
                "a>1",
                "a = new com.ql.util.express.bugfix.ThrowExceptionTest();a.testParseLong2('11112LL')"
        };

        for(String exp : expList) {
            try {
                runExpress(exp);
            } catch (QLException e) {
                System.out.println("捕获QLException类型的异常成功.");
                System.out.println(e.getCause());
//                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("捕获到系统异常");
            }
        }
    }
    
    @Test
    public void testQLBizException() throws Exception {

        String[] expList = new String[]{
                "a = new com.ql.util.express.bugfix.ThrowExceptionTest();a.testParseLong('11112LL')"
        };

        for(String exp : expList) {
            try {
                runExpress(exp);
            } catch (QLBizException e) {
                System.out.println("捕获到QLBizException业务系统异常成功.");
                System.out.println(e.getCause());
                System.out.println(e.getCause().getCause());
//                e.printStackTrace();

                String keywords = "run QlExpress Exception at line 1";
                String exceptionString = e.toString();
                Assert.assertTrue(exceptionString.contains(keywords));

                keywords = "java.lang.reflect.InvocationTargetException";//反射调用的异常，无法避免
                exceptionString = e.getCause().toString();
                Assert.assertTrue(exceptionString.contains(keywords));


                keywords = "java.lang.NumberFormatException";
                exceptionString = e.getCause().getCause().toString();
                Assert.assertTrue(exceptionString.contains(keywords));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("捕获到系统异常");
            }
        }
    }
}
