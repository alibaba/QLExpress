package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import org.junit.Assert;
import org.junit.Test;

public class DiffTypeCompareTest {

    @Test
    public void testDiffTypeCompare() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String[] expressionArray = new String[] {
            "x != 2",
            "x == \"2\"",
            "y != true",
            "y == \"true\""
        };

        for (String expression : expressionArray) {
            DefaultContext<String, Object> context = new DefaultContext<>();
            System.out.println(expression);
            context.put("x", 2);
            context.put("y", true);
            Object result = runner.execute(expression, context, null, true, false);
            Assert.assertEquals(false, result);
            System.out.println(result);
        }

        expressionArray = new String[] {
                "x == 2",
                "x == \"2\"",
                "y == true",
                "y == \"true\""
        };
        QLExpressRunStrategy.setCompareDiffTypeEqualAsFalse(false);
        for (String expression : expressionArray) {
            DefaultContext<String, Object> context = new DefaultContext<>();
            System.out.println(expression);
            context.put("x", 2);
            context.put("y", true);
            Object result = runner.execute(expression, context, null, true, false);
            Assert.assertEquals(true, result);
            System.out.println(result);
        }

    }
}
