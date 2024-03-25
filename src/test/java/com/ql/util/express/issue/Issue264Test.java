package com.ql.util.express.issue;

import java.math.BigDecimal;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * @author 冰够
 */
public class Issue264Test {
    /**
     * isPrecise:false, result:10.0              , result == 10.0:true
     * isPrecise:false, result:9.9999999999      ,     d1 == d2  :false
     *
     * isPrecise:true, result:9.9999999999      , result == 10.0:false
     * isPrecise:true, result:9.9999999999      ,     d1 == d2  :true
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        String exp = "a/b*c";
        boolean isPrecise = true;
        ExpressRunner runner = new ExpressRunner(isPrecise, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("a", 20.0);
        context.put("b", 6);
        context.put("c", 3);
        Object result = runner.execute(exp, context, null, false, false);
        Double d1 = null;
        if (result instanceof Double) {
            d1 = (Double)result;
        }
        if (result instanceof BigDecimal) {
            d1 = ((BigDecimal)result).doubleValue();
        }
        System.out.printf("isPrecise:%s, result:%-18s, result == 10.0:%s\n", isPrecise, d1, d1 == 10.0);

        BigDecimal bigDecimal = new BigDecimal("20.0")
            .divide(new BigDecimal("6"), 32, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal("3"));
        BigDecimal bigDecimal1 = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println("bigDecimal = " + bigDecimal);
        System.out.println("bigDecimal1 = " + bigDecimal1);
        double d2 = bigDecimal.doubleValue();
        System.out.printf("isPrecise:%s, result:%-18s,     d1 == d2  :%s\n", isPrecise, d2, d1 == d2);
    }
}
