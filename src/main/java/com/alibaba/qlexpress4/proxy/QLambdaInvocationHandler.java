package com.alibaba.qlexpress4.proxy;

import com.alibaba.qlexpress4.runtime.QLambda;

import java.lang.reflect.InvocationHandler;
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
        return Modifier.isAbstract(method.getModifiers()) ? qLambda.call(args).getResult().get()
            : method.getReturnType() == String.class && "toString".equals(method.getName()) ? "QLambdaProxy"
                : method.invoke(args);
    }
}
