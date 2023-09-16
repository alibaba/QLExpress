package com.alibaba.qlexpress4.security;

import java.lang.reflect.Member;
import java.util.Set;

/**
 * Author: DQinYuan
 */
public interface QLSecurityStrategy {

    static QLSecurityStrategy open() {
        return StrategyOpen.getInstance();
    }

    static QLSecurityStrategy isolation() {
        return StrategyIsolation.getInstance();
    }

    static QLSecurityStrategy blackList(Set<Member> blackList) {
        return new StrategyBlackList(blackList);
    }

    static QLSecurityStrategy whiteList(Set<Member> whiteList) {
        return new StrategyWhiteList(whiteList);
    }

    /**
     * check if member secure
     * @param member member of object
     * @return true if secure
     */
    boolean check(Member member);

}
