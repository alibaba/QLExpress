package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

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
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {
        Object bean = parameters.get(0).get();
        if(bean == null){
            throw errorReporter.report("GET_METHOD_INPUT_BEAN_NULL","input parameters is null");
        }
        try {
            Object cacheElement = CacheUtils.getMethodCacheElement(bean,this.methodName);
            if(cacheElement == null){
                List<Method> methods;
                if (bean instanceof Class) {
                    methods = PropertiesUtils.getClzMethod((Class<?>)bean,methodName, qlOptions.isAllowAccessPrivateMethod());
                }else {
                    methods = PropertiesUtils.getMethod(bean, methodName, qlOptions.isAllowAccessPrivateMethod());
                }
                Value dataMethod = new DataMethod(methods, this.methodName, bean ,qlOptions.isAllowAccessPrivateMethod());
                qRuntime.push(dataMethod);
                if(cacheElement!=null){
                    CacheUtils.setMethodCacheElement(bean,this.methodName,dataMethod);
                }
            }else {
                qRuntime.push((Value) cacheElement);
            }
        } catch (Exception e){
            throw errorReporter.report("GET_METHOD_INPUT_BEAN_NULL","UnExpected exception: "+e.getMessage());
        }
    }
}
