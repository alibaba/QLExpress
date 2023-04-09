package com.alibaba.qlexpress4.member;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:16
 */
public class QLMetaProtocol implements MetaProtocol {

    @Override
    public IMethod getMethod(Class<?> clazz, String name, Method method) {
        return new QLMetaProtocolMethod(method);
    }

    @Override
    public IField getField(Class<?> clazz, String name, Field field) {
        return new QLMetaProtocolField(field);
    }
}
