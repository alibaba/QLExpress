package com.alibaba.qlexpress4.security;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:15
 */
public class SafePointStrategy {
    private StrategyEnum strategyEnum;
    private StrategyWhiteList strategyWhiteList;
    private StrategyBlackList strategyBlackList;
    private StrategySandBox strategySandBox;

    public StrategyEnum getStrategyEnum() {
        return strategyEnum;
    }

    public StrategyBlackList getStrategyBlackList() {
        return strategyBlackList;
    }

    public StrategyWhiteList getStrategyWhiteList() {
        return strategyWhiteList;
    }

    public StrategySandBox getStrategySandBox() {
        return strategySandBox;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static SafePointStrategy safePointStrategy;
        static {
            SafePointStrategy systemStrategy = new SafePointStrategy();
            systemStrategy.strategyEnum = StrategyEnum.BLACKLIST;
            systemStrategy.strategyBlackList = StrategyFactory.newStrategyBlackList(new HashMap<Class, String>() {{
                put(System.class, "exit");
                put(Runtime.class, "exec");
                put(ProcessBuilder.class, "start");
                put(Method.class, "invoke");
                put(ClassLoader.class, "loadClass");
                put(ClassLoader.class, "findClass");
                put(Class.class, "forName");
            }});
            systemStrategy.strategySandBox = StrategyFactory.newStrategySandBox(false);
            safePointStrategy = systemStrategy;
        }

        public SafePointStrategy defaultSystemStrategy() {
            return safePointStrategy;
        }

        public SafePointStrategy userDefinedStrategy(StrategyWhiteList strategyWhiteList) {
            return userDefinedStrategy(strategyWhiteList, StrategyFactory.newStrategySandBox(false));
        }

        public SafePointStrategy userDefinedStrategy(StrategyBlackList strategyBlackList) {
            return userDefinedStrategy(strategyBlackList, StrategyFactory.newStrategySandBox(false));
        }

        public SafePointStrategy userDefinedStrategy(StrategyBlackList strategyBlackList, StrategySandBox strategySandBox) {
            return userDefinedStrategy(StrategyEnum.BLACKLIST, strategyBlackList, strategySandBox);
        }

        public SafePointStrategy userDefinedStrategy(StrategyWhiteList strategyWhiteList,StrategySandBox strategySandBox) {
            return userDefinedStrategy(StrategyEnum.WHITELIST, strategyWhiteList, strategySandBox);
        }

        private SafePointStrategy userDefinedStrategy(StrategyEnum strategyEnum, StrategyBlackList
                strategyBlackList,StrategySandBox strategySandBox) {
            return userDefinedStrategy(strategyEnum, strategyBlackList, null, strategySandBox);
        }

        private SafePointStrategy userDefinedStrategy(StrategyEnum strategyEnum, StrategyWhiteList
                strategyWhiteList,StrategySandBox strategySandBox) {
            return userDefinedStrategy(strategyEnum, null, strategyWhiteList, strategySandBox);
        }

        private SafePointStrategy userDefinedStrategy(StrategyEnum strategyEnum, StrategyBlackList
                strategyBlackList,StrategyWhiteList strategyWhiteList,StrategySandBox strategySandBox) {
            SafePointStrategy systemStrategy = new SafePointStrategy();
            systemStrategy.strategyEnum = strategyEnum;
            systemStrategy.strategyBlackList = strategyBlackList;
            systemStrategy.strategyWhiteList = strategyWhiteList;
            systemStrategy.strategySandBox = strategySandBox;
            return systemStrategy;
        }
    }
}
