package com.ql.util.express.test;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRemoteCacheRunner;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.LocalExpressCacheRunner;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;

/**
 * 关于ExpressRunner的脚本缓存管理方案
 *
 * @author tianqiao
 */
public class ExpressCacheTest {
    private final ExpressRunner runner = new ExpressRunner();

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 25, 5L,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), Executors.defaultThreadFactory(),
        new AbortPolicy());

    /**
     * Single td invoke
     */
    @Test
    public void testScriptCache() throws Exception {
        runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("语文", 88);
        context.put("数学", 99);
        context.put("英语", 95);
        long times = 10000;
        long start = new Date().getTime();
        while (times-- > 0) {
            calculateTask(false, context);
        }
        long end = new Date().getTime();
        echo("不做缓存耗时：" + (end - start) + " ms");

        times = 10000;
        start = new Date().getTime();
        while (times-- > 0) {
            calculateTask(true, context);
        }
        end = new Date().getTime();
        echo("做缓存耗时：" + (end - start) + " ms");
    }

    /**
     * validate local cache not null
     * @throws Exception
     */
    @Test
    public void testOnInvokeCacheNotNull() throws Exception {
        Assert.assertTrue(Objects.nonNull(new ExpressRunner()
            .getExpressInstructionSetCache()));
        Assert.assertTrue(Objects.nonNull(new ExpressRunner(false, false,
            (ConcurrentHashMap<String, InstructionSet>)null)
            .getExpressInstructionSetCache()));
        Assert.assertTrue(Objects.nonNull(new ExpressRunner(false, false,
            new ConcurrentHashMap<>())
            .getExpressInstructionSetCache()));

    }

    /**
     * can use jmh for accurate statistics
     * avg output: 360ms ~ 400ms
     * @throws Exception
     */
    @Test
    public void testScriptWithConcurrentHashMap() throws Exception {
        runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");

        // set self define with eviction policy to avoid
        //runner.setWrapCacheMap(null);
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("语文", 88);
        context.put("数学", 99);
        context.put("英语", 95);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CountDownLatch cnt = new CountDownLatch(100);

        IntStream.range(0, 100)
            .forEach(i -> {
                executor.submit(() -> {
                    try {
                        testOnBatchInvokeSingleScriptCalc(context);
                        cnt.countDown();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        cnt.await();
        stopWatch.stop();
        Assert.assertTrue(executor.getCompletedTaskCount() == 100);
    }

    private void testOnBatchInvokeSingleScriptCalc(IExpressContext ctx) throws Exception {
        for (int i = 0; i < 10000; i ++) {
            // enable cache
            calculateTask(true, ctx);
        }
    }

    @Test
    public void testLocalCacheMutualImpact() throws Exception {
        // 缓存在本地的脚本都是全局的，可以相互调用
        runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
        runner.addMacro("是否优秀", "计算平均成绩>90");
        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("语文", 88);
        context.put("数学", 99);
        context.put("英语", 95);
        echo(runner.execute("是否优秀", context, null, false, false));
    }

    @Test
    public void testRemoteCache() {
        //数据的预先加载
        ExpressRunner runner = new ExpressRunner();
        ExpressRemoteCacheRunner cacheRunner = new LocalExpressCacheRunner(runner);
        cacheRunner.loadCache("计算平均成绩", "(语文+数学+英语)/3.0");
        cacheRunner.loadCache("是否优秀", "计算平均成绩>90");

        IExpressContext<String, Object> context = new DefaultContext<>();
        context.put("语文", 88);
        context.put("数学", 99);
        context.put("英语", 95);
        //ExpressRemoteCacheRunner都只能执行自己原有的脚本内容，而且相互之间隔离，保证最高的脚本安全性
        echo(cacheRunner.execute("计算平均成绩", context, null, false, false, null));
        try {
            echo(cacheRunner.execute("计算平均成绩>90", context, null, false, false, null));
        } catch (Exception e) {
            echo("ExpressRemoteCacheRunner只支持预先加载的脚本内容");
        }
        try {
            echo(cacheRunner.execute("是否优秀", context, null, false, false, null));
        } catch (Exception e) {
            echo("ExpressRemoteCacheRunner不支持脚本间的相互调用");
        }
    }

    private void echo(Object obj) {
        System.out.println(obj);
    }

    private void calculateTask(boolean isCache, IExpressContext<String, Object> context) throws Exception {
        runner.execute("计算平均成绩", context, null, isCache, false);
    }
}
