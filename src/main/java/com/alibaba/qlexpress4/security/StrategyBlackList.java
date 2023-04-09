package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.strategy.IStrategy;

import java.util.List;
/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:31
 */
public class StrategyBlackList implements IStrategy {

    private final List<StrategyStruct>  blackStrategyStructList;

    public StrategyBlackList(List<StrategyStruct>  blackStrategyStructList){
        this.blackStrategyStructList = blackStrategyStructList;
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
