package com.ql.util.express.config;

import com.ql.util.express.exception.QLTimeOutException;

/**
 * @author tianqiao@taobao.com
 * @since 2019/6/17 4:12 PM
 */
public class QLExpressTimer {
    private static ThreadLocal<Boolean> NEED_TIMER = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<Long> TIME_OUT_MILLIS = new ThreadLocal<Long>() {};
    private static ThreadLocal<Long> START_TIME = new ThreadLocal<Long>() {};
    private static ThreadLocal<Long> END_TIME = new ThreadLocal<Long>() {};

    /**
     * 设置计时器
     *
     * @param timeoutMillis 超时时间
     */
    public static void setTimer(long timeoutMillis) {
        NEED_TIMER.set(true);
        TIME_OUT_MILLIS.set(timeoutMillis);
    }

    /**
     * 开始计时
     */
    public static void startTimer() {
        if (NEED_TIMER.get()) {
            long t = System.currentTimeMillis();
            START_TIME.set(t);
            END_TIME.set(t + TIME_OUT_MILLIS.get());
        }
    }

    /**
     * 断言是否超时
     *
     * @throws QLTimeOutException
     */
    public static void assertTimeOut() throws QLTimeOutException {
        if (NEED_TIMER.get() && System.currentTimeMillis() > END_TIME.get()) {
            throw new QLTimeOutException("运行QLExpress脚本的下一条指令将超过了限定时间:" + TIME_OUT_MILLIS.get() + "ms");
        }
    }

    public static boolean hasExpired() {
        return NEED_TIMER.get() && System.currentTimeMillis() > END_TIME.get();
    }

    public static void reset() {
        if (NEED_TIMER.get()) {
            START_TIME.remove();
            END_TIME.remove();
            NEED_TIMER.remove();
            TIME_OUT_MILLIS.remove();
        }
    }
}
