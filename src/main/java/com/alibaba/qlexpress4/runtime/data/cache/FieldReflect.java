package com.alibaba.qlexpress4.runtime.data.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Author: TaoKan
 */
public class FieldReflect {

    private final Method getMethod;
    private final Method setMethod;
    private final Field field;

    public FieldReflect(Method getMethod, Method setMethod, Field field) {
        this.getMethod = getMethod;
        this.setMethod = setMethod;
        this.field = field;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public Field getField() {
        return field;
    }
}
