package com.alibaba.qlexpress4.security;

import java.lang.reflect.Member;

/**
 * A security policy that isolates qlexpress script with jvm
 * Author: DQinYuan
 */
public class StrategyIsolation implements QLSecurityStrategy {
    
    private static final StrategyIsolation INSTANCE = new StrategyIsolation();
    
    public static StrategyIsolation getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean check(Member member) {
        throw new IllegalStateException();
    }
}
