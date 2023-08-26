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
    private final boolean useSandBox;

    public DefaultStrategy(Set<String> blackStrategyStructList, Set<String> whiteStrategyStructList, boolean useSandBox){
        this.blackStrategyStructList = blackStrategyStructList;
        this.whiteStrategyStructList = whiteStrategyStructList;
        this.useSandBox = useSandBox;
    }

    @Override
    public boolean checkPassed(IMethod iMethod) {
        if(useSandBox) {
            return false;
        }
        if (this.blackStrategyStructList != null && this.blackStrategyStructList.contains(iMethod.getQualifyName())) {
            return false;
        }
        if (this.whiteStrategyStructList != null && this.whiteStrategyStructList.contains(iMethod.getQualifyName())) {
            return true;
        }
        return false;
    }
}
