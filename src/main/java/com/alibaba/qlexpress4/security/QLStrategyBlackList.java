package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:34
 */
public class QLStrategyBlackList extends StrategyBlackList {

    public QLStrategyBlackList(Set<String> blackStrategyStructList) {
        super(blackStrategyStructList);
    }

    @Override
    public boolean checkInRules(IMethod iMethod) {
        if (this.getBlackStrategyStructList().contains(iMethod.getQualifyName())) {
            return true;
        }
        return false;
    }
}
