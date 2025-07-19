package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.IMethod;
import com.alibaba.qlexpress4.runtime.JvmIMethod;
import com.alibaba.qlexpress4.runtime.MemberResolver;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersTypeConvertor;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Author: DQinYuan
 */
public class QMethodFunction implements CustomFunction {
    
    private final Object object;
    
    private final IMethod method;
    
    public QMethodFunction(Object object, Method method) {
        this.object = object;
        this.method = new JvmIMethod(method);
    }
    
    @Override
    public Object call(QContext qContext, Parameters parameters)
        throws Throwable {
        Object[] argumentsArr = BasicUtil.argumentsArr(parameters);
        Class<?>[] typeArr = BasicUtil.getTypeOfObject(argumentsArr);
        
        int priority = MemberResolver.resolvePriority(method.getParameterTypes(), typeArr);
        if (priority == MemberResolver.MatchPriority.MISMATCH.priority) {
            throw new UserDefineException(UserDefineException.ExceptionType.INVALID_ARGUMENT,
                "invalid argument types " + Arrays.toString(typeArr) + " for java method '" + method.getName() + "'"
                    + " in declaring java class '" + method.getDeclaringClass().getName() + "'");
        }
        Object[] convertResult =
            ParametersTypeConvertor.cast(argumentsArr, method.getParameterTypes(), method.isVarArgs());
        return MethodHandler.Access.accessMethodValue(method, object, convertResult);
    }
}
