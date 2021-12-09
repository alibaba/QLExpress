package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

public class ImportTest {
    @Test
    public void testImport() throws Exception {
        String express = "import java.math.*;" +
            "import com.ql.util.express.test.BeanExample;" +
            "abc = new BeanExample(\"张三\").unionName(\"李四\") ;" +
            "return new BigInteger(\"1000\");";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("import 实现错误", r.toString().equals("1000"));
        System.out.println(r);
        System.out.println(context);
    }
}
