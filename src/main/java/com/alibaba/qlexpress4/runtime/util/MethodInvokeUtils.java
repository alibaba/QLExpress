package com.alibaba.qlexpress4.runtime.util;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.IMethod;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersTypeConvertor;

import java.util.Map;

/**
 * Author: DQinYuan
 */
public class MethodInvokeUtils {
    
    public static Value findMethodAndInvoke(Object bean, String methodName, Object[] params, Class<?>[] type,
        ReflectLoader reflectLoader, ErrorReporter errorReporter) {
        IMethod method = reflectLoader.loadMethod(bean, methodName, type);
        if (method == null) {
            QLambda qLambdaInnerMethod = findQLambdaInstance(bean, methodName);
            if (qLambdaInnerMethod != null) {
                try {
                    QResult qResult = qLambdaInnerMethod.call(params);
                    return ValueUtils.toImmutable(qResult.getResult());
                }
                catch (UserDefineException e) {
                    throw ThrowUtils.reportUserDefinedException(errorReporter, e);
                }
                catch (Throwable t) {
                    throw ThrowUtils.wrapThrowable(t,
                        errorReporter,
                        QLErrorCodes.INVOKE_LAMBDA_ERROR.name(),
                        QLErrorCodes.INVOKE_LAMBDA_ERROR.getErrorMsg());
                }
            }
            else {
                throw errorReporter.report(QLErrorCodes.METHOD_NOT_FOUND.name(),
                    String.format(QLErrorCodes.METHOD_NOT_FOUND.getErrorMsg(), methodName));
            }
        }
        else {
            // method invoke
            Object[] convertResult =
                ParametersTypeConvertor.cast(params, method.getParameterTypes(), method.isVarArgs());
            try {
                Object value = MethodHandler.Access.accessMethodValue(method, bean, convertResult);
                return new DataValue(value);
            }
            catch (Exception e) {
                throw ReflectLoader.unwrapMethodInvokeEx(errorReporter, methodName, e);
            }
        }
    }
    
    private static QLambda findQLambdaInstance(Object bean, String methodName) {
        if (bean instanceof Map) {
            Map map = (Map)bean;
            Object mapValue = map.get(methodName);
            if (mapValue instanceof QLambda) {
                return (QLambda)mapValue;
            }
        }
        return null;
    }
    
}
