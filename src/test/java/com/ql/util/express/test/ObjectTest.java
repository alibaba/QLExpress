package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class ObjectTest {
    @Test
    public void testABC() throws Exception {
        String express = "object.amount*2+object.volume";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        ObjectBean tempObject = new ObjectBean(100, 60);
        context.put("object", tempObject);
        Object result = runner.execute(express, context, null, false, true);
        System.out.println(result);
        Assert.assertNotSame("数据执行错误", 200, result);
    }

    @Test
    public void testABC2() throws Exception {
        String express = "object.getAmount(1)";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        ObjectBean tempObject = new ObjectBean(100, 60);
        context.put("object", tempObject);
        Object result = runner.execute(express, context, null, false, true);
        System.out.println(result);
    }
}
