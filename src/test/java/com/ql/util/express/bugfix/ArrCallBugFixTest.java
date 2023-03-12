package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class ArrCallBugFixTest {

    @Test
    public void fieldCallEmbedArrayTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        Map<String, String> m1 = new HashMap<>();
        m1.put("b", "bbb");

        Map<String, Map<String, String>> m = new HashMap<>();
        m.put("a", m1);
        context.put("l", new Object[] {m});
        String express = "l[0].a.b";
        Object res = runner.execute(express,
                context, null, false, true);
        Assert.assertEquals("bbb", res);
    }

    @Test
    public void arrayCallEmbedArrayTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

        Map<String, java.lang.String[]> m = new HashMap<>();
        m.put("a", new String[] {"bbb"});
        context.put("l", new Object[] {m});
        String express = "l[0].a[0]";
        Object res = runner.execute(express,
                context, null, false, true);
        Assert.assertEquals("bbb", res);
    }
}
