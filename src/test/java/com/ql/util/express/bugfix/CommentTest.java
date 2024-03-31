package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * Author: DQinYuan
 */
public class CommentTest {

    @Test
    public void invalidNumInCommitTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        // 定义表达式
        String express = "/** 2倍 **/ 1+1";
        Object result = runner.execute(express, context, null, true, true);
        Assert.assertEquals(2, result);
    }

}
