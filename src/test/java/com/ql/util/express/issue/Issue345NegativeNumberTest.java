package com.ql.util.express.issue;

import java.math.BigDecimal;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zgxkbtl
 */
public class Issue345NegativeNumberTest {
    @Test
    public void test() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        BigDecimal value = new BigDecimal("12.22");
        context.put("b", value);
        String express = "-b";
        Object result = runner.execute(express, context, null, true, true);
        //Assert.assertTrue((Boolean)result);
        Assert.assertEquals(new BigDecimal("-12.22"), result);
    }
}