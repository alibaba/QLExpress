package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class ATempTest {
    @Test
    public void test2Java() throws Exception {
        //String express = "2 in (1,2,3)";
        //String express = "include Test; max(1,2,3)";
        String express = "when 1==2 then println(100000)";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(r);
    }
}
