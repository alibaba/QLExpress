package com.ql.util.express.issue;

import java.math.BigDecimal;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue264Test {
    @Test
    public void test() throws Exception {
        String exp = "a/b*c";
        ExpressRunner runner = new ExpressRunner(true, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("a", 20);
        context.put("b", 6);
        context.put("c", 3);
        Object result = runner.execute(exp, context, null, false, false);
        System.out.println(result);

        BigDecimal result2 = new BigDecimal("20.0")
            .divide(new BigDecimal("6"), 10, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("3"));
        System.out.println("result2 = " + result2);
    }
}
