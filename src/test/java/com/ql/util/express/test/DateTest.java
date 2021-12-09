package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tianqiao on 16/12/20.
 */
public class DateTest {

    @Test
    public void testDateCompare() throws Exception {
        String express = "a = new Date();b=a;a==b";
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("testDateCompare", (Boolean)r);
    }

    @Test
    public void testDateCompare1() throws Exception {
        String express = "a = new Date();for(i=0;i<1000;i++){f=f+i;}b=new Date();a<=b";
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("testDateCompare", (Boolean)r);
    }

    @Test
    public void testDateCompare2() throws Exception {
        String express = "a = new Date();for(i=0;i<1000;i++){f=f+i;}b=new Date();b>a";
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, context, null, false, false);
        Assert.assertTrue("testDateCompare", (Boolean)r);
    }
}
