package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class ExpressTest {

    @Test
    public void testDemo() throws Exception {
        String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
        ExpressRunner runner = new ExpressRunner();
        Object r = runner.execute(express, null, null, false, false);
        Assert.assertTrue("表达式计算", r.toString().equalsIgnoreCase("117"));
        System.out.println("表达式计算：" + express + " = " + r);
    }

    @Test
    public void test_10000次() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        String express = "10 * 10 + 1 + 2 * 3 + 5 * 2";
        int num = 100000;
        runner.execute(express, null, null, true, false);
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            runner.execute(express, null, null, true, false);
        }
        System.out.println("执行" + num + "次\"" + express + "\" 耗时："
            + (System.currentTimeMillis() - start));
    }

    @Test
    public void testExpress() throws Exception {
        ExpressRunner runner = new ExpressRunner();

        runner.addOperatorWithAlias("如果", "if", null);
        runner.addOperatorWithAlias("则", "then", null);
        runner.addOperatorWithAlias("否则", "else", null);

        runner.addOperator("love", new LoveOperator("love"));
        runner.addOperatorWithAlias("属于", "in", "用户$1不在允许的范围");
        runner.addOperatorWithAlias("myand", "and", "用户$1不在允许的范围");
        runner.addFunction("累加", new GroupOperator("累加"));
        runner.addFunction("group", new GroupOperator("group"));
        runner.addFunctionOfClassMethod("isVIP", BeanExample.class.getName(),
            "isVIP", new String[] {"String"}, "$1不是VIP用户");
        runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
            new String[] {"double"}, null);
        runner.addFunctionOfClassMethod("取绝对值TWO", Math.class.getName(), "abs",
            new Class[] {double.class}, null);
        runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),
            "upper", new String[] {"String"}, null);
        runner.addFunctionOfClassMethod("testLong", BeanExample.class.getName(),
            "testLong", new String[] {"long"}, null);
        String[][] expressTest = new String[][] {
            {"isVIP(\"qh\") ; isVIP(\"xuannan\"); return isVIP(\"qh\") ;", "false"},
            {"如果  三星卖家  则  'a' love 'b'  否则   'b' love 'd' ", "b{a}b"},
            {"when  三星卖家  then  'a' love 'b'  否则   'b' love 'd' ", "b{a}b"},
            {"int defVar = 100; defVar = defVar + 100;", "200"},
            {"int a=0; if false then a = 5 else  a=10+1 ; return a ", "11"},
            {" 3+ (1==2?4:3) +8", "14"},
            {" 如果  (true) 则 {2+2;} 否则 {20 + 20;} ", "4"},
            {"'AAAAAAA' +'-' + \"\" +'' + \"B\"", "AAAAAAA-B"},
            {"System.out.println(\"ss\")", "null"},
            {"unionName = new com.ql.util.express.test.BeanExample(\"张三\").unionName(\"李四\")",
                "张三-李四"},
            {"group(2,3,4)", "9"},
            {"取绝对值(-5.0)", "5.0"},
            {"取绝对值TWO(-10.0)", "10.0"},
            {"max(2,3,4,10)", "10"},
            {"max(2,-1)", "2"},
            {"max(3,2) + 转换为大写(\"abc\")", "3ABC"},
            {"c = 1000 + 2000", "3000"},
            {"b = 累加(1,2,3)+累加(4,5,6)", "21"},
            {"三星卖家 and 消保用户 ", "true"},
            {"new String(\"12345\").length()", "5"},
            {"'a' love 'b' love 'c' love 'd'", "d{c{b{a}b}c}d"},
            {"10 * (10 + 1) + 2 * (3 + 5) * 2", "142"},
            {"( 2  属于 (4,3,5)) or isVIP(\"qhlhl2010@gmail.com\") or  isVIP(\"qhlhl2010@gmail.com\")", "false"},
            {" 1!=1 and isVIP(\"qhlhl2010@gmail.com\")", "false"},
            {" 1==1 or isVIP(\"qhlhl2010@gmail.com\") ", "true"},
            {"abc == 1", "true"},
            {"2+2 in 2+2", "true"},
            {"true or null", "true"},
            {"null or true", "true"},
            {"null or null", "false"},

            {"true and null", "false"},
            {"null and true", "false"},
            {"null and null", "false"},

            {"'a' nor null", "a"},
            {"'a' nor 'b'", "a"},
            {" null nor null", "null"},
            {" null nor 'b'", "b"},

            {"testLong(abc)", "toString-long:1"},
            {"bean.testLongObject(abc)", "toString-LongObject:1"},

            {"sum=0;n=7.3;for(i=0;i<n;i=i+1){sum=sum+i;};sum;", "28"},
            {"0 in (16,50008090,9,8,7,50011397,50013864,28,1625,50006842,50020808,50020857,50008164,50020611,"
                + "50008163,50023804,50020332,27)", "false"}
        };
        IExpressContext<String, Object> expressContext = new ExpressContextExample(null);
        expressContext.put("b", new Integer(200));
        expressContext.put("c", new Integer(300));
        expressContext.put("d", new Integer(400));
        expressContext.put("bean", new BeanExample());
        expressContext.put("abc", 1L);
        expressContext.put("defVar", 1000);

        for (int point = 0; point < expressTest.length; point++) {
            String expressStr = expressTest[point][0];
            List<String> errorList = new ArrayList<>();
            Object result = runner.execute(expressStr, expressContext, null, false, true);
            if (expressTest[point][1].equalsIgnoreCase("null")
                && result != null
                || result != null
                && !expressTest[point][1].equalsIgnoreCase(result
                .toString())) {
                throw new Exception(
                    "处理错误,计算结果与预期的不匹配:" + expressStr + " = " + result + "但是期望值是：" + expressTest[point][1]);
            }
            System.out.println("Example " + point + " : " + expressStr + " =  " + result);
            if (errorList.size() > 0) {
                System.out.println("\t\t系统输出的错误提示信息:" + errorList);
            }
        }
        System.out.println(expressContext);
    }
}
