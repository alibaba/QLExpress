package com.alibaba.qlexpress4.member;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:16
 */
public interface MetaProtocol {
    IMethod getMethod(Class<?> clazz, String name, Method method);

    IField getField(Class<?> clazz, String name, Field field);
}
