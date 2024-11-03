package com.alibaba.qlexpress4.security;

import java.lang.reflect.Member;
import java.util.Set;

/**
 *
 * A security policy that prohibits access to Java members in the blacklist.
 * Author: DQinYuan
 */
public class StrategyBlackList implements QLSecurityStrategy {

    private final Set<Member> blackList;

    public StrategyBlackList(Set<Member> blackList) {
        this.blackList = blackList;
    }

    @Override
    public boolean check(Member member) {
        return !blackList.contains(member);
    }
}
