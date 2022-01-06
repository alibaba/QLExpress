package com.ql.util.express.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.Operator;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Created by tianqiao on 16/10/17.
 */
public class AddMethodInvokeTest {
    @Test
    public void testStringMethod() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute("'helloWorld'.length()", context, null, false, false);
        System.out.println(result);

        runner.addFunctionAndClassMethod("isBlank", Object.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                String str = (String)list[0];
                return str.trim().length() == 0;
            }
        });
        runner.addFunctionAndClassMethod("isNotBlank", String.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                String str = (String)list[0];
                return str.trim().length() > 0;
            }
        });
        result = runner.execute("isBlank('\t\n')", context, null, false, false);
        assert ((Boolean)result);
        result = runner.execute("'\t\n'.isBlank()", context, null, false, false);
        assert ((Boolean)result);
        result = runner.execute("isNotBlank('helloworld')", context, null, false, false);
        assert ((Boolean)result);
        result = runner.execute("'helloworld'.isNotBlank()", context, null, false, false);
        assert ((Boolean)result);
    }

    @Test
    public void testArrayOrMapJoinMethod() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();

        runner.addClassMethod("join", List.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                ArrayList arrayList = (ArrayList)list[0];
                return StringUtils.join(arrayList, (String)list[1]);
            }
        });
        runner.addClassMethod("join", Map.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                HashMap map = (HashMap)list[0];
                StringBuilder sb = new StringBuilder();
                for (Object key : map.keySet()) {
                    sb.append(key).append("=").append(map.get(key)).append((String)list[1]);
                }
                return sb.substring(0, sb.length() - 1);
            }
        });
        String express = "list=new ArrayList();list.add(1);list.add(2);list.add(3);return list.join(' , ');";
        Object result = runner.execute(express, context, null, false, false);
        System.out.println(result);

        express = "list=new HashMap();list.put('a',1);list.put('b',2);list.put('c',3);return list.join(' , ');";
        result = runner.execute(express, context, null, false, false);
        System.out.println(result);
    }

    @Test
    public void testAop() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();

        runner.addClassMethod("size", List.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                ArrayList arrayList = (ArrayList)list[0];
                System.out.println("拦截到List.size()方法");
                return arrayList.size();
            }
        });

        runner.addClassField("长度", List.class, new Operator() {
            @Override
            public Object executeInner(Object[] list) {
                ArrayList arrayList = (ArrayList)list[0];
                System.out.println("拦截到List.长度 字段的计算");
                return arrayList.size();
            }
        });
        String express = "list=new ArrayList();list.add(1);list.add(2);list.add(3);return list.size();";
        Object result = runner.execute(express, context, null, false, false);
        System.out.println(result);

        express = "list=new ArrayList();list.add(1);list.add(2);list.add(3);return list.长度;";
        result = runner.execute(express, context, null, false, false);
        System.out.println(result);

        //bugfix 没有return 的时候可能会多次调用getType，并且返回错误
        express = "list=new ArrayList();list.add(1);list.add(2);list.add(3);list.长度;";
        Object result2 = runner.execute(express, context, null, false, false);
        System.out.println(result2);
    }
}
