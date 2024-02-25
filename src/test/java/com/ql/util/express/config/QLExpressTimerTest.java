package com.ql.util.express.config;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.exception.QLTimeoutException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Author: DQinYuan
 */
public class QLExpressTimerTest {

    private static long OLD_TIMER;

    @BeforeClass
    public static void before() {
        OLD_TIMER = QLExpressTimer.getTimeout();
        QLExpressTimer.setTimeout(20);
    }

    @AfterClass
    public static void after() {
        QLExpressTimer.setTimeout(OLD_TIMER);
    }

    @Test
    public void timerTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.execute("Thread.sleep(5);a=1", context, null, true, false);
        runner.execute("Thread.sleep(18);a=5", context, null, true, false);
    }

    @Test(expected = QLTimeoutException.class)
    public void timerTimeoutTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.execute("Thread.sleep(21);a=1", context, null, true, false);
    }

    /**
     * execute 传入参数的超时时间优先
     */
    @Test(expected = QLTimeoutException.class)
    public void paramFirstTest() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        DefaultContext<String, Object> context = new DefaultContext<>();
        runner.execute("Thread.sleep(5);a=1", context, null, true, false, 3);
    }
}