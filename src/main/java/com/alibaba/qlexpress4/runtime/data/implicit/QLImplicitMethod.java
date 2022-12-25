package com.alibaba.qlexpress4.runtime.data.implicit;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:21
 */
public class QLImplicitMethod extends QLImplicitBase{
    private final Method method;

    public QLImplicitMethod(Method method, boolean needImplicitTrans, QLImplicitVars vars){
        super(needImplicitTrans, vars);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }
}