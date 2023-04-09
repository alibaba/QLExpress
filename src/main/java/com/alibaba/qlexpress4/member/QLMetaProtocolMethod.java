package com.alibaba.qlexpress4.member;


import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:18
 */
public class QLMetaProtocolMethod implements IMethod {
    private Method method;
    private ErrorReporter errorReporter;

    public QLMetaProtocolMethod(ErrorReporter errorReporter, Method method) {
        this.errorReporter = errorReporter;
        this.method = method;
    }

    @Override
    public Object invoke(Object bean, Object... params) {
        return null;
    }

    @Override
    public boolean allowVisitWithOutPermission() {
        return BasicUtil.isPublic(method);
    }

    @Override
    public void seVisitWithOutPermission(boolean allow) {
        method.setAccessible(allow);
    }
}
