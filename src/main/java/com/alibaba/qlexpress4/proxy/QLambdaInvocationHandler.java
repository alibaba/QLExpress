package com.alibaba.qlexpress4.proxy;


import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

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
    public QLConvertResult invoke(Object proxy, Method method, Object[] args) {
        try {
            if(Modifier.isAbstract(method.getModifiers())){
                Object rs = qLambda.call(args);
                return new QLConvertResult(QLConvertResultType.CAN_TRANS ,rs);
            }else {
                return new QLConvertResult(QLConvertResultType.CAN_TRANS, method.getReturnType() == String.class ? "QLambdaProxy" : null);
            }
        }catch (Throwable t){
            return new QLConvertResult(QLConvertResultType.NOT_TRANS,null);
        }
    }
}
