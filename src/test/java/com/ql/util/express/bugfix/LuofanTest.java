package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/29.
 */
public class LuofanTest {

    public class Response {

    }

    public static int lenOfAds(Response response) {
        return 1;
    }

    @Test
    public void testDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        runner.addFunctionOfClassMethod("lenOfAds", LuofanTest.class.getName(), "lenOfAds",
            new String[] {Response.class.getName()}, null);
        String exp = "lenOfAds(resp)";
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("resp", new Response());
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }
}
