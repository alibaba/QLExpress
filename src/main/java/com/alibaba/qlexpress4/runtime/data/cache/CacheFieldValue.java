package com.alibaba.qlexpress4.runtime.data.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:08
 */
public class CacheFieldValue {

    private Method getMethod;
    private Method setMethod;
    private Field field;

    public CacheFieldValue(Method getMethod, Method setMethod, Field field) {
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
