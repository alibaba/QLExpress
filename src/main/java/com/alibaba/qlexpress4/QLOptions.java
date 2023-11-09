package com.alibaba.qlexpress4;

import java.util.Collections;
import java.util.Map;

import com.alibaba.qlexpress4.security.QLSecurityStrategy;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:35 下午
 */
public class QLOptions {

    public static QLOptions DEFAULT_OPTIONS = QLOptions.builder().build();

    /**
     * precise evaluate based on BigDecimal
     * default false
     */
    private final boolean precise;

    /**
     * define global symbol in user context
     * default false
     */
    private final boolean polluteUserContext;

    /**
     * script timeout millisecond, default is -1, namely time unlimited
     * <= 0, time unlimited
     * default -1
     */
    private final long timeoutMillis;

    /**
     * attachments will be carried to user defined function/operator/macro
     * only used to pass data, not as variable value
     *
     * default empty map
     */
    private final Map<String, Object> attachments;

    /**
     * allow cache compile result of script
     *
     * default true
     */
    private final boolean cache;

    /**
     * avoid null pointer
     * default false
     */
    private final boolean avoidNullPointer;

    /**
     * max length of arrays allowed to be created
     * -1 means no limit
     * default -1
     */
    private final int maxArrLength;

    private QLOptions(boolean precise, boolean polluteUserContext, long timeoutMillis,
                      Map<String, Object> attachments, boolean cache, boolean avoidNullPointer,
                      int maxArrLength) {
        this.precise = precise;
        this.polluteUserContext = polluteUserContext;
        this.timeoutMillis = timeoutMillis;
        this.attachments = attachments;
        this.cache = cache;
        this.avoidNullPointer = avoidNullPointer;
        this.maxArrLength = maxArrLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPrecise() {
        return precise;
    }

    public boolean isPolluteUserContext() {
        return polluteUserContext;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public boolean isCache() {
        return cache;
    }

    public boolean isAvoidNullPointer() {
        return avoidNullPointer;
    }

    public int getMaxArrLength() {
        return maxArrLength;
    }

    /**
     * @param newArrLen new arr length in runtime
     * @return true if less or equal to max arr len
     */
    public boolean checkArrLen(int newArrLen) {
        return maxArrLength == -1 || newArrLen <= maxArrLength;
    }

    public static class Builder {
        private boolean precise = false;

        private boolean polluteUserContext = false;

        private long timeoutMillis = -1;

        private Map<String, Object> attachments = Collections.emptyMap();

        private boolean cache = true;

        private boolean avoidNullPointer = false;

        private int maxArrLength = -1;

        public Builder precise(boolean precise) {
            this.precise = precise;
            return this;
        }

        public Builder polluteUserContext(boolean polluteUserContext) {
            this.polluteUserContext = polluteUserContext;
            return this;
        }

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public Builder attachments(Map<String, Object> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder cache(boolean cache) {
            this.cache = cache;
            return this;
        }

        public Builder avoidNullPointer(boolean avoidNullPointer) {
            this.avoidNullPointer = avoidNullPointer;
            return this;
        }

        public Builder maxArrLength(int maxArrLength) {
            this.maxArrLength = maxArrLength;
            return this;
        }

        public QLOptions build() {
            return new QLOptions(precise, polluteUserContext, timeoutMillis,
                attachments, cache, avoidNullPointer, maxArrLength);
        }
    }
}
