package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:32
 */
public class StrategyWhiteList implements IStrategyWhiteList {
    private final Set<String> whiteStrategyStructList;

    public StrategyWhiteList(Set<String> whiteStrategyStructList) {
        this.whiteStrategyStructList = whiteStrategyStructList;
    }

    protected Set<String> getWhiteStrategyStructList() {
        return whiteStrategyStructList;
    }

    @Override
    public boolean checkRulesPassed(IMethod iMethod) {
        if (this.whiteStrategyStructList.contains(iMethod.getQualifyName())) {
            return true;
        }
        return false;
    }
}
