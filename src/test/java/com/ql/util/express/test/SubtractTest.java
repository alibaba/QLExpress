package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class SubtractTest {
    @Test
    public void testMax() throws Exception {
        //String express = "return max(max(0.0,1) - 0.95,0);";
        String express = "-3-(-5*-7-9)-(9-2);";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        System.out.println(r);
        Assert.assertEquals("\"-\"号测试", "-36", r.toString());
    }
}
