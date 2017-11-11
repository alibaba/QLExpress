package com.ql.util.express.example;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/4.
 */
public class ArgumentTypeMismatchTest {
    
    
    private static ArgumentTypeMismatchTest singleton = new ArgumentTypeMismatchTest();
    
    public void functionABC(Long a,Integer b,String c)
    {
        System.out.println("functionABC");
    }
    
    @Test
    public void test1() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunctionOfServiceMethod("abc", singleton,"functionABC",new Class[]{Long.class,Integer.class,String.class},null);
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a","1");
        context.put("b","2");
        context.put("c","3");
        try {
            runner.execute(exp, context, null, false, false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    @Test
    public void test2() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("abc", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                Long paramA = Long.valueOf(list[0].toString());
                Integer paramB = Integer.valueOf(list[1].toString());
                String paramC = list[2].toString();
                singleton.functionABC(paramA,paramB,paramC);
                return null;
            }
        });
        String exp = "abc(a,b,c)";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("a","1");
        context.put("b","2");
        context.put("c","3");
        try {
            runner.execute(exp, context, null, false, false);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.print("test ok!");
    }
}
