package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue284LikeTest {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        String express = "param1 like \"%abc\"";
        Object result = runner.execute(express, context, null, true, true);
        Assert.assertFalse((Boolean)result);

        express = "\"abc\" like param2";
        result = runner.execute(express, context, null, true, true);
        Assert.assertFalse((Boolean)result);

        express = "param1 like param2";
        result = runner.execute(express, context, null, true, true);
        Assert.assertTrue((Boolean)result);
    }
}