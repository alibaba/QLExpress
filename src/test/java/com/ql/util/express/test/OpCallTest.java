package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

public class OpCallTest {
    @Test
    public void testList() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addOperator("@love", new LoveOperator("@love"));
        runner.loadMultiExpress(null, "function abc(String s){println(s)}");
        runner.addOperatorWithAlias("打印", "println", null);
        runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(), "isVIP", new Class[] {String.class}, "");
        runner.addOperatorWithAlias("是否VIP", "isVIP", "亲爱的$1,你还不是VIP用户");

        String[][] expressTest = new String[][] {
            {"println \"ssssss\"", "null"},
            {"println (\"ssssss\")", "null"},
            {"abc (\"bbbbbbbb\")", "null"},
            {"打印 (\"函数别名测试\")", "null"},
            {"isVIP (\"玄难\")", "false"},
            {"是否VIP (\"玄难\")", "false"},
        };
        IExpressContext<String, Object> expressContext = new ExpressContextExample(null);

        for (int point = 0; point < expressTest.length; point++) {
            String expressStr = expressTest[point][0];
            List<String> errorList = new ArrayList<>();
            Object result = runner.execute(expressStr, expressContext, errorList, false, true);
            if (result == null && !expressTest[point][1].equalsIgnoreCase("null")
                || expressTest[point][1].equalsIgnoreCase("null") && result != null
                || result != null && !expressTest[point][1].equalsIgnoreCase(result.toString())) {
                throw new Exception(
                    "处理错误,计算结果与预期的不匹配:" + expressStr + " = " + result + "但是期望值是：" + expressTest[point][1]);
            }
            System.out.println("Example " + point + " : " + expressStr + " =  " + result);
            if (errorList.size() > 0) {
                System.out.println("\t\t系统输出的错误提示信息:" + errorList);
            }
        }
    }
}
