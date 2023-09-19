package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.MethodReflect;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Author: DQinYuan
 */
public class QMethodFunction implements CustomFunction {

    private final Object object;

    private final Method method;

    public QMethodFunction(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    @Override
    public Object call(QContext qContext, Parameters parameters) throws Throwable {
        Object[] argumentsArr = BasicUtil.argumentsArr(parameters);
        Class<?>[] typeArr = BasicUtil.getTypeOfObject(argumentsArr);
        MethodReflect methodReflect = MethodHandler.Preferred
                .findMostSpecificMethod(typeArr, new Method[]{method});
        if (methodReflect == null) {
            throw new UserDefineException(UserDefineException.INVALID_ARGUMENT,
                    "invalid argument types " + Arrays.toString(typeArr) + " for java method '" + method.getName() + "'"
                    + " in declaring java class '" + method.getDeclaringClass().getName() + "'"
            );
        }
        QLConvertResult convertResult = ParametersConversion.convert(argumentsArr, typeArr, method.getParameterTypes(),
                methodReflect.needImplicitTrans(), methodReflect.getVars());
        return MethodHandler.Access.accessMethodValue(method, object, argumentsArr);
    }
}
