package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.operator.OperatorCheckStrategy;

import java.util.Set;

/**
 * Script validation configuration class
 * Used to configure operator restriction rules during script validation
 *
 * @author QLExpress Team
 */
public class CheckOptions {
    
    private final OperatorCheckStrategy operatorCheckStrategy;
    
    public static final CheckOptions DEFAULT_OPTIONS = new CheckOptions();
    
    private CheckOptions() {
        this(OperatorCheckStrategy.allowAll());
    }
    
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
        
        public Builder whitelist(Set<String> allowedOperators) {
            this.operatorCheckStrategy = OperatorCheckStrategy.whitelist(allowedOperators);
            return this;
        }
        
        public Builder blacklist(Set<String> forbiddenOperators) {
            this.operatorCheckStrategy = OperatorCheckStrategy.blacklist(forbiddenOperators);
            return this;
        }
        
        public Builder allowAll() {
            this.operatorCheckStrategy = OperatorCheckStrategy.allowAll();
            return this;
        }
        
        public CheckOptions build() {
            validateOperatorCheckStrategy();
            return new CheckOptions(operatorCheckStrategy);
        }
        
        private void validateOperatorCheckStrategy() {
            if (operatorCheckStrategy == null) {
                throw new IllegalArgumentException("Operator check strategy cannot be null");
            }
            
            Set<String> operators = operatorCheckStrategy.getOperators();
            OperatorCheckStrategy.StrategyType strategyType = operatorCheckStrategy.getStrategyType();
            
            if (strategyType == OperatorCheckStrategy.StrategyType.WHITELIST
                && (operators == null || operators.isEmpty())) {
                throw new IllegalArgumentException("Whitelist strategy requires non-empty operator set");
            }
            
            if (strategyType == OperatorCheckStrategy.StrategyType.BLACKLIST
                && (operators == null || operators.isEmpty())) {
                throw new IllegalArgumentException("Blacklist strategy requires non-empty operator set");
            }
        }
    }
}
