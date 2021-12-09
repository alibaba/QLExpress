package com.ql.util.express.test;

import java.util.AbstractList;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import org.junit.Assert;
import org.junit.Test;

public class IsAssignableTest {
    @Test
    public void testABC() throws Exception {
        Assert.assertEquals("数据类型转换判断错误", true, ExpressUtil.isAssignable(boolean.class, Boolean.class));
        Assert.assertEquals("数据类型转换判断错误", true, ExpressUtil.isAssignable(char.class, Character.class));
        Assert.assertEquals("数据类型转换判断错误", true, ExpressUtil.isAssignable(long.class, int.class));
        Assert.assertEquals("数据类型转换判断错误", true, ExpressUtil.isAssignable(Long.class, int.class));
        Assert.assertEquals("数据类型转换判断错误", true, ExpressUtil.isAssignable(Long.class, Integer.class));
        Assert.assertEquals("数据类型转换判断错误", true, ExpressUtil.isAssignable(List.class, AbstractList.class));
        Assert.assertEquals("数据类型转换判断错误", ExpressUtil.isAssignable(List.class, AbstractList.class),
            ExpressUtil.isAssignableOld(
                List.class, AbstractList.class));
        Assert.assertEquals("数据类型转换判断错误", ExpressUtil.isAssignable(long.class, int.class),
            ExpressUtil.isAssignableOld(long.class, int.class));

        int index = ExpressUtil.findMostSpecificSignature(new Class[] {Integer.class},
            new Class[][] {{Integer.class}, {int.class}});

        System.out.println(index);

        String express = "bean.testInt(p)";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("bean", new BeanExample());
        context.put("p", 100);

        Object r = runner.execute(express, context, null, false, true);
        System.out.println(r);
        Assert.assertTrue("数据类型转换错误：", r.toString().equalsIgnoreCase("toString-int:100"));

        context = new DefaultContext<>();
        express = "bean.testLong(p)";
        context.put("bean", new BeanExample());
        context.put("p", 100L);
        r = runner.execute(express, context, null, false, true);
        Assert.assertTrue("数据类型转换错误：", r.toString().equalsIgnoreCase("toString-long:100"));
    }
}
