package com.alibaba.qlexpress4.security;

import java.lang.reflect.Member;

/**
 * Author: DQinYuan
 */
public class StrategyOpen implements QLSecurityStrategy {

    private static final StrategyOpen INSTANCE = new StrategyOpen();

    public static StrategyOpen getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean check(Member member) {
        return true;
    }
}
