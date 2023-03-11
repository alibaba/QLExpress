package com.alibaba.qlexpress4.protocol;

import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/3/11 下午6:59
 */
public class DefaultQLMetaProtocol implements QLMetaProtocol {

    public static final MethodHandles.Lookup LOOKUP_PUBLIC = MethodHandles.publicLookup();
    public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private Object findMethodHandleAndInvoke(Object bean, MethodHandle methodHandle, Object[] params) throws Throwable{
        if(bean instanceof MetaClass){
            return methodHandle.invokeWithArguments(params);
        }else {
            return methodHandle.bindTo(bean).invokeWithArguments(params);
        }
    }
    @Override
    public Object methodInvoke(Object bean, Object[] params, Method method, boolean allowAccessPrivateMethod) throws Throwable {
        if (BasicUtil.isPublic(method)) {
            MethodHandle methodHandle = LOOKUP_PUBLIC.unreflect(method);
            return findMethodHandleAndInvoke(bean, methodHandle, params);
        } else {
            if(!allowAccessPrivateMethod){
                throw new IllegalAccessException("can not allow access");
            }else {
                synchronized (method) {
                    try {
                        method.setAccessible(true);
                        MethodHandle methodHandle = LOOKUP.unreflect(method);
                        return findMethodHandleAndInvoke(bean, methodHandle, params);
                    } finally {
                        method.setAccessible(false);
                    }
                }
            }
        }
    }
}
