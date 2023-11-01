package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

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
        Class<?>[] type = BasicUtil.getTypeOfObject(params);
        Optional<Method> methodOp = reflectLoader.loadMethod(bean, methodName, type);
        if (!methodOp.isPresent()) {
            throw new UserDefineException(UserDefineException.INVALID_ARGUMENT,
                    "method reference '" + methodName + "' not found for argument types " + Arrays.toString(type));
        }
        Method method = methodOp.get();
        Object[] convertResult = ParametersConversion.convert(params, method.getParameterTypes(), method.isVarArgs());
        Object value = MethodHandler.Access.accessMethodValue(method, bean, convertResult);
        return new QResult(new DataValue(value), QResult.ResultType.RETURN);
    }
}
