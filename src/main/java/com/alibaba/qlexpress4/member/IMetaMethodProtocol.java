package com.alibaba.qlexpress4.member;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/3/11 下午6:58
 */
public interface IMetaMethodProtocol {
    Object methodInvoke(Object bean, Object[] params, Method method, boolean allowAccessPrivateMethod) throws Throwable;
}
