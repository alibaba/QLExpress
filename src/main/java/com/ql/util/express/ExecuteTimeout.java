package com.ql.util.express;

/**
 * Author: DQinYuan
 */
public class ExecuteTimeout {
    /**
     * 表示不限制时间的实例
     */
    public static final ExecuteTimeout NO_TIMEOUT = new ExecuteTimeout(-1);

    private final long timeoutMillis;

    private final long endTime;

    public ExecuteTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.endTime = timeoutMillis != -1 ? System.currentTimeMillis() + timeoutMillis : -1;
    }

    public boolean isExpired() {
        return endTime != -1 && System.currentTimeMillis() > endTime;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }
}
