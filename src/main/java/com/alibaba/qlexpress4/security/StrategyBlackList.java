package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.strategy.IStrategy;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:31
 */
public abstract class StrategyBlackList implements IStrategy {

    private final List<StrategyStruct> blackStrategyStructList;

    public StrategyBlackList(List<StrategyStruct> blackStrategyStructList) {
        this.blackStrategyStructList = blackStrategyStructList;
    }

    public abstract boolean check();

    public abstract boolean effect();

    protected List<StrategyStruct> getBlackStrategyStructList() {
        return blackStrategyStructList;
    }

}
