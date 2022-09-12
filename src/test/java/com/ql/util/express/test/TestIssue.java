package com.ql.util.express.test;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author TaoKan
 * @Date 2022/9/12 上午11:30
 */
@State(Scope.Benchmark)
public class TestIssue {
    public static final HashMap<Class<?>,Class<?>> IS_PRIM = new HashMap();

    {
        IS_PRIM.put(Void.class,void.class);
        IS_PRIM.put(Float.class,float.class);
        IS_PRIM.put(Integer.class,int.class);
        IS_PRIM.put(Double.class,double.class);
        IS_PRIM.put(Long.class,long.class);
        IS_PRIM.put(Character.class,char.class);
        IS_PRIM.put(Boolean.class,boolean.class);
        IS_PRIM.put(Short.class,short.class);
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public Class<?> testOri(){
        Class<?> clazz = Double.class;
        if (clazz.isPrimitive()) {
            return clazz;
        }
        return clazz;
    }
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public Class<?> testOriS(){
        Class<?> clazz = Double.class;
        if (IS_PRIM.containsKey(clazz)) {
            return clazz;
        }
        return clazz;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestIssue.class.getSimpleName())
//                .include(BenchMarkUsage.class.getSimpleName()+".*measureThroughput*")
                // 预热3轮
                .warmupIterations(3)
                // 度量5轮
                .measurementIterations(10)
                .forks(3)
                .build();

        new Runner(opt).run();
    }
}
