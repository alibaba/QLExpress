package com.alibaba.qlexpress4.operator;

import java.util.Collections;
import java.util.Set;

/**
 * Whitelist strategy that only allows specified operators
 *
 * @author QLExpress Team
 */
public class WhiteOperatorCheckStrategy implements OperatorCheckStrategy {
    
    private final Set<String> allowedOperators;
    
    public WhiteOperatorCheckStrategy(Set<String> allowedOperators) {
        if (allowedOperators == null || allowedOperators.isEmpty()) {
            throw new IllegalArgumentException("Whitelist operators cannot be null or empty");
        }
        this.allowedOperators = Collections.unmodifiableSet(allowedOperators);
    }
    
    @Override
    public boolean isAllowed(String operator) {
        return allowedOperators.contains(operator);
    }
    
    @Override
    public Set<String> getOperators() {
        return allowedOperators;
    }
    
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.WHITELIST;
    }
}
