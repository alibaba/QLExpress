package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author  gjx
 */
public class Issue342IllegalAccessExceptionTest {
    @Test
    public void test10() throws Exception {
        String express = ""
                + "    打印(70);"
                + "   return 10;";
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfServiceMethod("打印", System.out, "println", new String[] {"int"}, null);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("for循环后面跟着一个函数的时候错误", "10", r.toString());
    }
}