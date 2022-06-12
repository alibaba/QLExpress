package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/12 上午11:44
 */
public class DataFieldByGetFieldAndSetMethod implements LeftValue {

    private Field field;
    private Method setMethod;
    private Object bean;
    private boolean allowAccessPrivate;
    private Class<?> defineType;


    public DataFieldByGetFieldAndSetMethod(Field field, Method setMethod, Object obj, boolean allowAccessPrivate) {
        this.field = field;
        this.setMethod = setMethod;
        this.bean = obj;
        this.allowAccessPrivate = allowAccessPrivate;
        this.defineType = Object.class;
    }

    @Override
    public void set(Object newValue) {
        methodSet(newValue);
    }

    @Override
    public Object get() {
        return fieldGet();
    }

    @Override
    public Class<?> getDefineType() {
        return this.defineType;
    }

    private Object fieldGet() {
        try {
            if (!this.allowAccessPrivate || this.field.isAccessible()) {
                return this.field.get(this.bean);
            } else {
                synchronized (this.field) {
                    try {
                        this.field.setAccessible(true);
                        return this.field.get(this.bean);
                    } finally {
                        this.field.setAccessible(false);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    private boolean methodSet(Object newValue) {
        try {
            if (this.setMethod == null) {
                return false;
            }
            if (!this.allowAccessPrivate || this.setMethod.isAccessible()) {
                this.setMethod.invoke(bean, newValue);
                return true;
            } else {
                synchronized (this.setMethod) {
                    try {
                        this.setMethod.setAccessible(true);
                        this.setMethod.invoke(bean, newValue);
                        return true;
                    } finally {
                        this.setMethod.setAccessible(false);
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

}
