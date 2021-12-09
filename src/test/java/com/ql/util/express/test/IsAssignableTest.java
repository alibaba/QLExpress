package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.ExpressUtil;
import org.junit.Assert;
import org.junit.Test;

public class IsAssignableTest {
    @Test
    public void testABC() throws Exception {
        Assert.assertTrue("数据类型转换判断错误", ExpressUtil.isAssignable(boolean.class, Boolean.class) == true);
        Assert.assertTrue("数据类型转换判断错误", ExpressUtil.isAssignable(char.class, java.lang.Character.class) == true);
        Assert.assertTrue("数据类型转换判断错误", ExpressUtil.isAssignable(long.class, int.class) == true);
        Assert.assertTrue("数据类型转换判断错误", ExpressUtil.isAssignable(Long.class, int.class) == true);
        Assert.assertTrue("数据类型转换判断错误", ExpressUtil.isAssignable(Long.class, Integer.class) == true);
        Assert.assertTrue("数据类型转换判断错误",
            ExpressUtil.isAssignable(java.util.List.class, java.util.AbstractList.class) == true);
        Assert.assertTrue("数据类型转换判断错误",
            ExpressUtil.isAssignable(java.util.List.class, java.util.AbstractList.class) == ExpressUtil.isAssignableOld(
                java.util.List.class, java.util.AbstractList.class));
        Assert.assertTrue("数据类型转换判断错误",
            ExpressUtil.isAssignable(long.class, int.class) == ExpressUtil.isAssignableOld(long.class, int.class));

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
