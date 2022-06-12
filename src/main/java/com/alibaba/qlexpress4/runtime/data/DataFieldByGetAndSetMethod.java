package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/12 上午11:44
 */
public class DataFieldByGetAndSetMethod implements LeftValue {

    private Method getMethod;
    private Method setMethod;
    private Object bean;
    private boolean allowAccessPrivate;
    private Class<?> defineType;


    public DataFieldByGetAndSetMethod(Method getMethod, Method setMethod, Object obj, boolean allowAccessPrivate) {
        this.getMethod = getMethod;
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
        return methodGet();
    }

    @Override
    public Class<?> getDefineType() {
        return this.defineType;
    }


    private Object methodGet() {
        try {
            if (this.getMethod == null) {
                return null;
            }
            if (!this.allowAccessPrivate || this.getMethod.isAccessible()) {
                return this.getMethod.invoke(this.bean);
            } else {
                synchronized (this.getMethod) {
                    try {
                        this.getMethod.setAccessible(true);
                        return this.getMethod.invoke(this.bean);
                    } finally {
                        this.getMethod.setAccessible(false);
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
