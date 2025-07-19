package com.alibaba.qlexpress4.runtime;

import java.util.Objects;

/**
 * Author: DQinYuan
 */
public class MetaClass {
    
    private final Class<?> clz;
    
    public MetaClass(Class<?> clz) {
        this.clz = clz;
    }
    
    public Class<?> getClz() {
        return clz;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MetaClass metaClass = (MetaClass)o;
        return clz.equals(metaClass.clz);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(clz);
    }
}
