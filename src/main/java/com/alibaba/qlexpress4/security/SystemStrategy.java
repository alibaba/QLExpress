package com.alibaba.qlexpress4.security;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午9:49
 */
public class SystemStrategy {
    private StrategyEnum strategyEnum;
    private List<StrategyStruct> strategyStructList;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SystemStrategy systemStrategy;

        public SystemStrategy defaultSystemStrategy() {
            SystemStrategy systemStrategy = new SystemStrategy();
            systemStrategy.strategyEnum = StrategyEnum.BLACKLIST;
            systemStrategy.strategyStructList = Arrays.asList(
                    new StrategyStruct(System.class, "exit"),
                    new StrategyStruct(Runtime.class, "exec"),
                    new StrategyStruct(ProcessBuilder.class, "start"),
                    new StrategyStruct(Method.class, "invoke"),
                    new StrategyStruct(Class.class, "forName"),
                    new StrategyStruct(ClassLoader.class, "loadClass"),
                    new StrategyStruct(ClassLoader.class, "findClass")
            );
            return systemStrategy;
        }

        public SystemStrategy selfDefinedStrategy(StrategyEnum strategyEnum, List<StrategyStruct> strategyStructList) {
            SystemStrategy systemStrategy = new SystemStrategy();
            systemStrategy.strategyEnum = strategyEnum;
            systemStrategy.strategyStructList = strategyStructList;
            return systemStrategy;
        }

        public SystemStrategy selfDefinedStrategy(List<StrategyStruct> strategyStructList) {
            return selfDefinedStrategy(StrategyEnum.BLACKLIST, strategyStructList);
        }
    }
}
