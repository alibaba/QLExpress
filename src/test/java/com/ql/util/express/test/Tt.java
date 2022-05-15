package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

/**
 * Author: DQinYuan
 */
public class Tt {

    String a;

    public static String getB() {
        return "bbbb";
    }
    public String getA() {
        return a;
    }

    public static void main(String[] args) throws Exception {
        String express = "c.getB()";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Tt tt = new Tt();
        tt.a = "10";
        context.put("c", tt);

        Object res = runner.execute(express, context, null, true, true);
    }

}
