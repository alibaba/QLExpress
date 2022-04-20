package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataMethod;
import com.alibaba.qlexpress4.runtime.data.DataMethodInvoke;
import com.alibaba.qlexpress4.utils.CacheUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @Operation: get specified method of object on the top of stack
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class GetMethodInstruction extends QLInstruction {

    private final String methodName;

    public GetMethodInstruction(ErrorReporter errorReporter, String methodName) {
        super(errorReporter);
        this.methodName = methodName;
    }

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = parameters.get(0).get();
        if(bean == null){
            throw errorReporter.report("GET_METHOD_INPUT_BEAN_NULL","input parameters is null");
        }

        LeftValue dataMethod = new DataMethod();
        try {
            List<Method> methodValue = CacheUtils.getMethodWithCache(bean, this.methodName, null, qlOptions.isAllowAccessPrivateMethod());
            QLambda qLambda = new QLambda(this.methodName, null , null);
            dataMethod.set(qLambda);
            qRuntime.push(dataMethod);
        } catch (InvocationTargetException e) {
            throw errorReporter.report("GET_METHOD_VALUE_ERROR","InvocationTargetException: "+e.getMessage());
        } catch (IllegalAccessException e) {
            throw errorReporter.report("GET_METHOD_VALUE_ERROR","IllegalAccessException: "+e.getMessage());
        } catch (Exception e){
            throw errorReporter.report("GET_METHOD_VALUE_ERROR","UnExpectedException: "+e.getMessage());
        }

    }
}
