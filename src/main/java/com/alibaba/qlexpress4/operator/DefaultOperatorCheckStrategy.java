package com.alibaba.qlexpress4.operator;

import java.util.Collections;
import java.util.Set;

/**
 * @author zhoutao
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
        return Collections.emptySet();
    }
}
