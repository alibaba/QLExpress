package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Operation: get specified method of object on the top of stack
 * @Input: 1
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class GetMethodInstruction extends QLInstruction {

    private final String methodName;

    public GetMethodInstruction(ErrorReporter errorReporter, String methodName) {
        super(errorReporter);
        this.methodName = methodName;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = qRuntime.pop().get();
        if (bean == null) {
            throw this.errorReporter.report("GET_METHOD_ERROR", "can not get method from null");
        }
        QLCaches qlCaches = qRuntime.getQLCaches();
        DataValue dataMethod = bean instanceof MetaClass?
                getClazzMethod(qlCaches, ((MetaClass) bean).getClz(), qlOptions.enableAllowAccessPrivateMethod()):
                getInstanceMethod(qlCaches, bean, qlOptions.enableAllowAccessPrivateMethod());
        qRuntime.push(dataMethod);
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    public DataValue getClazzMethod(QLCaches qlCaches, Object bean, boolean enableAllowAccessPrivateMethod){
        List<Method> cacheElement = CacheUtil.getMethodCacheElement(qlCaches.getQlMethodCache(), (Class<?>) bean , this.methodName);
        if (cacheElement == null) {
            List<Method> methods = PropertiesUtil.getClzMethod((Class<?>) bean, this.methodName, enableAllowAccessPrivateMethod);
            if (methods.size() == 0) {
                throw this.errorReporter.report("GET_METHOD_ERROR", "method not exists");
            }
            QLambda qLambda = new QLambdaMethod(methods, bean, enableAllowAccessPrivateMethod);
            DataValue dataMethod = new DataValue(qLambda, QLambda.class);
            CacheUtil.setMethodCacheElement(qlCaches.getQlMethodCache(), (Class<?>) bean, this.methodName, methods);
            return dataMethod;
        }else {
            QLambda qLambda = new QLambdaMethod(cacheElement, bean, enableAllowAccessPrivateMethod);
            return new DataValue(qLambda);
        }
    }

    public DataValue getInstanceMethod(QLCaches qlCaches, Object bean, boolean enableAllowAccessPrivateMethod) {
        List<Method> cacheElement = CacheUtil.getMethodCacheElement(qlCaches.getQlMethodCache(), bean.getClass(), this.methodName);
        if (cacheElement == null) {
            List<Method> methods = PropertiesUtil.getMethod(bean, this.methodName, enableAllowAccessPrivateMethod);
            if (methods.size() == 0) {
                throw this.errorReporter.report("GET_METHOD_ERROR", "method not exists");
            }
            QLambda qLambda = new QLambdaMethod(methods, bean, enableAllowAccessPrivateMethod);
            DataValue dataMethod = new DataValue(qLambda, QLambda.class);
            CacheUtil.setMethodCacheElement(qlCaches.getQlMethodCache(), bean.getClass(), this.methodName, methods);
            return dataMethod;
        } else {
            QLambda qLambda = new QLambdaMethod(cacheElement, bean, enableAllowAccessPrivateMethod);
            return new DataValue(qLambda);
        }
    }

}
