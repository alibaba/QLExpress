package com.alibaba.qlexpress4;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: DQinYuan
 */
public class DefaultClassSupplier implements ClassSupplier {

    private final Map<String, NullableCls> cache = new ConcurrentHashMap<>();

    /**
     * @param clsQualifiedName
     * @return
     */
    @Override
    public Class<?> loadCls(String clsQualifiedName) {
        NullableCls nullableCls = cache.computeIfAbsent(clsQualifiedName, this::loadClsInner);
        return nullableCls.found? nullableCls.cls: null;
    }

    private NullableCls loadClsInner(String clsQualifiedName) {
        try {
            Class<?> aClass = Class.forName(clsQualifiedName);
            return new NullableCls(aClass, true);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return new NullableCls(null, false);
        }
    }

    private static class NullableCls {
        private Class<?> cls;
        private boolean found;

        public NullableCls(Class<?> cls, boolean found) {
            this.cls = cls;
            this.found = found;
        }
    }
}
