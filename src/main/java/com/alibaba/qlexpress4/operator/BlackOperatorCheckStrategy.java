package com.alibaba.qlexpress4.operator;

import java.util.Collections;
import java.util.Set;

/**
 * Blacklist strategy that forbids specified operators
 *
 * @author QLExpress Team
 */
public class BlackOperatorCheckStrategy implements OperatorCheckStrategy {
    
    private final Set<String> blackOperators;
    
    public BlackOperatorCheckStrategy(Set<String> blackOperators) {
        if (blackOperators == null || blackOperators.isEmpty()) {
            throw new IllegalArgumentException("Blacklist operators cannot be null or empty");
        }
        this.blackOperators = Collections.unmodifiableSet(blackOperators);
    }
    
    @Override
    public boolean isAllowed(String operator) {
        return !blackOperators.contains(operator);
    }
    
    @Override
    public Set<String> getOperators() {
        return blackOperators;
    }
    
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.BLACKLIST;
    }
}
