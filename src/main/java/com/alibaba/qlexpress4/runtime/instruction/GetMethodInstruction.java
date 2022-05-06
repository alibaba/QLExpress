package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataMethod;
import com.alibaba.qlexpress4.utils.CacheUtils;
import com.alibaba.qlexpress4.utils.PropertiesUtils;

import java.lang.reflect.Method;
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
        Object bean = qRuntime.pop(0).get(0).get();
        if(bean == null){
            throw errorReporter.report("GET_METHOD_ERROR","can not get method from null");
        }
        try {
            Object cacheElement = CacheUtils.getMethodCacheElement(bean,this.methodName);
            if(cacheElement == null){
                List<Method> methods;
                if (bean instanceof Class) {
                    methods = PropertiesUtils.getClzMethod((Class<?>)bean, methodName, qlOptions.isAllowAccessPrivateMethod());
                }else {
                    methods = PropertiesUtils.getMethod(bean, methodName, qlOptions.isAllowAccessPrivateMethod());
                }
                Value dataMethod = new DataMethod(methods, bean, qlOptions.isAllowAccessPrivateMethod());
                qRuntime.push(dataMethod);
                if(cacheElement!=null){
                    CacheUtils.setMethodCacheElement(bean,this.methodName,dataMethod);
                }
            }else {
                qRuntime.push((Value) cacheElement);
            }
        } catch (Exception e){
            throw errorReporter.report("GET_METHOD_ERROR","can not get method: "+e.getMessage());
        }
    }
}
