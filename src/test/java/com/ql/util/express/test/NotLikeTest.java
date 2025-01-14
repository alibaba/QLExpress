package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class NotLikeTest {

    @Test
    public void test() throws Exception {

        String express1 = "2 notLike '%2%' ";
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object execute = runner.execute(express1, context, null, false, false);
        Assert.assertFalse((Boolean) execute);

        String express2 = "sag notLike '%sag' ";
        context = new DefaultContext<>();
        execute = runner.execute(express2, context, null, false, false);
        Assert.assertTrue((Boolean) execute);


        String express3 = "'sag' notLike '%sag%' ";
        context = new DefaultContext<>();
        execute = runner.execute(express3, context, null, false, false);
        Assert.assertFalse((Boolean) execute);


        String express4 = "null notLike '%sag%' ";
        context = new DefaultContext<>();
        execute = runner.execute(express4, context, null, false, false);
        Assert.assertTrue((Boolean) execute);
    }
}
