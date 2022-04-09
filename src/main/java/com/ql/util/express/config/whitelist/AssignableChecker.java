package com.ql.util.express.config.whitelist;

public class AssignableChecker implements WhiteChecker {
    private final Class<?> whiteClazz;

    public AssignableChecker(Class<?> whiteClazz) {
        this.whiteClazz = whiteClazz;
    }

    @Override
    public boolean check(Class<?> clazz) {
        return whiteClazz.isAssignableFrom(clazz);
    }
}
