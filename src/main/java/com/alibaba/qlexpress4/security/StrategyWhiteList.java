package com.alibaba.qlexpress4.security;

import java.lang.reflect.Member;
import java.util.Set;

/**
 * A security policy that only permits access to Java members in the whitelist.
 * Author: DQinYuan
 */
public class StrategyWhiteList implements QLSecurityStrategy {

    private final Set<Member> whiteList;

    public StrategyWhiteList(Set<Member> whiteList) {
        this.whiteList = whiteList;
    }


    @Override
    public boolean check(Member member) {
        return whiteList.contains(member);
    }
}
