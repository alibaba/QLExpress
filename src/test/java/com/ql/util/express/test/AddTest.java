package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class AddTest {
    @Test
    public void test() throws Exception {
        String express = "5+'5'";
        ExpressRunner expressRunner = new ExpressRunner(false, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object result = expressRunner.execute(express, context, null, false, false, null);
        System.out.println("result = " + result);
    }
}
