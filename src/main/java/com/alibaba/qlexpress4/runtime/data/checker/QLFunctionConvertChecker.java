package com.alibaba.qlexpress4.runtime.data.checker;

import com.alibaba.qlexpress4.proxy.QLambdaInvocationHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.lang.reflect.Proxy;

/**
 * @Author TaoKan
 * @Date 2022/7/20 下午11:07
 */
public class QLFunctionConvertChecker implements TypeConvertChecker<QLConvertResult, Object, Class<?>> {

    @Override
    public boolean typeCheck(Object value, Class<?> type) {
        return CacheUtil.isFunctionInterface(type);
    }

    @Override
    public QLConvertResult typeReturn(Object value, Class<?> type) {
        return (QLConvertResult) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new QLambdaInvocationHandler((QLambda) value));
    }
}