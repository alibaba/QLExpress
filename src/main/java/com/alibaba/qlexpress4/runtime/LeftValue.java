package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * assignable value
 * <p>
 * Author: DQinYuan
 */
public interface LeftValue extends Value {

    default QLConvertResult set(Object newValue, ErrorReporter errorReporter){
        Class<?> defineType = getDefinedType();
        QLConvertResult result = InstanceConversion.castObject(newValue, defineType);
        if(result.getResultType().equals(QLConvertResultType.NOT_TRANS)){
            throw errorReporter.report("TRANS_OBJECT_ERROR","can not trans to:"+defineType.getName());
        }
        setInner(result.getCastValue());
        return result;
    }

    void setInner(Object newValue);
}
