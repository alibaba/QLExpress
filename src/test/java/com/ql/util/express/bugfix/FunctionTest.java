package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/20.
 */
public class FunctionTest {

    @Test
    public void testFunction() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        String exp = "function abc(int a,int b){ return a + b;} return abc(1+100,2*100)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);
    }

    @Test
    public void testFunction2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String exp =
            "function FAILFUN(Integer errorCode,String message){\n" +
                "System.out.println(errorCode+\" \"+message);\n" +
                "return NewMap(\"message\":message,\"errorCode\":errorCode,\"success\":false);\n" +
                "}\n" +
                "FAILFUN(-1,'error')";
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);
    }

    @Test
    public void testFunction3() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String funExp =
            "function FAILFUN(Integer errorCode,String message){\n" +
                "System.out.println(errorCode+\" \"+message);\n" +
                "return NewMap(\"message\":message,\"errorCode\":errorCode,\"success\":false);\n" +
                "}\n";
        runner.loadMultiExpress("", funExp);

        String exp = "FAILFUN(-1,'error')";
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);

        String exp2 = "FAILFUN(-12,'error')";
        IExpressContext<String, Object> context2 = new DefaultContext<>();
        Object result2 = runner.execute(exp2, context2, null, false, false);
        System.out.println(result);
    }
}
