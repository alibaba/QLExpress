package com.alibaba.qlexpress4.runtime;

import java.lang.reflect.InvocationTargetException;

/**
 * Author: DQinYuan
 */
public interface IMethod {

    Class<?>[] getParameterTypes();

    boolean isVarArgs();

    boolean isAccess();

    void setAccessible(boolean flag);

    String getName();

    Class<?> getDeclaringClass();

    Object invoke(Object obj, Object[] args) throws InvocationTargetException, IllegalAccessException;
}
