package com.alibaba.qlexpress4.operator;

import java.util.Collections;
import java.util.Set;

/**
 * Blacklist strategy that forbids specified operators
 * If blackOperators is empty, all operators are allowed
 *
 * @author QLExpress Team
 */
public class BlackOperatorCheckStrategy implements OperatorCheckStrategy {
    
    private final Set<String> blackOperators;
    
    public BlackOperatorCheckStrategy(Set<String> blackOperators) {
        if (blackOperators == null) {
            this.blackOperators = Collections.emptySet();
        }
        else {
            this.blackOperators = Collections.unmodifiableSet(blackOperators);
        }
    }
    
    @Override
    public boolean isAllowed(String operator) {
        if (blackOperators.isEmpty()) {
            return true;
        }
        return !blackOperators.contains(operator);
    }
    
    @Override
    public Set<String> getOperators() {
        return blackOperators;
    }
}
