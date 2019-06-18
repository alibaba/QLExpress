package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class PreloadExpressTest {
    
    @Test
    public void preloadExpress() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.loadMutilExpress(null,
                "function add(int a, int b){return a+b;} \n" +
                        "function sub(int a, int b){return a-b;}");
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("m", 1);
        context.put("n", 1);
        Object object = runner.execute("add(m,n)+sub(2,-2)", context, null, true, false);
        System.out.println(object);
        Assert.assertTrue((Integer)object==6);
    }
}
