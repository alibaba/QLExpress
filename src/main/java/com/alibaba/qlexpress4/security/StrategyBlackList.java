package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:31
 */
public abstract class StrategyBlackList implements IStrategy {

    private final Map<Class,String> blackStrategyStructList;

    public StrategyBlackList(Map<Class,String> blackStrategyStructList) {
        this.blackStrategyStructList = blackStrategyStructList;
    }

    public abstract boolean checkInRules(IMethod iMethod);

    protected Map<Class,String> getBlackStrategyStructList() {
        return blackStrategyStructList;
    }

}
