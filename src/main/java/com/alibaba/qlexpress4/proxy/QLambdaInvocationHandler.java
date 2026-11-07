package com.alibaba.qlexpress4.proxy;

import com.alibaba.qlexpress4.runtime.QLambda;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Author: TaoKan
 */
public class QLambdaInvocationHandler implements InvocationHandler {
    private final QLambda qLambda;
    
    public QLambdaInvocationHandler(QLambda qLambda) {
        this.qLambda = qLambda;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return invokeObjectMethod(proxy, method, args);
        }
        if (Modifier.isAbstract(method.getModifiers())) {
            return qLambda.call(safeArgs(args)).getResult().get();
        }
        if (method.isDefault()) {
            return invokeDefaultMethod(proxy, method, args);
        }
        return method.invoke(qLambda, args);
    }
    
    private Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "toString":
                return "QLambdaProxy";
            case "hashCode":
                return System.identityHashCode(proxy);
            case "equals":
                return proxy == args[0];
        }
        throw new UnsupportedOperationException(methodName);
    }
    
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args)
        throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        return lookupFor(declaringClass).unreflectSpecial(method, declaringClass)
            .bindTo(proxy)
            .invokeWithArguments(safeArgs(args));
    }
    
    private MethodHandles.Lookup lookupFor(Class<?> declaringClass)
        throws Throwable {
        try {
            Method privateLookupIn =
                MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
            return (MethodHandles.Lookup)privateLookupIn.invoke(null, declaringClass, MethodHandles.lookup());
        }
        catch (NoSuchMethodException e) {
            return java8LookupFor(declaringClass);
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
    
    private MethodHandles.Lookup java8LookupFor(Class<?> declaringClass)
        throws ReflectiveOperationException {
        Constructor<MethodHandles.Lookup> constructor =
            MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);
        return constructor.newInstance(declaringClass,
            MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE
                | MethodHandles.Lookup.PUBLIC);
    }
    
    private Object[] safeArgs(Object[] args) {
        return args == null ? new Object[0] : args;
    }
}
