package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.utils.CacheUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/4/18 上午7:47
 */
public class DataField implements LeftValue {

    private Field field;
    private Method getMethod;
    private Method setMethod;
    private Object bean;
    private boolean allowAccessPrivate;
    private String fieldName;
    private Class<?> clazz;


    public DataField(Field field, Method getMethod, Method setMethod, Class<?> clazz, Object obj,
                     String fieldName, boolean allowAccessPrivate){
        this.field = field;
        this.getMethod = getMethod;
        this.setMethod = setMethod;
        this.clazz = clazz;
        this.bean = obj;
        this.fieldName = fieldName;
        this.allowAccessPrivate = allowAccessPrivate;
    }

    @Override
    public void set(Object newValue) {
        if(!methodSet(newValue)){
            fieldSet(newValue);
        }
    }

    @Override
    public Object get() {
        Object rs = methodGet();
        if(rs == null){
            rs = fieldGet();
            if(rs != null){
                return rs;
            }
        }else {
            return rs;
        }
        return null;
    }

    private Object methodGet(){
        try {
            if(!this.allowAccessPrivate || this.getMethod.isAccessible()){
                return this.getMethod.invoke(this.bean);
            }else {
                synchronized (this.getMethod) {
                    try {
                        this.getMethod.setAccessible(true);
                        return this.getMethod.invoke(this.bean);
                    }finally {
                        this.getMethod.setAccessible(false);
                    }
                }
            }
        }catch (Exception e){
            return null;
        }
    }

    private Object fieldGet(){
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
        }catch (Exception e){
            return null;
        }
    }

    private boolean methodSet(Object newValue){
        try {
            if(this.setMethod == null){
                return false;
            }
            if(!this.allowAccessPrivate || this.setMethod.isAccessible()){
                this.setMethod.invoke(bean,newValue);
                return true;
            }else {
                synchronized (this.setMethod) {
                    try {
                        this.setMethod.setAccessible(true);
                        this.setMethod.invoke(bean,newValue);
                        return true;
                    }finally {
                        this.setMethod.setAccessible(false);
                    }
                }
            }
        }catch (Exception e){
        }
        return false;
    }

    private void fieldSet(Object newValue){
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
        }catch (Exception e){
        }
    }
}
