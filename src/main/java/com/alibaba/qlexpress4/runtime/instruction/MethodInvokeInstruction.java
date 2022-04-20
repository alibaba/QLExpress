package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.DataField;
import com.alibaba.qlexpress4.runtime.data.DataMethod;
import com.alibaba.qlexpress4.runtime.data.DataMethodInvoke;
import com.alibaba.qlexpress4.utils.CacheUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @Operation: invoke specified method of object on the top of stack
 * @Input: 1
 * @Output: 1, method return value, null for void method
 *
 * equivalent to GetMethodInstruction + CallInstruction
 *
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
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = parameters.get(0).get();

        if(bean == null){
            throw errorReporter.report("GET_METHOD_INVOKE_INPUT_BEAN_NULL","input parameters is null");
        }

        Object[] params = this.argNum > 0 ? new Object[this.argNum] : null;
        for(int i = 0; i < this.argNum; i++){
            params[i] = parameters.get(i+1).get();
        }

        Object methodValue = null;
        LeftValue dataMethodInvoke = new DataMethodInvoke();
        try {
            methodValue = CacheUtils.getMethodCacheElement(bean, this.methodName, params, qlOptions.isAllowAccessPrivateMethod());
            dataMethodInvoke.set(methodValue);
            qRuntime.push(dataMethodInvoke);
        } catch (InvocationTargetException e) {
            throw errorReporter.report("GET_METHOD_INVOKE_VALUE_ERROR","InvocationTargetException: "+e.getMessage());
        } catch (IllegalAccessException e) {
            throw errorReporter.report("GET_METHOD_INVOKE_VALUE_ERROR","IllegalAccessException: "+e.getMessage());
        } catch (Exception e){
            throw errorReporter.report("GET_METHOD_INVOKE_VALUE_ERROR","UnExpectedException: "+e.getMessage());
        }

    }
}
