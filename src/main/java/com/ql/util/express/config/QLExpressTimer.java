package com.ql.util.express.config;

import com.ql.util.express.exception.QLTimeoutException;

/**
 * @author tianqiao@taobao.com
 * @since 2019/6/17 4:12 PM
 */
public class QLExpressTimer {
    private static final ThreadLocal<Boolean> NEED_TIMER = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Long> TIME_OUT_MILLIS = new ThreadLocal<Long>() {};
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<Long>() {};
    private static final ThreadLocal<Long> END_TIME = new ThreadLocal<Long>() {};

    private QLExpressTimer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 设置超时时间
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
            long currentTimeMillis = System.currentTimeMillis();
            START_TIME.set(currentTimeMillis);
            END_TIME.set(currentTimeMillis + TIME_OUT_MILLIS.get());
        }
    }

    /**
     * 断言是否超时
     *
     * @throws QLTimeoutException
     */
    public static void assertTimeOut() throws QLTimeoutException {
        if (NEED_TIMER.get() && System.currentTimeMillis() > END_TIME.get()) {
            throw new QLTimeoutException("运行QLExpress脚本的下一条指令将超过限定时间:" + TIME_OUT_MILLIS.get() + "ms");
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
