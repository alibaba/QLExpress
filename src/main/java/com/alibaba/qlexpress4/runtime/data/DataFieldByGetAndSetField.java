package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;

import java.lang.reflect.Field;

/**
 * @Author TaoKan
 * @Date 2022/6/12 上午11:43
 */
public class DataFieldByGetAndSetField implements LeftValue {

    private Field field;
    private Object bean;
    private boolean allowAccessPrivate;
    private Class<?> defineType;


    public DataFieldByGetAndSetField(Field field, Object obj, boolean allowAccessPrivate) {
        this.field = field;
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
