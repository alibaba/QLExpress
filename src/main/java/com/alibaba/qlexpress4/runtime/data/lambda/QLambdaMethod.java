package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.MethodReflect;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:23
 */
public class QLambdaMethod implements QLambda {

    private final String methodName;
    private final ReflectLoader.PolyMethods polyMethods;
    private final Object bean;

    public QLambdaMethod(String methodName, ReflectLoader.PolyMethods polyMethods, Object obj) {
        this.methodName = methodName;
        this.polyMethods = polyMethods;
        this.bean = obj;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        Class<?>[] type = BasicUtil.getTypeOfObject(params);
        Optional<MethodReflect> methodReflectOp = polyMethods.getMethod(type);
        if (!methodReflectOp.isPresent()) {
            throw new UserDefineException(UserDefineException.INVALID_ARGUMENT,
                    "method reference '" + methodName + "' not found for argument types " + Arrays.toString(type));
        }
        MethodReflect methodReflect = methodReflectOp.get();
        Method method = methodReflect.getMethod();
        QLConvertResult convertResult = ParametersConversion.convert(params, type,
                method.getParameterTypes(), methodReflect.needImplicitTrans(), methodReflect.getVars());
        if(convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)){
            throw new UserDefineException(UserDefineException.INVALID_ARGUMENT,
                    "method reference '" + methodName + "' not found for argument types " + Arrays.toString(type));
        }
        Object value = MethodHandler.Access.accessMethodValue(method, bean, (Object[]) convertResult.getCastValue());
        return new QResult(new DataValue(value), QResult.ResultType.RETURN);
    }
}
