package com.alibaba.qlexpress4.runtime.data.implicit;

import java.lang.reflect.Constructor;

/**
 * @Author TaoKan
 * @Date 2022/7/3 上午10:25
 */
public class QLImplicitConstructor extends QLImplicitBase{
    private final Constructor<?> constructor;

    public QLImplicitConstructor(Constructor<?> constructor, boolean needImplicitTrans, QLImplicitVars vars){
        super(needImplicitTrans,vars);
        this.constructor = constructor;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }
}
