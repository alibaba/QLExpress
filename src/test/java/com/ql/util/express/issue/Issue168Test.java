package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue168Test {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        String exp = "\r\n"
            //+ "import com.ql.util.express.issue.Issue168Test;\r\n"
            //+ "return new Issue168Test();\r\n"
            + "import com.ql.util.express.issue.Issue168Test$Issue168InnerClass;\r\n"
            + "return new Issue168Test$Issue168InnerClass();\r\n";

        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println("result = " + result);
    }

    public static class Issue168InnerClass {

    }
}
