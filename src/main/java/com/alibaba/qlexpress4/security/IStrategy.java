package com.alibaba.qlexpress4.security;

import com.alibaba.qlexpress4.member.IMethod;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author TaoKan
 * @Date 2023/4/1 下午5:29
 */
public interface IStrategy {

    boolean checkPassed(IMethod iMethod);

    //DEMO
    IStrategy defaultStrategy = new DefaultStrategy(mapListTransToSet(new HashMap<Class, String>() {{
        put(System.class, "exit");
        put(Runtime.class, "exec");
        put(ProcessBuilder.class, "start");
        put(Method.class, "invoke");
        put(ClassLoader.class, "loadClass");
        put(ClassLoader.class, "findClass");
        put(Class.class, "forName");
    }}),null, true);


    static Set<String> mapListTransToSet(Map<Class, String> map) {
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
