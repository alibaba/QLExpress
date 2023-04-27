package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:31
 */
public abstract class StrategyBlackList implements IStrategy {

    private final Set<String> blackStrategyStructList;

    public StrategyBlackList(Set<String> blackStrategyStructList) {
        this.blackStrategyStructList = blackStrategyStructList;
    }

    public abstract boolean checkInRules(IMethod iMethod);

    protected Set<String> getBlackStrategyStructList() {
        return blackStrategyStructList;
    }

}
