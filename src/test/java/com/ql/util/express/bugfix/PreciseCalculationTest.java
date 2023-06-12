package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author zhanglei
 * @date 2023-05-25.
 */
public class PreciseCalculationTest {

    @Test
    public void testPrecise() throws Exception {
        String exp = "a/b*c";
		ExpressRunner runner = new ExpressRunner(true, false);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("a", 20);
		context.put("b", 6);
		context.put("c", 3);
		Object result = runner.execute(exp, context, null, false, false);
        Assert.assertTrue(((BigDecimal)result).doubleValue() == 10d);
    }

    @Test
    public void testPrecise2() throws Exception {
        String exp = "a/b*c";
        ExpressRunner runner = new ExpressRunner(true, false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a", 20.34);
        context.put("b", 6);
        context.put("c", 3);
        Object result = runner.execute(exp, context, null, false, false);
        Assert.assertTrue(((BigDecimal)result).doubleValue() == 10.1700000000d);
    }

    @Test
    public void testResultOverflow() throws Exception {
        String evalExpress = "a*2";
        final ExpressRunner runner = new ExpressRunner(true, false);
        DefaultContext<String, Object> nameMap = new DefaultContext<String, Object>();
        nameMap.put("a", new BigDecimal(Long.MAX_VALUE));
        Object result = runner.execute(evalExpress, nameMap, null, false, false);
        Assert.assertTrue(((BigDecimal)result).doubleValue() == 18446744073709551616.0000000000d);
    }
}

