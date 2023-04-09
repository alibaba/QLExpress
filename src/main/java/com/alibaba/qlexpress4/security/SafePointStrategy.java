package com.alibaba.qlexpress4.security;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:15
 */
public class SafePointStrategy {
    private StrategyEnum strategyEnum;
    private StrategyWhiteList strategyWhiteList;
    private StrategyBlackList strategyBlackList;
    private boolean isSandBoxMode;

    public StrategyEnum getStrategyEnum() {
        return strategyEnum;
    }

    public StrategyBlackList getStrategyBlackList() {
        return strategyBlackList;
    }

    public StrategyWhiteList getStrategyWhiteList() {
        return strategyWhiteList;
    }

    public boolean isSandBoxMode() {
        return isSandBoxMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public SafePointStrategy defaultSystemStrategy() {
            SafePointStrategy systemStrategy = new SafePointStrategy();
            systemStrategy.strategyEnum = StrategyEnum.BLACKLIST;
            StrategyFactory.newStrategyBlackList(Arrays.asList(
                    new StrategyStruct(System.class, "exit"),
                    new StrategyStruct(Runtime.class, "exec"),
                    new StrategyStruct(ProcessBuilder.class, "start"),
                    new StrategyStruct(Method.class, "invoke"),
                    new StrategyStruct(Class.class, "forName"),
                    new StrategyStruct(ClassLoader.class, "loadClass"),
                    new StrategyStruct(ClassLoader.class, "findClass")
            ));
            systemStrategy.isSandBoxMode = false;
            return systemStrategy;
        }

        public SafePointStrategy selfDefinedStrategy(StrategyWhiteList strategyWhiteList) {
            return selfDefinedStrategy(strategyWhiteList, false);
        }

        public SafePointStrategy selfDefinedStrategy(StrategyBlackList strategyBlackList) {
            return selfDefinedStrategy(strategyBlackList, false);
        }

        public SafePointStrategy selfDefinedStrategy(StrategyBlackList strategyBlackList, boolean sandBoxMode) {
            return selfDefinedStrategy(StrategyEnum.BLACKLIST, strategyBlackList, sandBoxMode);
        }

        public SafePointStrategy selfDefinedStrategy(StrategyWhiteList strategyWhiteList, boolean sandBoxMode) {
            return selfDefinedStrategy(StrategyEnum.WHITELIST, strategyWhiteList, sandBoxMode);
        }

        private SafePointStrategy selfDefinedStrategy(StrategyEnum strategyEnum, StrategyBlackList strategyBlackList,
                                                      boolean sandBoxMode) {
            return selfDefinedStrategy(strategyEnum, strategyBlackList, null, sandBoxMode);
        }

        private SafePointStrategy selfDefinedStrategy(StrategyEnum strategyEnum, StrategyWhiteList strategyWhiteList,
                                                      boolean sanBoxMode) {
            return selfDefinedStrategy(strategyEnum, null, strategyWhiteList, sanBoxMode);
        }

        private SafePointStrategy selfDefinedStrategy(StrategyEnum strategyEnum, StrategyBlackList strategyBlackList,
                                                      StrategyWhiteList strategyWhiteList,
                                                      boolean isSandBoxMode) {
            SafePointStrategy systemStrategy = new SafePointStrategy();
            systemStrategy.strategyEnum = strategyEnum;
            systemStrategy.strategyBlackList = strategyBlackList;
            systemStrategy.strategyWhiteList = strategyWhiteList;
            systemStrategy.isSandBoxMode = isSandBoxMode;
            return systemStrategy;
        }
    }
}
