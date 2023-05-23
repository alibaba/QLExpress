package com.alibaba.qlexpress4.security;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @Author TaoKan
 * @Date 2023/4/9 上午10:15
 */
public class SafePointStrategy {
    private IStrategyWhiteList strategyWhiteList;
    private IStrategyBlackList strategyBlackList;
    private IStrategySandBox strategySandBox;

    public static Builder builder() {
        return new Builder();
    }

    public IStrategyBlackList getStrategyBlackList() {
        return strategyBlackList;
    }

    public IStrategyWhiteList getStrategyWhiteList() {
        return strategyWhiteList;
    }

    public IStrategySandBox getStrategySandBox() {
        return strategySandBox;
    }

    public static class Builder {
        private static SafePointStrategy safePointStrategy;

        static {
            SafePointStrategy systemStrategy = new SafePointStrategy();
            systemStrategy.strategyBlackList = StrategyFactory.newStrategyBlackList(new HashMap<Class, String>() {{
                put(System.class, "exit");
                put(Runtime.class, "exec");
                put(ProcessBuilder.class, "start");
                put(Method.class, "invoke");
                put(ClassLoader.class, "loadClass");
                put(ClassLoader.class, "findClass");
                put(Class.class, "forName");
            }});
            systemStrategy.strategySandBox = StrategyFactory.newStrategySandBox();
            safePointStrategy = systemStrategy;
        }

        public SafePointStrategy defaultSystemStrategy() {
            return safePointStrategy;
        }

        public SafePointStrategy userDefineStrategy(IStrategySandBox iStrategySandBox,
                                                    IStrategyBlackList iStrategyBlackList, IStrategyWhiteList iStrategyWhiteList) {
            SafePointStrategy userDefine = new SafePointStrategy();
            userDefine.strategyWhiteList = iStrategyWhiteList;
            userDefine.strategyBlackList = iStrategyBlackList;
            userDefine.strategySandBox = iStrategySandBox;
            return userDefine;
        }
    }
}
