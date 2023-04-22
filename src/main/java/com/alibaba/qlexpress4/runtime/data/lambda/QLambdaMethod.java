package com.alibaba.qlexpress4.runtime.data.lambda;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.QLImplicitMethod;
import com.alibaba.qlexpress4.runtime.util.OptionUtils;
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
    private final boolean allowAccessPrivate;

    public QLambdaMethod(List<Method> methods, Object obj) {
        this.methods = methods;
        this.bean = obj;
        this.allowAccessPrivate = true;
    }

    public QLambdaMethod(List<Method> methods, Object obj, boolean allowAccessPrivate) {
        this.methods = methods;
        this.bean = obj;
        this.allowAccessPrivate = allowAccessPrivate;
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
        QLConvertResult convertResult = ParametersConversion.convert(params, type,
                method.getParameterTypes(), implicitMethod.needImplicitTrans(), implicitMethod.getVars());
        if (convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)) {
            return new QResult(null, QResult.ResultType.RETURN);
        }
        Class<?> clazz;
        if (bean instanceof MetaClass) {
            clazz = ((MetaClass) bean).getClz();
        } else {
            clazz = bean.getClass();
        }
        Object value = MethodHandler.Access.accessMethodValue(OptionUtils.getMethodFromQLOption(
                QLOptions.DEFAULT_OPTIONS, clazz, implicitMethod.getMethod()), bean,
                (Object[]) convertResult.getCastValue(), allowAccessPrivate);
        return new QResult(new DataValue(value), QResult.ResultType.RETURN);
    }
}
