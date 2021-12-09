package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class IfTest {
    @Test
    public void testIf() throws Exception {
        String[][] expresses = new String[][] {
            {"if 1==1 then return 100   else return 10;", "100"},
            {"if 1==2 then return 100   else return 10;", "10"},
            {"if 1==1 then return 100;  else return 10;", "100"},
            {"if 1==2 then return 100;  else return 10;", "10"},
            {"if 1==1 then {return 100}  else {return 10;}", "100"},
            {"if 1==2 then {return 100}  else {return 10;}", "10"},
            {"if 1==1 then return 100 ; return 10000;", "100"},
            {"if 1==2 then return 100; return 10000;", "10000"},
            {"if (1==1)  return 100   else return 10;", "100"},
            {"if (1==2)  return 100   else return 10;", "10"},
            {"if (1==1)  return 100;  else return 10;", "100"},
            {"if (1==2)  return 100;  else return 10;", "10"},
            {"if (1==1)  {return 100}  else {return 10;}", "100"},
            {"if (1==2)  {return 100}  else {return 10;}", "10"},
            {"if (1==1) return 100 ; return 10000;", "100"},
            {"if (1==2) return 100; return 10000;", "10000"},
        };
        for (int i = 0; i < expresses.length; i++) {
            IExpressContext<String, Object> expressContext = new DefaultContext<>();
            ExpressRunner runner = new ExpressRunner(false, true);
            runner.addOperatorWithAlias("加", "+", null);
            runner.addOperator("love", "+", new LoveOperator("love"));
            Object result = runner.execute(expresses[i][0], expressContext, null, false, true);
            System.out.println("运算结果：" + result);
            System.out.println("环境结果：" + expressContext);
            Assert.assertEquals("表达式执行错误:" + expresses[i][0] + " 期望值：" + expresses[i][1] + " 运算结果：" + result,
                expresses[i][1], result == null ? "null" : result.toString());
        }
    }
}
