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
        public SafePointStrategy defaultSystemStrategy() {
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
            return systemStrategy;
        }

        public SafePointStrategy selfDefinedStrategy(StrategyWhiteList strategyWhiteList) {
            return selfDefinedStrategy(strategyWhiteList, StrategyFactory.newStrategySandBox(false));
        }

        public SafePointStrategy selfDefinedStrategy(StrategyBlackList strategyBlackList) {
            return selfDefinedStrategy(strategyBlackList, StrategyFactory.newStrategySandBox(false));
        }

        public SafePointStrategy selfDefinedStrategy(StrategyBlackList strategyBlackList, StrategySandBox strategySandBox) {
            return selfDefinedStrategy(StrategyEnum.BLACKLIST, strategyBlackList, strategySandBox);
        }

        public SafePointStrategy selfDefinedStrategy(StrategyWhiteList strategyWhiteList,StrategySandBox strategySandBox) {
            return selfDefinedStrategy(StrategyEnum.WHITELIST, strategyWhiteList, strategySandBox);
        }

        private SafePointStrategy selfDefinedStrategy(StrategyEnum strategyEnum, StrategyBlackList
                strategyBlackList,StrategySandBox strategySandBox) {
            return selfDefinedStrategy(strategyEnum, strategyBlackList, null, strategySandBox);
        }

        private SafePointStrategy selfDefinedStrategy(StrategyEnum strategyEnum, StrategyWhiteList
                strategyWhiteList,StrategySandBox strategySandBox) {
            return selfDefinedStrategy(strategyEnum, null, strategyWhiteList, strategySandBox);
        }

        private SafePointStrategy selfDefinedStrategy(StrategyEnum strategyEnum, StrategyBlackList
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
