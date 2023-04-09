package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.strategy.IStrategy;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:32
 */
public class StrategyWhiteList implements IStrategy {
    private final List<StrategyStruct> whiteStrategyStructList;

    public StrategyWhiteList(List<StrategyStruct>  whiteStrategyStructList){
        this.whiteStrategyStructList = whiteStrategyStructList;
    }


    @Override
    public boolean check() {
        return false;
    }

    @Override
    public boolean effect() {
        return false;
    }
}
