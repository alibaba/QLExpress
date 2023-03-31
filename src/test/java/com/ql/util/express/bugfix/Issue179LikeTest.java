package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: DQinYuan
 */
public class Issue179LikeTest {

    @Test
    public void test() throws Exception {
        String express = "'1006' like '6%'";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        Object res = runner.execute(express, context, null, true, true);
        Assert.assertFalse((Boolean) res);
    }

}
