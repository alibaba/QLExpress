package com.alibaba.qlexpress4.member;


import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:18
 */
public class QLMetaProtocolMethod implements IMethod {
    private Method method;

    public QLMetaProtocolMethod(Method method) {
        this.method = method;
    }

    @Override
    public Object invoke(Object bean, Object... params) throws Throwable {
//        MethodHandle pmh = MethodHandler.LOOK_UP.unreflect(this.method);
//        return pmh.invokeWithArguments(bean, params);
        return this.method.invoke(bean, params);
    }

    @Override
    public boolean directlyAccess() {
        return BasicUtil.isPublic(method);
    }

    @Override
    public void setAccessible(boolean allow) {
        method.setAccessible(allow);
    }

    @Override
    public Class getClazz() {
        return method.getDeclaringClass();
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public String getQualifyName() {
        return method.getDeclaringClass() + "," + method.getName() ;
    }
}
