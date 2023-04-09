package com.alibaba.qlexpress4.security;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:34
 */
public class QLStrategyBlackList extends StrategyBlackList {

    public QLStrategyBlackList(List<StrategyStruct> blackStrategyStructList) {
        super(blackStrategyStructList);
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
