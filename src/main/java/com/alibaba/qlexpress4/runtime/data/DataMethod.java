package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/4/19 下午10:03
 */
public class DataMethod implements Value {

    public DataMethod(List<Method> methods, Object obj, boolean allowAccessPrivate){
        this.methods = methods;
        this.bean = obj;
        this.allowAccessPrivate = allowAccessPrivate;
    }

    private List<Method> methods;
    private Object bean;
    private boolean allowAccessPrivate;


    @Override
    public Object get() {
        if(methods == null || methods.size() == 0){
            return null;
        }
        QLambda qLambda = new QLambdaMethod(this.methods, this.bean, this.allowAccessPrivate);
        return qLambda;
    }
}
