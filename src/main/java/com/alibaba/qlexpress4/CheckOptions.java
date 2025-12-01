package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.operator.OperatorCheckStrategy;

/**
 * Script validation configuration class
 * Used to configure restriction rules during script validation
 *
 * @author QLExpress Team
 */
public class CheckOptions {
    
    /**
     * Operator check strategy for script validation
     * default OperatorCheckStrategy.allowAll()
     */
    private final OperatorCheckStrategy operatorCheckStrategy;
    
    /**
     * Whether to disable function calls in the script
     * default false
     */
    private final boolean disableFunctionCalls;
    
    /**
     * Default validation options
     */
    public static final CheckOptions DEFAULT_OPTIONS = CheckOptions.builder().build();
    
    private CheckOptions(OperatorCheckStrategy operatorCheckStrategy, boolean disableFunctionCalls) {
        this.operatorCheckStrategy = operatorCheckStrategy;
        this.disableFunctionCalls = disableFunctionCalls;
    }
    
    public OperatorCheckStrategy getCheckStrategy() {
        return operatorCheckStrategy;
    }
    
    public boolean isDisableFunctionCalls() {
        return disableFunctionCalls;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private OperatorCheckStrategy operatorCheckStrategy = OperatorCheckStrategy.allowAll();
        private boolean disableFunctionCalls = false;
        
        private Builder() {
        }
        
        public Builder operatorCheckStrategy(OperatorCheckStrategy operatorCheckStrategy) {
            this.operatorCheckStrategy = operatorCheckStrategy;
            return this;
        }
        
        public Builder disableFunctionCalls(boolean disableFunctionCalls) {
            this.disableFunctionCalls = disableFunctionCalls;
            return this;
        }
        
        public CheckOptions build() {
            return new CheckOptions(operatorCheckStrategy, disableFunctionCalls);
        }
    }
}
