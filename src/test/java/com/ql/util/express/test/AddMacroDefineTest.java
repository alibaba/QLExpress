package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class AddMacroDefineTest {
    @Test
    public void test2Java() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfClassMethod("abc", BeanExample.class.getName(), "testLong", new String[] {"long"}, null);
        runner.addMacro("玄难", "abc(100);");
        String express = "玄难 + \" - Test\";";
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        System.out.println(r);
    }
}
