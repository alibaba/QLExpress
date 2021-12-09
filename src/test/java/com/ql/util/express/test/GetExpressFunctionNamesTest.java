package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.junit.Assert;
import org.junit.Test;

public class GetExpressFunctionNamesTest {

    public Object fun3(Object a, Object b) {
        return "" + a + b;
    }

    @Test
    public void testFunctionDefine() throws Exception {
        String express
            = "function fun1(){return null;} function fun2(int a,int b,int c){return a*b+c;} a =fun1();b=fun2(1,2,3)"
            + "+fun3(1,2);";
        ExpressRunner runner = new ExpressRunner(false, true);

        String[] names = runner.getOutFunctionNames(express);
        for (String s : names) {
            System.out.println("function : " + s);
        }
        Assert.assertTrue("获取外部方法错误", names.length == 1);
        Assert.assertTrue("获取外部方法错误", names[0].equalsIgnoreCase("fun3"));

        //注意:
        // 如果已经通过function或者函数绑定定义了fun3, fun3就会变成一个普通的operator,这个接口就不会返回fun3函数定义

        //(1)自定义function
        runner = new ExpressRunner(true, true);
        runner.addFunctionOfServiceMethod("fun3", this, "fun3", new Class[] {Object.class, Object.class}, null);

        names = runner.getOutFunctionNames(express);
        for (String s : names) {
            System.out.println("function : " + s);
        }
        Assert.assertTrue("获取外部方法错误", names.length == 0);

        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object r = runner.execute(express, context, null, false, false);
        System.out.println("result : " + r);

        //(2)函数绑定function
        runner = new ExpressRunner(true, true);
        runner.addFunction("fun3", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                String s = "";
                for (Object obj : list) {
                    s = s + obj;
                }
                return s;
            }
        });

        names = runner.getOutFunctionNames(express);
        for (String s : names) {
            System.out.println("function : " + s);
        }
        Assert.assertTrue("获取外部方法错误", names.length == 0);

        context = new DefaultContext<String, Object>();
        r = runner.execute(express, context, null, false, false);
        System.out.println("result : " + r);

    }
}
