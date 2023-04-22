package com.alibaba.qlexpress4.runtime.util;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.member.IField;
import com.alibaba.qlexpress4.member.IMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:47
 */
public class OptionUtils {

    public static IMethod getMethodFromQLOption(QLOptions options, Class<?> clazz, Method method){
        String name = "";
        if(method != null){
            name = method.getName();
        }
        return options.getQlMetaProtocol().getMethod(clazz, name, method);
    }

    public static IField getFieldFromQLOption(QLOptions options, Class<?> clazz,Field field){
        String name = "";
        if(field != null){
            name = field.getName();
        }
        return options.getQlMetaProtocol().getField(clazz, name, field);
    }
}
