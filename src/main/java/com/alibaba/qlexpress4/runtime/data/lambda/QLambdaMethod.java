package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.cache.QLFunctionCache;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.BasicUtil;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:23
 */
public class QLambdaMethod implements QLambda {

    private List<Method> methods;
    private Object bean;
    private boolean allowAccessPrivate;
    private QLCaches qlCaches;

    public QLambdaMethod(List<Method> methods, Object obj, boolean allowAccessPrivate, QLCaches qlCaches) {
        this.methods = methods;
        this.bean = obj;
        this.allowAccessPrivate = allowAccessPrivate;
        this.qlCaches = qlCaches;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        if (methods == null || methods.size() == 0) {
            return new QResult(null, QResult.ResultType.RETURN);
        }

        Class<?>[] type = BasicUtil.getTypeOfObject(params);
        Method method = MethodHandler.Preferred.findMostSpecificMethod(qlCaches, type, this.methods.toArray(new Method[0]));

        if (method == null) {
            return new QResult(null, QResult.ResultType.RETURN);
        }
        if (BasicUtil.isPublic(method)) {
            Object result = method.invoke(this.bean, params);
            return new QResult(new DataValue(result), QResult.ResultType.RETURN);
        } else {
            if(!allowAccessPrivate){
                throw new RuntimeException("QLambdaMethod not accessible");
            }else {
                synchronized (method) {
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(this.bean, params);
                        return new QResult(new DataValue(result), QResult.ResultType.RETURN);
                    } finally {
                        method.setAccessible(false);
                    }
                }
            }
        }
    }
}
