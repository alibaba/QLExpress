package com.alibaba.qlexpress4.security;


import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午9:11
 */
public class StrategyFactory {
    /**
     * method runtime black list
     * @param list
     * @return
     */
    public static StrategyBlackList newStrategyBlackList(List<StrategyStruct> list){
        return new StrategyBlackList(list);
    }
    /**
     * method runtime white list
     * @param list
     * @return
     */
    public static StrategyWhiteList newStrategyWhiteList(List<StrategyStruct> list){
        return new StrategyWhiteList(list);
    }

}
