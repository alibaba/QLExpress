package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.exception.QLException;
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
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);
    }

    @Test(expected = QLException.class)
    public void test_variable_argument() throws Exception {
        //DynamicParamsUtil.supportDynamicParams = true;
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();

        MyString myString = new MyString();
        runner.addFunctionOfServiceMethod("format", myString, "format", new Class[] {String.class, Object[].class},
            null);
        runner.execute(""
                + "import com.ql.util.express.bugfix.MyString;"
                + "MyString myString = new MyString();"
                + "String.format(\"%s+%s\", \"1\", \"2\")",
            context, null, false, false);
    }
}
