package com.alibaba.qlexpress4.runtime.data.implicit;

import java.lang.reflect.Constructor;

/**
 * Author: TaoKan
 */
public class ConstructorReflect {
    private final Constructor<?> constructor;
    private final boolean needImplicitTrans;
    private final QLImplicitVars vars;

    public ConstructorReflect(Constructor<?> constructor, boolean needImplicitTrans, QLImplicitVars vars){
        this.constructor = constructor;
        this.needImplicitTrans = needImplicitTrans;
        this.vars = vars;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public boolean needImplicitTrans() {
        return needImplicitTrans;
    }

    public QLImplicitVars getVars() {
        return vars;
    }

}
