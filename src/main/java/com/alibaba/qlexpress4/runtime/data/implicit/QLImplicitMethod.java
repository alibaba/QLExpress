package com.alibaba.qlexpress4.runtime.data.implicit;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:21
 */
public class QLImplicitMethod {
    private final Method method;
    private final boolean needImplicitTrans;

    public QLImplicitMethod(Method method, boolean needImplicitTrans){
        this.method = method;
        this.needImplicitTrans = needImplicitTrans;
    }

    public Method getMethod() {
        return method;
    }

    public boolean needImplicitTrans() {
        return needImplicitTrans;
    }

}