package com.ql.util.express.config;

import com.ql.util.express.exception.QLTimeoutException;

/**
 * @author tianqiao@taobao.com
 * @since 2019/6/17 4:12 PM
 */
public class QLExpressTimer {

    private static long globalTimeoutMillis = -1;

    /**
     * 设置全局脚本超时时间, 默认 -1, 表示不限制时间
     *
     * @param timeoutMillis 超时时间
     * @deprecated 原 api 命名不合理, 推荐替换为 {@link #setTimeout(long)}
     */
    @Deprecated
    public static void setTimer(long timeoutMillis) {
        globalTimeoutMillis = timeoutMillis;
    }

    /**
     * 设置全局脚本超时时间, 默认 -1, 表示不限制时间
     *
     * @param timeoutMillis 超时时间
     * @since 3.3.3
     */
    public static void setTimeout(long timeoutMillis) {
        globalTimeoutMillis = timeoutMillis;
    }

    public static long getTimeout() {
        return globalTimeoutMillis;
    }
}
