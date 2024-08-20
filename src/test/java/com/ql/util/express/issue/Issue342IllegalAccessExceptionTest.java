package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author  gjx
 */
public class Issue342IllegalAccessExceptionTest {
     class MyPrintStream extends PrintStream {
        public MyPrintStream(OutputStream out) {
            super(out);
        }

        @Override
        public void println(String x) {
            super.println(x);
        }
    }
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @Before
    public void setUpStreams() {System.setOut(new MyPrintStream(outContent));
        System.setOut(new MyPrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
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