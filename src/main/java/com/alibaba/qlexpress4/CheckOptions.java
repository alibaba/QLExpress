package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.operator.OperatorCheckStrategy;

/**
 * Script validation configuration class
 * Used to configure operator restriction rules during script validation
 *
 * @author QLExpress Team
 */
public class CheckOptions {
    
    private final OperatorCheckStrategy operatorCheckStrategy;
    
    public static final CheckOptions DEFAULT_OPTIONS = CheckOptions.builder().build();
    
    private CheckOptions(OperatorCheckStrategy operatorCheckStrategy) {
        this.operatorCheckStrategy = operatorCheckStrategy;
    }
    
    public OperatorCheckStrategy getCheckStrategy() {
        return operatorCheckStrategy;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private OperatorCheckStrategy operatorCheckStrategy = OperatorCheckStrategy.allowAll();
        
        private Builder() {
        }
        
        public Builder operatorCheckStrategy(OperatorCheckStrategy operatorCheckStrategy) {
            this.operatorCheckStrategy = operatorCheckStrategy;
            return this;
        }
        
        public CheckOptions build() {
            return new CheckOptions(operatorCheckStrategy);
        }
    }
}
