package com.alibaba.qlexpress4.member;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Field;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:19
 */
public class QLMetaProtocolField implements IField {
    private Field field;
    private ErrorReporter errorReporter;


    public QLMetaProtocolField(ErrorReporter errorReporter, Field field) {
        this.field = field;
        this.errorReporter = errorReporter;
    }

    @Override
    public void set(Object bean, Object value) {
        try {
            field.set(bean, value);
        }catch (IllegalArgumentException e){

        }catch (IllegalAccessException e){

        }
    }

    @Override
    public Object get(Object bean) {
        try {
            return field.get(bean);
        }catch (IllegalArgumentException e){

        }catch (IllegalAccessException e){

        }
        return null;
    }

    @Override
    public boolean allowVisitWithOutPermission() {
        return BasicUtil.isPublic(field);
    }

    @Override
    public void seVisitWithOutPermission(boolean allow) {
        field.setAccessible(allow);
    }
}
