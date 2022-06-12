package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/12 上午11:43
 */
public class DataFieldByGetMethodAndSetField implements LeftValue {

    private Field field;
    private Method getMethod;
    private Object bean;
    private boolean allowAccessPrivate;
    private Class<?> defineType;


    public DataFieldByGetMethodAndSetField(Field field, Method getMethod, Object obj, boolean allowAccessPrivate) {
        this.field = field;
        this.getMethod = getMethod;
        this.bean = obj;
        this.allowAccessPrivate = allowAccessPrivate;
        this.defineType = Object.class;
    }

    @Override
    public void set(Object newValue) {
        fieldSet(newValue);
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

    private void fieldSet(Object newValue) {
        try {
            if (this.allowAccessPrivate || this.field.isAccessible()) {
                this.field.set(this.bean, newValue);
            } else {
                synchronized (this.field) {
                    try {
                        this.field.setAccessible(true);
                        this.field.set(this.bean, newValue);
                    } finally {
                        this.field.setAccessible(false);
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
