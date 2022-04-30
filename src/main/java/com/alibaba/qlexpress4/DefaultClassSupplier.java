package com.alibaba.qlexpress4;

/**
 * Author: DQinYuan
 */
public class DefaultClassSupplier implements ClassSupplier {

    public static final ClassSupplier INSTANCE = new DefaultClassSupplier();

    /**
     * TODO: support load inner class
     * @param clsQualifiedName
     * @return
     */
    @Override
    public Class<?> loadCls(String clsQualifiedName) {
        try {
            return Class.forName(clsQualifiedName);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return null;
        }
    }
}
