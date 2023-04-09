package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:32
 */
public abstract class StrategyWhiteList implements IStrategy {
    private final Map<Class,String> whiteStrategyStructList;

    public StrategyWhiteList(Map<Class,String> whiteStrategyStructList) {
        this.whiteStrategyStructList = whiteStrategyStructList;
    }

    public abstract boolean checkInRules(IMethod iMethod);

    protected Map<Class,String> getWhiteStrategyStructList() {
        return whiteStrategyStructList;
    }
}
