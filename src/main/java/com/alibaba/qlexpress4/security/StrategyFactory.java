package com.alibaba.qlexpress4.security;

import java.util.Map;

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
    public static StrategyBlackList newStrategyBlackList(Map<Class,String> list){
        return new QLStrategyBlackList(list);
    }
    /**
     * method runtime white list
     * @param list
     * @return
     */
    public static StrategyWhiteList newStrategyWhiteList(Map<Class,String> list){
        return new QLStrategyWhiteList(list);
    }

    /**
     * sandbox
     * @param isSandBoxMode
     * @return
     */
    public static StrategySandBox newStrategySandBox(boolean isSandBoxMode){
        return new QLStrategySandBoxMode(isSandBoxMode);
    }
}
