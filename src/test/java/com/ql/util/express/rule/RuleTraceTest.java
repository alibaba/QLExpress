package com.ql.util.express.rule;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by tianqiao on 16/12/8.
 */
public class RuleTraceTest {

    public class Demo
    {
        private boolean b = true;

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }
    }
    
    @Test
    public void testSimpleTrace() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express= "if(1+2==3 and 4>3 or b>3){return 'OK';}else if(1+2>1){'OK';}";
        RuleResult ruleResult= runner.executeRule(express, context, true, false);
        System.out.println("express:\n"+express);
        System.out.println("result:\n"+ruleResult.getResult());
        System.out.println("tree:\n"+ruleResult.getRule().toTree());
        System.out.println("trace:\n"+ruleResult.getTraceMap());
    }
    @Test
    public void testTrace() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        runner.addFunction("f", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                System.out.println(Arrays.toString(list));
                return null;
            }
        });
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("demo",new Demo());
        context.put("a",1);
        context.put("b",2);
        context.put("c",3);
        context.put("boolValue",true);
        context.put("trueValue",true);
    
        executeQl("if(demo.b){a=a+b;f();}",context,runner);
        executeQl("if(demo.b){a=a+b;return a+(a+(c+(a+b)));}",context,runner);
        executeQl("if(1+2==3 and boolValue or b>3){'OK';}",context,runner);
        executeQl("if(1+2==3 and (boolValue or b>3)){'OK';}",context,runner);
        executeQl("if(1+2==3 and boolValue or b>3){'OK';}else if(1+2==3 and boolValue or b>3){'OK';}",context,runner);
        executeQl("when boolValue then return 'ok';",context,runner);
        executeQl("when boolValue  then  'ok' else when trueValue then  'shit'",context,runner);
        executeQl("when 1+2>3  then  'ok' else when 1+2==3 then  'ok2' else when 1+2==3 then  'ok3'",context,runner);

        context.put("name","小明");
        context.put("age",12);
        context.put("score",150);
        executeQl("if(name.equals('小明') and age>10 and score>100)\n{\nSystem.out.println('好学生');\nreturn '小明是个好学生';\n}",context,runner);
    }

    void executeQl(String express,DefaultContext<String, Object> context,ExpressRunner runner) throws Exception {
        RuleResult ruleResult= runner.executeRule(express, context, true, false);
        System.out.println("express:\n"+express);
        System.out.println("result:\n"+ruleResult.getResult());
        System.out.println("tree:\n"+ruleResult.getRule().toTree());
        System.out.println("trace:\n"+ruleResult.getTraceMap());

    }
}
