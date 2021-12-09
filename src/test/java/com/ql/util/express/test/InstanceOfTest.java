package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tianqiao on 17/9/19.
 */
public class InstanceOfTest {

    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        String express = "s='';return s  instanceof String";
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("InstanceOfTest 出错", r.toString().equals("true"));
        System.out.println(r);

        express = "s='';return s  instanceof Object";
        r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("InstanceOfTest 出错", r.toString().equals("true"));
        System.out.println(r);
    }
}
