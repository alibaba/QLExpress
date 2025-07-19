package com.alibaba.qlexpress4;

import java.util.Collections;
import java.util.Map;

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
     * default false
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
    
    /**
     * Track the execution process of all expressions and return the path to the `execute` caller.
     * To enable expression tracing, please ensure that the InitOptions.traceExpression is alse set to true.
     * default false
     */
    private final boolean traceExpression;
    
    /**
     * disable short circuit in logic operator
     * default false
     */
    private final boolean shortCircuitDisable;
    
    private QLOptions(boolean precise, boolean polluteUserContext, long timeoutMillis, Map<String, Object> attachments,
        boolean cache, boolean avoidNullPointer, int maxArrLength, boolean traceExpression,
        boolean shortCircuitDisable) {
        this.precise = precise;
        this.polluteUserContext = polluteUserContext;
        this.timeoutMillis = timeoutMillis;
        this.attachments = attachments;
        this.cache = cache;
        this.avoidNullPointer = avoidNullPointer;
        this.maxArrLength = maxArrLength;
        this.traceExpression = traceExpression;
        this.shortCircuitDisable = shortCircuitDisable;
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
    
    public boolean isTraceExpression() {
        return traceExpression;
    }
    
    public boolean isShortCircuitDisable() {
        return shortCircuitDisable;
    }
    
    public static class Builder {
        private boolean precise = false;
        
        private boolean polluteUserContext = false;
        
        private long timeoutMillis = -1;
        
        private Map<String, Object> attachments = Collections.emptyMap();
        
        private boolean cache = false;
        
        private boolean avoidNullPointer = false;
        
        private int maxArrLength = -1;
        
        private boolean traceExpression = false;
        
        private boolean shortCircuitDisable = false;
        
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
        
        public Builder traceExpression(boolean traceExpression) {
            this.traceExpression = traceExpression;
            return this;
        }
        
        public Builder shortCircuitDisable(boolean shortCircuitDisable) {
            this.shortCircuitDisable = shortCircuitDisable;
            return this;
        }
        
        public QLOptions build() {
            return new QLOptions(precise, polluteUserContext, timeoutMillis, attachments, cache, avoidNullPointer,
                maxArrLength, traceExpression, shortCircuitDisable);
        }
    }
}
