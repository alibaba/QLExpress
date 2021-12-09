package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorSelfDefineClassFunction;
import org.junit.Test;

public class FunctionDescTest {
    @Test
    public void testFunctionDesc() throws Exception {
        String express = "isVIP(\"qianghui\")";
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
            "isVIP", new String[] {"String"}, new String[] {"用户名称"}, new String[] {"UserName"}, "$1不是VIP用户");
        OperatorBase op = runner.getFunciton("isVIP");
        System.out.println(op.getOperDataDesc());
        System.out.println(op.getOperDataAnnotaion());

        Object r = runner.execute(express, context, null, false, false);
        System.out.println(r);

        runner.replaceOperator("isVIP", new OperatorSelfDefineClassFunction("isVIP",
            BeanExample.class.getName(), "isVIPTwo", new String[] {"String"}, null, null, null));
        Object r2 = runner.execute(express, context, null, false, false);
        System.out.println(r2);
        //		Assert.assertTrue("属性操作错误", r.toString().equalsIgnoreCase("ffff"));
        //		Assert.assertTrue("属性操作错误", ((BeanExample)context.get("example")).child.a.toString().equalsIgnoreCase("ssssssss"));
    }
}
