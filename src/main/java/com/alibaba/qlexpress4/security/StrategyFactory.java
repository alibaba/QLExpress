package com.alibaba.qlexpress4.security;

import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:29
 */
public class StrategyFactory {
    public static StrategyBlackList newBlackList(Set<String> blackList){
        return null;
    }

    public static StrategyBlackList newBlackList(){
        return null;
    }


    public static StrategyBlackList newWhiteList(){
        return null;
    }
}
