package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMethod;
import com.alibaba.qlexpress4.utils.BasicUtil;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author TaoKan
 * @Date 2022/5/15 下午8:23
 */
public class QLambdaMethod implements QLambda {

    private final List<Method> methods;
    private final Object bean;
    private final QLOptions qlOptions;

    public QLambdaMethod(List<Method> methods, Object obj, QLOptions qlOptions) {
        this.methods = methods;
        this.bean = obj;
        this.qlOptions = qlOptions;
    }

    @Override
    public QResult call(Object... params) throws Exception {
        if (methods == null || methods.size() == 0) {
            return new QResult(null, QResult.ResultType.RETURN);
        }

        Class<?>[] type = BasicUtil.getTypeOfObject(params);
        QLImplicitMethod implicitMethod = MethodHandler.Preferred.findMostSpecificMethod(type, this.methods.toArray(new Method[0]));
        Method method = implicitMethod.getMethod();
        if (implicitMethod == null) {
            return new QResult(null, QResult.ResultType.RETURN);
        }
        if (BasicUtil.isPublic(method)) {
            QLConvertResult convertResult = ParametersConversion.convert(params, type,
                    method.getParameterTypes(), implicitMethod.needImplicitTrans(), implicitMethod.getVars());
            if(convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)){
                return new QResult(null, QResult.ResultType.RETURN);
            }
            try {
                Object value = MethodHandler.Access.accessMethodValue(implicitMethod.getMethod(),bean,
                        (Object[]) convertResult.getCastValue(),qlOptions);
                return new QResult(new DataValue(value), QResult.ResultType.RETURN);
            }catch (Throwable t){
                throw new RuntimeException(t);
            }
        } else {
            if (!qlOptions.enableAllowAccessPrivateMethod()) {
                throw new RuntimeException("QLambdaMethod not accessible");
            } else {
                synchronized (method) {
                    try {
                        method.setAccessible(true);
                        Object result = method.invoke(this.bean, params);
                        return new QResult(new DataValue(result), QResult.ResultType.RETURN);
                    } finally {
                        method.setAccessible(false);
                    }
                }
            }
        }
    }
}
