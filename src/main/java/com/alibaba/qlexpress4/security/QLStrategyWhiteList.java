package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:35
 */
public class QLStrategyWhiteList extends StrategyWhiteList {

    public QLStrategyWhiteList(Set<String> whiteStrategyStructList) {
        super(whiteStrategyStructList);
    }

    @Override
    public boolean checkInRules(IMethod iMethod) {
        if (this.getWhiteStrategyStructList().contains(iMethod.getQualifyName())) {
            return true;
        }
        return false;
    }
}
