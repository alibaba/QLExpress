package com.alibaba.qlexpress4.operator;

import java.util.Set;

/**
 * Strategy that allows all operators without restriction
 *
 * @author QLExpress Team
 */
public class DefaultOperatorCheckStrategy implements OperatorCheckStrategy {
    
    private static final DefaultOperatorCheckStrategy INSTANCE = new DefaultOperatorCheckStrategy();
    
    public static DefaultOperatorCheckStrategy getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean isAllowed(String operator) {
        return true;
    }
    
    @Override
    public Set<String> getOperators() {
        return null;
    }
    
    @Override
    public StrategyType getStrategyType() {
        return StrategyType.ALLOW_ALL;
    }
}
