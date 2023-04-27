package com.alibaba.qlexpress4.security;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午9:11
 */
public class StrategyFactory {
    /**
     * method runtime black list
     *
     * @param map
     * @return
     */
    public static StrategyBlackList newStrategyBlackList(Map<Class, String> map) {
        return new QLStrategyBlackList(mapListTransToSet(map));
    }

    /**
     * method runtime white list
     *
     * @param map
     * @return
     */
    public static StrategyWhiteList newStrategyWhiteList(Map<Class, String> map) {
        return new QLStrategyWhiteList(mapListTransToSet(map));
    }

    /**
     * sandbox
     *
     * @param isSandBoxMode
     * @return
     */
    public static StrategySandBox newStrategySandBox(boolean isSandBoxMode) {
        return new QLStrategySandBox(isSandBoxMode);
    }


    private static Set<String> mapListTransToSet(Map<Class, String> map) {
        Set<String> list = new HashSet<>();
        map.forEach((k, v) -> {
            if (StringUtils.isBlank(v)) {
                list.add(k.getName());
            } else {
                list.add(k.getName() + "." + v);
            }
        });
        return list;
    }
}
