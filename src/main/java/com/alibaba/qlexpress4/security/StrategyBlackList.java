package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:31
 */
public class StrategyBlackList implements IStrategy {

    private final Set<String> blackStrategyStructList;

    public StrategyBlackList(Set<String> blackStrategyStructList) {
        this.blackStrategyStructList = blackStrategyStructList;
    }

    protected Set<String> getBlackStrategyStructList() {
        return blackStrategyStructList;
    }

    @Override
    public boolean check(IMethod iMethod) {
        if (this.blackStrategyStructList != null && this.blackStrategyStructList.contains(iMethod.getQualifyName())) {
            return false;
        }
        return true;
    }
}
