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

}
