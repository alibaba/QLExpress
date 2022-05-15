package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.BasicUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:23
 */
public class QLambdaMethod implements QLambda {

    public QLambdaMethod(List<Method> methods, Object obj, boolean allowAccessPrivate){
        this.methods = methods;
        this.bean = obj;
        this.allowAccessPrivate = allowAccessPrivate;
    }

    private List<Method> methods;
    private Object bean;
    private boolean allowAccessPrivate;

    @Override
    public QResult call(Object... params) {
        try {
            Class<?>[] type = BasicUtils.getTypeOfObject(params);
            Method method = MethodHandler.Preferred.findMostSpecificMethod(type, this.methods.toArray(new Method[0]));

            if(method == null){
                return new QResult(null, QResult.ResultType.RETURN);
            }
            if(!this.allowAccessPrivate || method.isAccessible()){
                Object result = method.invoke(this.bean,params);
                return new QResult(new DataValue(result), QResult.ResultType.RETURN);
            }else {
                synchronized (method) {
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(this.bean,params);
                        return new QResult(new DataValue(result), QResult.ResultType.RETURN);
                    }finally {
                        method.setAccessible(false);
                    }
                }
            }
        }catch (Exception e){
        }

        return new QResult(null, QResult.ResultType.RETURN);
    }
}
