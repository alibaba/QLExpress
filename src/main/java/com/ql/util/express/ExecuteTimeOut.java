package com.ql.util.express;

/**
 * Author: DQinYuan
 */
public class ExecuteTimeOut {
    /**
     * 表示不限制时间的实例
     */
    public static final ExecuteTimeOut NO_TIMEOUT = new ExecuteTimeOut(-1);

    private final long timeoutMillis;

    private final long endTime;

    public ExecuteTimeOut(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.endTime = timeoutMillis != -1? System.currentTimeMillis() + timeoutMillis: -1;
    }

    public boolean isExpired() {
        return endTime != -1 && System.currentTimeMillis() > endTime;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }
}
