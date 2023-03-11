package com.ql.util.express.test.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import java.math.BigDecimal;

/**
 * @Author TaoKan
 * @Date 2022/9/12 上午10:31
 */
public class TestIssue {
    public static void main(String[] args) throws Exception {

        ExpressRunner runner = new ExpressRunner(true, false);
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("a", 6500);
        context.put("b",24);
        context.put("c",0.75);
        Object result = runner.execute("a/b*c", context, null, true, true);
        System.out.println("a/b*c=" + result.toString());

//        BigDecimal a = new BigDecimal(6500);
//        BigDecimal b = new BigDecimal(24);
//        BigDecimal c = new BigDecimal("0.75");
//        System.out.println(a.divide(b).multiply(c));
    }
}
