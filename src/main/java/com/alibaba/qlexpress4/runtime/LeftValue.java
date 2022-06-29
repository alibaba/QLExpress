package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;

/**
 * assignable value
 * <p>
 * Author: DQinYuan
 */
public interface LeftValue extends Value {

    default void set(Object newValue){
        setInner(InstanceConversion.castObject(newValue, getDefineType()));
    }

    void setInner(Object newValue);
}
