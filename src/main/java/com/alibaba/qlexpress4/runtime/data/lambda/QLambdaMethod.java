package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.IMethod;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersTypeConvertor;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.util.Arrays;

/**
 * Author: TaoKan
 */
public class QLambdaMethod implements QLambda {

    private final String methodName;
    private final ReflectLoader reflectLoader;
    private final Object bean;

    public QLambdaMethod(String methodName, ReflectLoader reflectLoader, Object obj) {
        this.methodName = methodName;
        this.reflectLoader = reflectLoader;
        this.bean = obj;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        if (bean instanceof MetaClass) {
            Class<?>[] type = BasicUtil.getTypeOfObject(params);
            IMethod method = reflectLoader.loadMethod(bean, methodName, type);
            if (method != null) {
                // static method
                Object[] convertResult = ParametersTypeConvertor.cast(params, method.getParameterTypes(), method.isVarArgs());
                Object value = MethodHandler.Access.accessMethodValue(method, bean, convertResult);
                return new QResult(new DataValue(value), QResult.ResultType.RETURN);
            }
            if (params.length < 1) {
                throw createMethodNotFoundException(type);
            }
            if (((MetaClass)bean).getClz() != params[0].getClass()) {
                throw createMethodNotFoundException(type);
            }
            Class<?>[] restParamsType = Arrays.copyOfRange(type, 1, type.length);
            method = reflectLoader.loadMethod(params[0], methodName, restParamsType);
            if (method == null) {
                throw createMethodNotFoundException(restParamsType);
            }
            Object paramBean = params[0];
            Object[] restParams = Arrays.copyOfRange(params, 1, params.length);
            Object[] convertResult = ParametersTypeConvertor.cast(restParams, method.getParameterTypes(), method.isVarArgs());
            Object value = MethodHandler.Access.accessMethodValue(method, paramBean, convertResult);
            return new QResult(new DataValue(value), QResult.ResultType.RETURN);
        } else {
            Class<?>[] type = BasicUtil.getTypeOfObject(params);
            IMethod method = reflectLoader.loadMethod(bean, methodName, type);
            if (method == null) {
                throw createMethodNotFoundException(type);
            }
            Object[] convertResult = ParametersTypeConvertor.cast(params, method.getParameterTypes(), method.isVarArgs());
            Object value = MethodHandler.Access.accessMethodValue(method, bean, convertResult);
            return new QResult(new DataValue(value), QResult.ResultType.RETURN);
        }
    }

    /**
     * Create method not found exception
     * @param type parameter type array
     * @return UserDefineException exception object
     */
    private UserDefineException createMethodNotFoundException(Class<?>[] type) {
        return new UserDefineException(UserDefineException.ExceptionType.INVALID_ARGUMENT,
                "method reference '" + methodName + "' not found for argument types " + Arrays.toString(type));
    }
}
