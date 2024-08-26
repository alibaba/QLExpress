package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class ForFlowFunctionTest {
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
    }

    @Test
    public void testABC() throws Exception {
        String express = ""
            + "for(i = 0; i < 1; i = i + 1){"
            + "    打印(70);"
            + "}"
            + "打印(70);"
            + "return 10;";
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfServiceMethod("打印", System.out, "println", new String[] {"int"}, null);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("for循环后面跟着一个函数的时候错误", "10", r.toString());
    }
}
