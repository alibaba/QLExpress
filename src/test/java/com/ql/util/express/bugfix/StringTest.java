package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 17/7/5.
 */
public class StringTest {

    @Test
    public void testFunction() throws Exception {

        ExpressRunner runner = new ExpressRunner();
        String exp = "a = \"11111,2222\";p = a.split(\",\");";
        System.out.println(exp);
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);
    }
}
