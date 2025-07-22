package com.alibaba.qlexpress4.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Author: DQinYuan
 */
public class JvmIMethod implements IMethod {
    private final Method method;
    
    public JvmIMethod(Method method) {
        this.method = method;
    }
    
    @Override
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }
    
    @Override
    public boolean isVarArgs() {
        return method.isVarArgs();
    }
    
    @Override
    public boolean isAccess() {
        return Modifier.isPublic(method.getDeclaringClass().getModifiers()) && Modifier.isPublic(method.getModifiers());
    }
    
    @Override
    public void setAccessible(boolean flag) {
        method.setAccessible(flag);
    }
    
    @Override
    public String getName() {
        return method.getName();
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }
    
    @Override
    public Object invoke(Object obj, Object[] args)
        throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj, args);
    }
}
