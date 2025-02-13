package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue368Test {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        String express = "'测试一下'>1";
        Object result = runner.execute(express, context, null, true, true);
        Assert.assertTrue((Boolean)result);
    }
}
