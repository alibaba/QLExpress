package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.config.QLExpressRunStrategy;
import org.junit.Test;

/**
 * @Author TaoKan
 * @Date 2022/12/25 下午4:35
 */
public class CaseTest {
    @Test
    public void elTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        QLExpressRunStrategy.setForbidInvokeSecurityRiskMethods(true);
        runner.addFunctionOfServiceMethod("myFunction", this, "myFunction", new Class[]{double[].class}, null);
        DefaultContext<String, Object> context = new DefaultContext<>();
        String express = "a = [1.1,2.2,3.3];result = myFunction(a);System.out.println(\"result ----------\"+result);return result;";
        Object r = runner.execute(express, context, null, true, true);
        System.out.println(r);
    }

    public int myFunction(double[] params) {
        return params.length;
    }
}