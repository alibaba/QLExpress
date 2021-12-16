package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * 测试重载适配性的case
 * Created by tianqiao on 17/6/20.
 */
public class AdjustTypesTest {
    public static final AdjustTypesTest instance = new AdjustTypesTest();

    public Integer test(Integer a) {
        System.out.println("invoke Integer");
        return a;
    }

    public String test(String a) {
        System.out.println("invoke String");
        return a;
    }

    public Object test(Object... a) {
        System.out.println("invoke dynamic params");
        return a[a.length - 1];
    }

    @Test
    public void testDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        runner.addFunctionOfServiceMethod("test", instance, "test", new Class[] {Integer.class}, null);
        runner.addFunctionOfServiceMethod("testString", instance, "test", new Class[] {String.class}, null);
        String exp = "test(1) +test(1) + testString('aaaa')";
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }

    @Test
    public void testDemo2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("testAdjustTypes", instance);
        String exp = "testAdjustTypes.test(1) + testAdjustTypes.test(1) + testAdjustTypes.test('aaaa')";
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }

    @Test
    public void testDemo3() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("testAdjustTypes", instance);
        String exp
            = "testAdjustTypes.test(1) + testAdjustTypes.test(1) + testAdjustTypes.test('aaaa')+ testAdjustTypes.test"
            + "('aaaa','bbbbb')";
        Object result = runner.execute(exp, context, null, false, true);
        System.out.println(result);
    }
}
