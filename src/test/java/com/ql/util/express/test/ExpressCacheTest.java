package com.ql.util.express.test;

import java.util.Date;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRemoteCacheRunner;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.LocalExpressCacheRunner;
import org.junit.Test;

/**
 * 关于ExpressRunner的脚本缓存管理方案
 *
 * @author tianqiao
 */
public class ExpressCacheTest {

    ExpressRunner runner = new ExpressRunner();

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
            calulateTask(false, context);
        }
        long end = new Date().getTime();
        echo("不做缓存耗时：" + (end - start) + " ms");

        times = 10000;
        start = new Date().getTime();
        while (times-- > 0) {
            calulateTask(true, context);
        }
        end = new Date().getTime();
        echo("做缓存耗时：" + (end - start) + " ms");
    }

    @Test
    public void testLocalCacheMutualImpact() throws Exception {

        //缓存在本地的脚本都是全局的，可以相互调用

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

    private void calulateTask(boolean isCache, IExpressContext<String, Object> context) throws Exception {
        runner.execute("计算平均成绩", context, null, isCache, false);
    }

}
