package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.runtime.data.DataValue;

/**
 * Author: DQinYuan
 */
public interface Value {
    Value NULL_VALUE = new DataValue(null);

    Object get();

    Class<?> getDefineType();

    default Class<?> getType() {
        Object value = get();
        if (value == null) {
            return null;
        }

        return value.getClass();
    }

    Class<?> getDeclaredClass();

    default Class<?> getActualClass() {
        Object value = get();
        if (value == null) {
            // TODO 这个地方是否返回声明的类型？
            //return getDeclaredClass();
            return null;
        }

        return value.getClass();
    }

    default String getTypeName() {
        Object value = get();
        if (value == null) {
            return null;
        }

        return value.getClass().getName();
    }

    default String getActualClassName() {
        Class<?> actualClass = getActualClass();
        if (actualClass == null) {
            return null;
        }
        return actualClass.getName();
    }
}
