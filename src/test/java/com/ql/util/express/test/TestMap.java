package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class TestMap {

    @Test
    public void testInt2Object() throws Exception {
        String express = "Map a = new HashMap(); a.put(\"a\",100 - 10);return a.get(\"a\")";
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("Map读取错误", r.toString().equalsIgnoreCase("90"));
    }

    @Test
    public void testmain() throws Exception {
        IExpressContext<String, Object> expressContext = new IExpressContext<String, Object>() {
            Map<String, Object> map = new HashMap<>();

            public Object put(String name, Object object) {
                return map.put(name, object);
            }

            public Object get(Object key) {
                return map.get(key);
            }
        };

        Map<String, Object> map = new HashMap<>();
        map.put("key1", 1);
        expressContext.put("map", map);

        String expressionstr = "map.key1";
        ExpressRunner runner = new ExpressRunner(false, true);
        Object r = runner.execute(expressionstr, expressContext, null,
            true, true);
        Assert.assertTrue("Map读取错误", r.toString().equalsIgnoreCase("1"));
    }
}
