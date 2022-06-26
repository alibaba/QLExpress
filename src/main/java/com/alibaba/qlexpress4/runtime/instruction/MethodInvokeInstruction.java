package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Operation: invoke specified method of object on the top of stack
 * @Input: ${argNum} + 1
 * @Output: 1, method return value, null for void method
 * <p>
 * equivalent to GetMethodInstruction + CallInstruction
 * <p>
 * Author: DQinYuan
 */
public class MethodInvokeInstruction extends QLInstruction {

    private final String methodName;

    private final int argNum;

    public MethodInvokeInstruction(ErrorReporter errorReporter, String methodName, int argNum) {
        super(errorReporter);
        this.methodName = methodName;
        this.argNum = argNum;
    }

    @Override
    public QResult execute(QRuntime qRuntime, QLOptions qlOptions) {
        Parameters parameters = qRuntime.pop(this.argNum + 1);
        Object bean = parameters.get(0).get();
        Object[] params = this.argNum > 0 ? new Object[this.argNum] : null;
        for (int i = 0; i < this.argNum; i++) {
            params[i] = parameters.get(i + 1).get();
        }
        if (bean == null) {
            throw errorReporter.report("GET_METHOD_VALUE_ERROR", "can not get method value from null");
        }
        Class<?>[] type = BasicUtil.getTypeOfObject(params);
        Method method;
        if(bean instanceof Class){
            method = getClazzMethod(bean, type, qlOptions.enableAllowAccessPrivateMethod());
        }else {
            method = getInstanceMethod(bean, type, qlOptions.enableAllowAccessPrivateMethod());
        }
        try {
            Object value = MethodHandler.Access.accessMethodValue(method,bean,params,qlOptions.enableAllowAccessPrivateMethod());
            Value dataValue = new DataValue(value);
            qRuntime.push(dataValue);
        }catch (Exception e){
            throw errorReporter.report("GET_METHOD_VALUE_ERROR", "can not allow access method");
        }
        return QResult.CONTINUE_RESULT;
    }

    @Override
    public int stackInput() {
        return argNum + 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    public Method getClazzMethod(Object bean, Class<?>[] type, boolean enableAllowAccessPrivateMethod){
        Method cacheElement = CacheUtil.getMethodInvokeCacheElement((Class<?>) bean , this.methodName, type);
        if (cacheElement == null) {
            List<Method> methods = PropertiesUtil.getClzMethod((Class<?>) bean, this.methodName, enableAllowAccessPrivateMethod);
            Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
            if(method == null){
                throw errorReporter.report("GET_METHOD_VALUE_ERROR", "method not exists");
            }
            CacheUtil.setMethodInvokeCacheElement((Class<?>)bean, this.methodName, method, type);
            return method;
        }else {
            return cacheElement;
        }
    }

    public Method getInstanceMethod(Object bean, Class<?>[] type, boolean enableAllowAccessPrivateMethod){
        Method cacheElement = CacheUtil.getMethodInvokeCacheElement(bean.getClass() , this.methodName, type);
        if (cacheElement == null) {
            List<Method> methods = PropertiesUtil.getMethod(bean, this.methodName, enableAllowAccessPrivateMethod);
            Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
            if(method == null){
                throw errorReporter.report("GET_METHOD_VALUE_ERROR", "method not exists");
            }
            CacheUtil.setMethodInvokeCacheElement(bean.getClass(), this.methodName, method, type);
            return method;
        }else {
            return cacheElement;
        }
    }

}
