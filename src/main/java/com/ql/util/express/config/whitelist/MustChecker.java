package com.ql.util.express.config.whitelist;

public class MustChecker implements WhiteChecker {
    private final Class<?> whiteClazz;

    public MustChecker(Class<?> whiteClazz) {
        this.whiteClazz = whiteClazz;
    }

    @Override
    public boolean check(Class<?> clazz) {
        return clazz == whiteClazz;
    }
}
