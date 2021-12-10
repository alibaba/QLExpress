package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.DynamicParamsUtil;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * tianqiao
 * 2016-09-12
 */
public class TestMethodParams {

    @Test
    public void testMethodDynamicDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new DefaultContext<>();

        //(1)默认的不定参数可以使用数组来代替
        Object r = runner.execute(
            "a = new com.ql.util.express.test.TestMethodParams();a.getTemplate([11,'22',33L,true])", expressContext,
            null, false, false);
        System.out.println(r);
        //(2)目前只支持只有Object[]一个参数的这种情况
        Object r2 = runner.execute(
            "a = new com.ql.util.express.test.TestMethodParams();a.getTemplate(11,'22',33L,true)", expressContext, null,
            false, false);
        System.out.println(r2);
    }

    @Test
    public void testDynamicDemo() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        runner.addFunctionOfServiceMethod("getTemplate", this, "getTemplate", new Class[] {Object[].class}, null);
        runner.addFunctionOfClassMethod("getTemplateStatic", TestMethodParams.class.getName(), "getTemplateStatic",
            new Class[] {Object.class, String[].class}, null);
        //(1)默认的不定参数可以使用数组来代替
        Object r = runner.execute("getTemplate([11,'22',33L,true])", expressContext, null, false, false);
        System.out.println(r);
        //(2)像java一样,支持函数动态参数调用,需要打开以下全局开关,否则以下调用会失败
        DynamicParamsUtil.supportDynamicParams = true;
        r = runner.execute("getTemplate(11,'22',33L,true)", expressContext, null, false, false);
        System.out.println(r);
        r = runner.execute("getTemplateStatic('22','33','44')", expressContext, null, false, false);
        System.out.println(r);
    }

    //等价于getTemplate(Object[] params)
    public Object getTemplate(Object... params) {
        String result = "";
        for (Object obj : params) {
            result = result + obj + ",";
        }
        return result;
    }

    public static Object getTemplateStatic(Object a, String... params) {
        String result = "";
        for (Object obj : params) {
            result = result + obj + ",";
        }
        return result;
    }

    //等价于Integer[] params
    public Integer integerArrayInvoke(Integer... params) {
        if (params == null) {
            return 0;
        }
        Integer result = 0;
        for (Integer obj : params) {
            if (obj != null) {
                result = result + obj;
            }
        }
        System.out.println(result);
        return result;
    }

    //等价于Object[] params
    public Object objectArrayInvoke(Object... params) {
        if (params == null) {
            return "";
        }
        String result = "";
        for (Object obj : params) {
            if (obj != null) {
                result = result + obj + ",";
            }
        }
        System.out.println(result);
        return result;
    }

    //等价于Integer[] params,重载objectArrayInvoke
    public Object objectArrayInvoke(Integer... params) throws Exception {
        return this.integerArrayInvoke(params);
    }

    //带同步的不定参数
    public Object objectArrayInvokeWithHead(String head, Integer... params) throws Exception {
        return head + this.integerArrayInvoke(params);
    }

    @Test
    public void testMethodArrayParamType() throws Exception {

        Class<?> longClass = Long.class;
        Class<?> numberClass = Number.class;
        Class<?> stringClass = String.class;
        Class<?> objectClass = Object.class;

        assert (numberClass.isAssignableFrom(longClass));
        assert (objectClass.isAssignableFrom(numberClass));
        assert (objectClass.isAssignableFrom(stringClass));

        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        runner.addFunctionOfServiceMethod("integerArrayInvoke", this, "integerArrayInvoke",
            new Class[] {Integer[].class}, null);
        runner.addFunctionOfServiceMethod("objectArrayInvokeWithHead", this, "objectArrayInvokeWithHead",
            new Class[] {String.class, Integer[].class}, null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke", this, "objectArrayInvoke", new Class[] {Object[].class},
            null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke_Integer", this, "objectArrayInvoke",
            new Class[] {Integer[].class}, null);

        testInvoke("integerArrayInvoke([1,2,3,4])", "10", runner, expressContext);
        //null测试
        testInvoke("integerArrayInvoke(null)", "0", runner, expressContext);
        testInvoke("integerArrayInvoke([null,1,2,3,null,4])", "10", runner, expressContext);
        testInvoke("integerArrayInvoke([null,null])", "0", runner, expressContext);
        testInvoke("objectArrayInvoke([null,null])", "", runner, expressContext);

        //重载测试
        testInvoke("objectArrayInvoke([1,2,3,null,4])", "1,2,3,4,", runner, expressContext);
        testInvoke("objectArrayInvoke_Integer([1,2,3,null,4])", "10", runner, expressContext);
        //需要把数组统一成Object
        testInvoke("objectArrayInvoke(['1',2,3,null,4])", "1,2,3,4,", runner, expressContext);

        //带有head
        testInvoke("objectArrayInvokeWithHead('hello:',[1,2,3,null,4])", "hello:10", runner, expressContext);
    }

    @Test
    public void testDynamicParams() throws Exception {

        DynamicParamsUtil.supportDynamicParams = true;

        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> expressContext = new DefaultContext<>();
        runner.addFunctionOfServiceMethod("integerArrayInvoke", this, "integerArrayInvoke",
            new Class[] {Integer[].class}, null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke", this, "objectArrayInvoke", new Class[] {Object[].class},
            null);
        runner.addFunctionOfServiceMethod("objectArrayInvoke_Integer", this, "objectArrayInvoke",
            new Class[] {Integer[].class}, null);
        runner.addFunctionOfServiceMethod("objectArrayInvokeWithHead", this, "objectArrayInvokeWithHead",
            new Class[] {String.class, Integer[].class}, null);

        testInvoke("integerArrayInvoke()", "0", runner, expressContext);
        testInvoke("integerArrayInvoke(null)", "0", runner, expressContext);
        testInvoke("integerArrayInvoke(1)", "1", runner, expressContext);
        testInvoke("integerArrayInvoke(1,2,3,4)", "10", runner, expressContext);
        //null测试
        testInvoke("integerArrayInvoke(null,1,2,3,null,4)", "10", runner, expressContext);
        testInvoke("integerArrayInvoke(null,null)", "0", runner, expressContext);
        testInvoke("objectArrayInvoke(null,null)", "", runner, expressContext);

        //重载测试
        testInvoke("objectArrayInvoke()", "", runner, expressContext);
        testInvoke("objectArrayInvoke(null)", "", runner, expressContext);
        testInvoke("objectArrayInvoke(null,1,2,3,4)", "1,2,3,4,", runner, expressContext);
        testInvoke("objectArrayInvoke(1,2,3,null,4)", "1,2,3,4,", runner, expressContext);
        testInvoke("objectArrayInvoke_Integer(1,2,3,null,4)", "10", runner, expressContext);
        //需要把数组统一成Object
        testInvoke("objectArrayInvoke('1',2,3,null,4)", "1,2,3,4,", runner, expressContext);
        //带有head
        testInvoke("objectArrayInvokeWithHead('hello:',1,2,3,null,4)", "hello:10", runner, expressContext);
    }

    void testInvoke(String text, String expert, ExpressRunner runner, IExpressContext<String, Object> expressContext)
        throws Exception {

        Object r = runner.execute(text, expressContext, null, false, false);
        assert (r.toString().equals(expert));
    }
}
