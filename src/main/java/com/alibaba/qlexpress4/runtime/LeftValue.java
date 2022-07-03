package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;

/**
 * assignable value
 * <p>
 * Author: DQinYuan
 */
public interface LeftValue extends Value {

    default QLConvertResult set(Object newValue){
        QLConvertResult result = InstanceConversion.castObject(newValue, getDefineType());
        setInner(result.getCastValue());
        return result;
    }

    void setInner(Object newValue);
}
