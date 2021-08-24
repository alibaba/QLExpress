package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.QLambda;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LambdaTest {

    @Test
    public void lambdaAddTest() throws Exception {
        String express = "la = (a,b)->a+b; la.apply(1,2)";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();

        Object res = runner.execute(express, context, null, true, true);
        Assert.assertEquals(3, res);
    }

    @Test
    public void lambdaOperatorTest() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addOperator("map", new Operator() {
            @Override
            public Object executeInner(Object[] list) throws Exception {
                List<?> origin = (List<?>) list[0];
                QLambda qLambda = (QLambda) list[1];
                List result = new ArrayList();
                for (int i = 0; i < origin.size(); i++) {
                    result.add(qLambda.call(origin.get(i), i));
                }
                return result;
            }
        });

        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
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


        List<Integer> a= Arrays.asList(1,2,3).stream().filter(item->item>1).map(item->item+1).collect(Collectors.toList());
        a.stream().forEach(item->System.out.println(item));
        Assert.assertEquals(Arrays.asList(3, 4), a);

        String expr = "a = NewList(1,2,3).stream()" +
                ".filter(item -> item > 1)" +
                ".map(item->item+1)" +
                ".collect(Collectors.toList());" +
                "a.stream().forEach(item->System.out.println(item));" +
                "return a;";
        ExpressRunner runner = new ExpressRunner(false, true);
        Object res = runner.execute(expr, new DefaultContext<String, Object>(), null,
                false, false);
        Assert.assertEquals(Arrays.asList(3, 4), res);
    }

}
