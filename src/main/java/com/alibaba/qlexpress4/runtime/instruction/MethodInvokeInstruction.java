package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataMethodInvoke;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Operation: invoke specified method of object on the top of stack
 * @Input: 1
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
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Parameters parameters = qRuntime.pop(0);
        Object bean = parameters.get(0).get();
        Object[] params = this.argNum > 0 ? new Object[this.argNum] : null;
        for (int i = 0; i < this.argNum; i++) {
            params[i] = parameters.get(i + 1).get();
        }

        if (bean == null) {
            throw errorReporter.report("GET_METHOD_VALUE_ERROR", "can not get method value from null");
        }
        try {
            Class<?>[] type = BasicUtil.getTypeOfObject(params);
            Object cacheElement = CacheUtil.getMethodCacheElement(bean, this.methodName, type);
            if (cacheElement == null) {
                List<Method> methods;
                if (bean instanceof Class) {
                    methods = PropertiesUtil.getClzMethod((Class<?>) bean, this.methodName, qlOptions.enableAllowAccessPrivateMethod());
                } else {
                    methods = PropertiesUtil.getMethod(bean, this.methodName, qlOptions.enableAllowAccessPrivateMethod());
                }
                Method method = MethodHandler.Preferred.findMostSpecificMethod(type, methods.toArray(new Method[0]));
                Value dataMethodInvoke = new DataMethodInvoke(method, bean, params, qlOptions.enableAllowAccessPrivateMethod());
                Value dataValue = new DataValue(dataMethodInvoke.get());
                qRuntime.push(dataValue);
                if (cacheElement != null) {
                    CacheUtil.setMethodCacheElement(bean, this.methodName, dataValue, type);
                }
            } else {
                qRuntime.push((Value) cacheElement);
            }
        } catch (Exception e) {
            throw errorReporter.report("GET_METHOD_VALUE_ERROR", "can not get method value: " + e.getMessage());
        }
    }
}
