package com.ql.util.express;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class QLambdaInvocationHandler implements InvocationHandler {
    private final QLambda qLambda;

    public QLambdaInvocationHandler(QLambda qLambda) {
        this.qLambda = qLambda;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return Modifier.isAbstract(method.getModifiers()) ? qLambda.call(args) :
            // 为了应对 toString 方法
            method.getReturnType() == String.class ? "QLambdaProxy" : null;
    }
}
