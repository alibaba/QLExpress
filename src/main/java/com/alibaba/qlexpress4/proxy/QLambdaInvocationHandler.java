package com.alibaba.qlexpress4.proxy;


import com.alibaba.qlexpress4.runtime.QLambda;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @Author TaoKan
 * @Date 2022/5/28 下午5:54
 */
public class QLambdaInvocationHandler implements InvocationHandler {
    private final QLambda qLambda;

    public QLambdaInvocationHandler(QLambda qLambda) {
        this.qLambda = qLambda;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            if(Modifier.isAbstract(method.getModifiers())){
                Object rs = qLambda.call(args);
                return rs;
            }else {
                return method.getReturnType() == String.class ? "QLambdaProxy" : null;
            }
        }catch (Exception e){
        }
        return null;
    }
}
