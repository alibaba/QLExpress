package com.alibaba.qlexpress4.runtime.data.implicit;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:21
 */
public class MethodReflect {
    private final Method method;
    private final boolean needImplicitTrans;
    private final QLImplicitVars vars;

    public MethodReflect(Method method, boolean needImplicitTrans, QLImplicitVars vars){
        this.method = method;
        this.needImplicitTrans = needImplicitTrans;
        this.vars = vars;
    }

    public Method getMethod() {
        return method;
    }

    public boolean needImplicitTrans() {
        return needImplicitTrans;
    }

    public QLImplicitVars getVars() {
        return vars;
    }


}