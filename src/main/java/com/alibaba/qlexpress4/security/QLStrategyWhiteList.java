package com.alibaba.qlexpress4.security;

import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:35
 */
public class QLStrategyWhiteList extends StrategyWhiteList {

    public QLStrategyWhiteList(List<StrategyStruct> whiteStrategyStructList) {
        super(whiteStrategyStructList);
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
