package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/7/8 下午7:42
 */
public class DefaultStrategy implements IStrategy{

    private final Set<String> blackStrategyStructList;
    private final Set<String> whiteStrategyStructList;

    public DefaultStrategy(Set<String> blackStrategyStructList,
                           Set<String> whiteStrategyStructList){
        this.blackStrategyStructList = blackStrategyStructList;
        this.whiteStrategyStructList = whiteStrategyStructList;
    }

    @Override
    public boolean checkBlackList(IMethod iMethod) {
        if (this.blackStrategyStructList != null && this.blackStrategyStructList.contains(iMethod.getQualifyName())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkSandbox(IMethod iMethod) {
        return false;
    }

    @Override
    public boolean checkWhiteList(IMethod iMethod) {
        if (this.whiteStrategyStructList != null && this.whiteStrategyStructList.contains(iMethod.getQualifyName())) {
            return true;
        }
        return false;
    }
}
