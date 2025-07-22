package com.alibaba.qlexpress4;

/**
 * Author: DQinYuan
 */
@FunctionalInterface
public interface ClassSupplier {
    
    Class<?> loadCls(String clsQualifiedName);
    
}