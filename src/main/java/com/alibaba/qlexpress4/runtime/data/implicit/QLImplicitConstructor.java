package com.alibaba.qlexpress4.runtime.data.implicit;

import java.lang.reflect.Constructor;

/**
 * @Author TaoKan
 * @Date 2022/7/3 上午10:25
 */
public class QLImplicitConstructor {
    private final Constructor<?> constructor;
    private final boolean needImplicitTrans;

    public QLImplicitConstructor(Constructor<?> constructor, boolean needImplicitTrans){
        this.constructor = constructor;
        this.needImplicitTrans = needImplicitTrans;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public boolean needImplicitTrans() {
        return needImplicitTrans;
    }
}
