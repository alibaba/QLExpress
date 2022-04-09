package com.ql.util.express.config.whitelist;

public interface WhiteChecker {

    /**
     * @param clazz
     * @return true 表示白名单校验通过
     */
    boolean check(Class<?> clazz);
}
