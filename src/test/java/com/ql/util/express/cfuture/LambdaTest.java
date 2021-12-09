package com.ql.util.express.cfuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.QLambda;
import org.junit.Assert;
import org.junit.Test;

public class LambdaTest {

    @Test
    public void lambdaAddTest() throws Exception {
        String express = "la = (a,b)->a+b; la.apply(1,2)";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

        Object res = runner.execute(express, context, null, true, true);
        Assert.assertEquals(3, res);
    }

    @Test
    public void lambdaOperatorTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addOperator("map", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                List<?> origin = (List<?>)list[0];
                QLambda qLambda = (QLambda)list[1];
                List result = new ArrayList();
                for (int i = 0; i < origin.size(); i++) {
                    result.add(qLambda.call(origin.get(i), i));
                }
                return result;
            }
        });

        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("l", Arrays.asList("a", "b", "c"));
        Object res = runner.execute("l map (item, index)->item+index", context, null,
            true, true);
        Assert.assertEquals(Arrays.asList("a0", "b1", "c2"), res);

        Object res1 = runner.execute("l map (item,index)->item+index", context,
            null, true, true);
        Assert.assertEquals(Arrays.asList("a0", "b1", "c2"), res1);
    }

    @Test
    public void streamTest() throws Exception {

        List<Integer> a = Arrays.asList(1, 2, 3).stream().filter(item -> item > 1).map(item -> item + 1).collect(
            Collectors.toList());
        a.stream().forEach(item -> System.out.println(item));
        Assert.assertEquals(Arrays.asList(3, 4), a);

        String expr = "a = NewList(1,2,3).stream()" +
            ".filter(item -> item > 1)" +
            ".map(item->item+1)" +
            ".collect(Collectors.toList());" +
            "a.stream().forEach(item->System.out.println(item));" +
            "return a;";
        ExpressRunner runner = new ExpressRunner(false, true);
        Object res = runner.execute(expr, new DefaultContext<>(), null,
            false, false);
        Assert.assertEquals(Arrays.asList(3, 4), res);
    }

    @Test
    public void foreachTest() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");
        map.put("d", "d");
        map.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });

        List<Integer> list = Arrays.asList(1, 2, 3);
        list.forEach(item -> {
            System.out.println(item);
        });

        String expr = "map.forEach((k, v) -> {\n" +
            "            System.out.println(k);\n" +
            "            System.out.println(v);\n" +
            "        });";
        String expr2 = "list.forEach(item ->{\n" +
            "            System.out.println(item);\n" +
            "        });";
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("map", map);
        context.put("list", list);
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.execute(expr, context, null,
            false, false);
        runner.execute(expr2, context, null,
            false, false);
    }
}
