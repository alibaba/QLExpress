package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.member.MethodHandler;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ParametersConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;
import com.alibaba.qlexpress4.runtime.data.implicit.MethodReflect;
import com.alibaba.qlexpress4.runtime.util.ThrowUtils;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Operation: invoke specified method of object on the top of stack
 * @Input: ${argNum} + 1
 * @Output: 1, method return value, null for void method
 * <p>
 * equivalent to GetMethodInstruction + CallInstruction
 * <p>
 * Author: DQinYuan
 */
public class MethodInvokeInstruction extends QLInstruction {

    private final String methodName;

    private final int argNum;

    private final boolean optional;

    public MethodInvokeInstruction(ErrorReporter errorReporter, String methodName, int argNum, boolean optional) {
        super(errorReporter);
        this.methodName = methodName;
        this.argNum = argNum;
        this.optional = optional;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters parameters = qContext.pop(this.argNum + 1);
        Object bean = parameters.get(0).get();
        Class<?>[] type = new Class[this.argNum];
        Object[] params = this.argNum > 0 ? new Object[this.argNum] : null;
        for (int i = 0; i < this.argNum; i++) {
            Value v = parameters.get(i + 1);
            params[i] = v.get();
            type[i] = v.getType();
        }
        if (bean == null) {
            if (qlOptions.isAvoidNullPointer() || optional) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.report(new NullPointerException(),
                    "GET_METHOD_FROM_NULL", "can not get method from null");
        }
        ReflectLoader reflectLoader = qContext.getReflectLoader();
        Optional<ReflectLoader.PolyMethods> polyMethodsOp = reflectLoader.loadMethod(bean, methodName);
        Optional<MethodReflect> methodReflectOp = polyMethodsOp.flatMap(polyMethods -> polyMethods.getMethod(type));
        if (!methodReflectOp.isPresent()) {
            QLambda qLambdaInnerMethod = findQLambdaInstance(bean);
            if (qLambdaInnerMethod != null) {
                try {
                    QResult qResult = qLambdaInnerMethod.call(params);
                    Value dataValue = new DataValue(qResult.getResult());
                    qContext.push(dataValue);
                } catch (UserDefineException e) {
                    throw ThrowUtils.reportUserDefinedException(errorReporter, e);
                } catch (Throwable t) {
                    throw ThrowUtils.wrapThrowable(t, errorReporter,
                            "LAMBDA_EXECUTE_EXCEPTION", "lambda execute exception");
                }
            } else {
                throw errorReporter.report("METHOD_NOT_FOUND", "method '" + methodName + "' not found");
            }
        } else {
            // method invoke
            MethodReflect methodReflect = methodReflectOp.get();
            QLConvertResult convertResult = ParametersConversion.convert(params, type, methodReflect.getMethod().getParameterTypes()
                    , methodReflect.needImplicitTrans(), methodReflect.getVars());
            if (convertResult.getResultType().equals(QLConvertResultType.NOT_TRANS)) {
                throw errorReporter.report("GET_METHOD_VALUE_CAST_PARAM_ERROR", "can not cast param");
            }
            Method method = methodReflect.getMethod();
            if (!BasicUtil.isPublic(method)) {
                method.setAccessible(true);
            }
            try {
                Object value = MethodHandler.Access.accessMethodValue(methodReflect.getMethod(), bean,
                        (Object[]) convertResult.getCastValue());
                Value dataValue = new DataValue(value);
                qContext.push(dataValue);
            } catch (Exception e) {
                throw ReflectLoader.unwrapMethodInvokeEx(errorReporter, methodName, e);
            }
        }

        return QResult.NEXT_INSTRUCTION;
    }


    @Override
    public int stackInput() {
        return argNum + 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "MethodInvoke " + methodName + " with argNum " + argNum, debug);
    }

    public String getMethodName() {
        return methodName;
    }

    protected QLambda findQLambdaInstance(Object bean) {
        if (bean instanceof Map) {
            Map map = (Map) bean;
            Object mapValue = map.get(methodName);
            if (mapValue instanceof QLambda) {
                return (QLambda) mapValue;
            }
        }
        return null;
    }
}
