package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * created by jiwenxing on 2022/5/12
 */
public class ScientificNumberTest {

    static List<String> express = new ArrayList<>();
    static {
        express.add("2e2==200");
        express.add("2E2==200");
        express.add("2.0e2==200");
        express.add("2.0E2==200");
    }

    @Test
    public void testFunction() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();
        for (String exp: express) {
            Object result = runner.execute(exp, context, null, false, false);
            Assert.assertTrue((Boolean)result);
        }
    }
}
