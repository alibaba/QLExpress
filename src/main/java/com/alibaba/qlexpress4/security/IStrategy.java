package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:29
 */
public interface IStrategy {

    IStrategy defaultBlackList = StrategyFactory.newStrategyBlackList(new HashMap<Class, String>() {{
        put(System.class, "exit");
        put(Runtime.class, "exec");
        put(ProcessBuilder.class, "start");
        put(Method.class, "invoke");
        put(ClassLoader.class, "loadClass");
        put(ClassLoader.class, "findClass");
        put(Class.class, "forName");
    }});

    IStrategy defaultSandBox = StrategyFactory.newStrategySandBox();


    boolean check(IMethod iMethod);
}
