package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.junit.Test;

import java.lang.reflect.Method;

public class RecursivelyRunnerTest {

    static final String TEST_EXPRESS = "eval('10+10')+eval('20')+eval('30-10')";

    static final ExpressRunner globalRunner = new ExpressRunner();
    static{
        globalRunner.addFunction("eval",new GlobalEvalOperator());
    }


    @Test
    public  void testErrorRecursivelyInvoke() throws Exception{

        try {
    
            Object r = globalRunner.execute(TEST_EXPRESS, null, null, true, false);
            System.out.println(r);
        }catch (Exception e){
//            e.printStackTrace();
            System.out.println("符合预期，嵌套调用同一个ExpressRunner不支持!");
        }

    }

    @Test
    public  void testEvalOperator() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("eval",new EvalOperator());

        Object r = runner.execute(TEST_EXPRESS, null, null, true, false);
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
        

        Object r = runner.execute(TEST_EXPRESS, null, null, true, false);
        System.out.println(r);
        
    }

    public static class GlobalEvalOperator extends Operator{


        @Override
        public Object executeInner(Object[] list) throws Exception {
            Object result = globalRunner.execute((String) list[0], new DefaultContext(), null, true, false);
            return result;
        }
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
