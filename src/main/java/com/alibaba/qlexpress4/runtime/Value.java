package com.alibaba.qlexpress4.runtime;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = new Value() {
        @Override
        public Object get() {
            return null;
        }
    };
    
    Object get();
    
    default Class<?> getType() {
        Object value = get();
        if (value == null) {
            return Nothing.class;
        }
        
        return value.getClass();
    }
    
    default String getTypeName() {
        Class<?> type = getType();
        if (type == null) {
            return null;
        }
        return type.getName();
    }
}
