package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue292Test {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        String express = ""
            + "map = NewMap('age':'9');"
            + "return map.age;";
        Object result = runner.execute(express, context, null, true, true);
        Assert.assertTrue(result instanceof Character);

        express = ""
            + "map = NewMap('age':'10');"
            + "return map.age;";
        result = runner.execute(express, context, null, true, true);
        Assert.assertTrue(result instanceof String);
    }
}
