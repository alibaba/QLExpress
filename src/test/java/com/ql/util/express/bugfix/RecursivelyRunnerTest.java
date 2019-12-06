package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.junit.Test;

import java.lang.reflect.Method;

public class RecursivelyRunnerTest {

    @Test
    public  void testEvalOperator() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("eval",new EvalOperator());
        String express = "eval('10')+eval('20')+eval('30')";
        Object r = runner.execute(express, null, null, true, false);
        System.out.println(r);

    }
    
    @Test
    public  void testSubRunner() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        
        //bind SubRunner.evel method
        SubRunner subRunner = new SubRunner();
        Method [] methods = SubRunner.class.getDeclaredMethods();
        for (Method m : methods) {
            String name = m.getName();
            runner.addFunctionOfServiceMethod(name, subRunner, name, m.getParameterTypes(), null);
        }
        
        String express = "eval('10')+eval('20')+eval('30')";
        Object r = runner.execute(express, null, null, true, false);
        System.out.println(r);
        
    }

    public static class EvalOperator extends Operator{

        private ExpressRunner subRun = new ExpressRunner();
    
        @Override
        public Object executeInner(Object[] list) throws Exception {
            Object result = subRun.execute((String) list[0], new DefaultContext(), null, true, false);
            return result;
        }
    }
    
    public static class SubRunner {
        
        private ExpressRunner subRun = new ExpressRunner();
        
        public Object eval(String obj) throws Exception {
            Object result = subRun.execute(obj, new DefaultContext(), null, true, false);
            return result;
        }
    }
}
