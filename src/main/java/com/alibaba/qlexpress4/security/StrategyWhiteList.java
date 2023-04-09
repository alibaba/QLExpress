package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.strategy.IStrategy;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:32
 */
public abstract class StrategyWhiteList implements IStrategy {
    private final List<StrategyStruct> whiteStrategyStructList;

    public StrategyWhiteList(List<StrategyStruct> whiteStrategyStructList) {
        this.whiteStrategyStructList = whiteStrategyStructList;
    }

    public abstract boolean check();

    public abstract boolean effect();

    protected List<StrategyStruct> getWhiteStrategyStructList() {
        return whiteStrategyStructList;
    }
}
