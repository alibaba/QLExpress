package com.ql.util.express.config.whitelist;

public class CheckerFactory {

    public static WhiteChecker must(Class<?> clazz) {
        return new MustChecker(clazz);
    }

    public static WhiteChecker assignable(Class<?> clazz) {
        return new AssignableChecker(clazz);
    }

}
