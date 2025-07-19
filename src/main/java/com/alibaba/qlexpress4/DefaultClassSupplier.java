package com.alibaba.qlexpress4;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: DQinYuan
 */
public class DefaultClassSupplier implements ClassSupplier {
    
    private static final DefaultClassSupplier INSTANCE = new DefaultClassSupplier();
    
    public static DefaultClassSupplier getInstance() {
        return INSTANCE;
    }
    
    private final Map<String, Optional<Class<?>>> cache = new ConcurrentHashMap<>();
    
    /**
     * @param clsQualifiedName qualified name of class
     * @return loaded class
     */
    @Override
    public Class<?> loadCls(String clsQualifiedName) {
        Optional<Class<?>> clsOp = cache.computeIfAbsent(clsQualifiedName, this::loadClsInner);
        return clsOp.orElse(null);
    }
    
    private Optional<Class<?>> loadClsInner(String clsQualifiedName) {
        try {
            Class<?> aClass = Class.forName(clsQualifiedName);
            return Optional.of(aClass);
        }
        catch (ClassNotFoundException | NoClassDefFoundError e) {
            return Optional.empty();
        }
    }
}
