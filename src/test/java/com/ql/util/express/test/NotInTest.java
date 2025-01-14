package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NotInTest {
    @Test
    public void testOperatorNotIn() throws Exception {

        String express1 = "2 notIn (2,3) ";
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object execute = runner.execute(express1, context, null, false, false);
        Assert.assertFalse((Boolean) execute);

        String express2 = "2 notIn a";
        int[] a = {1, 2, 3};
        context.put("a", a);
        execute = runner.execute(express2, context, null, false, false);
        Assert.assertFalse((Boolean) execute);

        String express3 = "2 notIn b";
        List<Integer> b = new ArrayList<>();
        b.add(2);
        b.add(3);

        context.put("b", b);
        execute = runner.execute(express3, context, null, false, false);
        Assert.assertFalse((Boolean) execute);
    }
}
