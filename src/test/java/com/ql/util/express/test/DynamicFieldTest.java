package com.ql.util.express.test;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetRunner;
import org.junit.Assert;
import org.junit.Test;

public class DynamicFieldTest {

    @Test
    public void testField() throws Exception {
        String express = ""
            + "String 用户 = \"张三\";"
            + "费用.用户 = 100;"
            + "用户 = \"李四\";"
            + "费用.用户 = 200;";

        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Map<String, Object> fee = new HashMap<>();
        context.put("费用", fee);
        InstructionSet set = runner.parseInstructionSet(express);
        InstructionSetRunner.executeOuter(runner, set, null, context, null, true, false, true);
        runner.execute(express, context, null, false, true);
        System.out.println(context.get("费用"));
        Assert.assertEquals("动态属性错误", "100", fee.get("张三").toString());
        Assert.assertEquals("动态属性错误", "200", fee.get("李四").toString());
    }

    @Test
    public void testLoadFromFile() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        runner.loadExpress("testFunctionParameterType");
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("auctionUtil", new BeanExample());
        Object r = runner.executeByExpressName("testFunctionParameterType", context, null, false, false);
        System.out.println(r);
        System.out.println(context);
    }
}
