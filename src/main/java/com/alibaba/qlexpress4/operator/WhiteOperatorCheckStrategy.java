package com.alibaba.qlexpress4.operator;

import java.util.Collections;
import java.util.Set;

/**
 * Whitelist strategy that only allows specified operators
 * If allowedOperators is empty, no operators are allowed
 *
 * @author QLExpress Team
 */
public class WhiteOperatorCheckStrategy implements OperatorCheckStrategy {
    
    private final Set<String> allowedOperators;
    
    public WhiteOperatorCheckStrategy(Set<String> allowedOperators) {
        if (allowedOperators == null) {
            this.allowedOperators = Collections.emptySet();
        }
        else {
            this.allowedOperators = Collections.unmodifiableSet(allowedOperators);
        }
    }
    
    @Override
    public boolean isAllowed(String operator) {
        return allowedOperators.contains(operator);
    }
    
    @Override
    public Set<String> getOperators() {
        return allowedOperators;
    }
}
