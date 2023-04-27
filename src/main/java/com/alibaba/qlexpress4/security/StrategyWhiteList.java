package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:32
 */
public abstract class StrategyWhiteList implements IStrategy {
    private final Set<String> whiteStrategyStructList;

    public StrategyWhiteList(Set<String> whiteStrategyStructList) {
        this.whiteStrategyStructList = whiteStrategyStructList;
    }

    public abstract boolean checkInRules(IMethod iMethod);

    protected Set<String> getWhiteStrategyStructList() {
        return whiteStrategyStructList;
    }
}
