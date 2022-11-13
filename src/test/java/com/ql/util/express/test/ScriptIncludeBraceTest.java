package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.exception.QLCompileException;
import org.junit.Assert;
import org.junit.Test;

public class ScriptIncludeBraceTest {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("{条件A}", true);
        context.put("{条件B}", false);
        Assert.assertThrows(QLCompileException.class,
            () -> runner.execute("{条件A} || {条件B}", context, null, true, false));
        Assert.assertThrows(QLCompileException.class,
            () -> runner.execute("\\{条件A\\} || \\{条件B\\}", context, null, true, false));
    }
}
