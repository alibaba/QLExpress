package com.ql.util.express.bugfix;

import java.lang.reflect.Method;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.junit.Test;

/**
 * 可重入性单元测试
 */
public class RecursivelyRunnerTest {

    static ExpressRunner runner = new ExpressRunner();

    @Test
    public void testEvalOperator() throws Exception {
        runner.addFunction("eval", new EvalOperator());
        String express = "eval('10')+eval('20')+eval('30')";
        Object r = runner.execute(express, null, null, true, false);
        System.out.println(r);
    }

    @Test
    public void testSubRunner() throws Exception {
        //bind SubRunner.eval method
        SubRunner subRunner = new SubRunner();
        Method[] methods = SubRunner.class.getDeclaredMethods();
        for (Method m : methods) {
            String name = m.getName();
            runner.addFunctionOfServiceMethod(name, subRunner, name, m.getParameterTypes(), null);
        }

        String express = "eval2('10')+eval2('20')+eval2('30')";
        Object r = runner.execute(express, null, null, true, false);
        System.out.println(r);
    }

    public static class EvalOperator extends Operator {
        @Override
        public Object executeInner(Object[] list) throws Exception {
            return runner.execute((String)list[0], new DefaultContext(), null, true, false);
        }
    }

    public static class SubRunner {
        public Object eval2(String obj) throws Exception {
            return runner.execute(obj, new DefaultContext(), null, true, false);
        }
    }
}
