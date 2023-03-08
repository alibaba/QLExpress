package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

public class ForFlowFunctionTest {

    @Test
    public void testABC() throws Exception {
        String express = ""
                + "for(i = 0; i < 1; i = i + 1){"
                + "    打印(70);"
                + "}"
                + "打印(70);"
                + "return 10;";
        ExpressRunner runner = new ExpressRunner(false, true);
        runner.addFunctionOfServiceMethod("打印", System.out, "println", new String[]{"int"}, null);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("for循环后面跟着一个函数的时候错误", "10", r.toString());
    }

    @Test
    public void testEnhancedForLoopWithArray() throws Exception {
        String express = ""
                + "sum = 0;"
                + "for(v : arr){"
                + "    sum = sum + v;"
                + "}"
                + "return sum;";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Integer[] array = new Integer[]{1, 2, 3, 4, 5};
        context.put("arr", array);
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("增强for循环遍历数组出错", "15", r.toString());
    }

    @Test
    public void testEnhancedForLoopWithList() throws Exception {
        String express = ""
                + "sum = 0;"
                + "for(v : list){"
                + "    sum = sum + v;"
                + "}"
                + "return sum;";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        context.put("list", list);
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("增强for循环遍历列表出错", "15", r.toString());
    }

    @Test
    public void testEnhancedForLoopWithSet() throws Exception {
        String express = ""
                + "sum = 0;"
                + "for(v : set){"
                + "    sum = sum + v;"
                + "}"
                + "return sum;";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);
        context.put("set", set);
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("增强for循环遍历集合出错", "15", r.toString());
    }

    @Test
    public void testEnhancedForLoopWithCustomIterable() throws Exception {
        String express = ""
                + "sum = 0;"
                + "for(v : iterable){"
                + "    sum = sum + v;"
                + "}"
                + "return sum;";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        Iterable<Integer> iterable = new DelegatedIterable<>(list);
        context.put("iterable", iterable);
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("增强for循环遍历自定义对象出错", "15", r.toString());
    }

    @Test
    public void testEnhancedForLoopWithBuiltinArray() throws Exception {
        String express = ""
                + "sum = 0;"
                + "arr = [1, 2, 3, 4, 5];"
                + "for(v : arr){"
                + "    sum = sum + v;"
                + "}"
                + "return sum;";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = runner.execute(express, context, null, false, true);
        Assert.assertEquals("增强for循环遍历数组出错", "15", r.toString());
    }

    static class DelegatedIterable<T> implements Iterable<T> {
        private final Iterable<T> delegated;

        DelegatedIterable(Iterable<T> delegated) {
            this.delegated = delegated;
        }

        @Override
        public Iterator<T> iterator() {
            return delegated.iterator();
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            delegated.forEach(action);
        }
    }
}
