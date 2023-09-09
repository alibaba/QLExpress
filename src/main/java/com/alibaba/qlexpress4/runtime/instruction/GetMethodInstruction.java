package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @Operation: get specified method of object on the top of stack
 * @Input: 1
 * @Output: 1
 * <p>
 * Author: DQinYuan
 */
public class GetMethodInstruction extends QLInstruction {

    private final String methodName;

    public GetMethodInstruction(ErrorReporter errorReporter, String methodName) {
        super(errorReporter);
        this.methodName = methodName;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object bean = qContext.pop().get();
        if (bean == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw this.errorReporter.report(new NullPointerException(),
                    "GET_METHOD_FROM_NULL", "can not get method from null");
        }
        ReflectLoader reflectLoader = qContext.getReflectLoader();
        Optional<ReflectLoader.PolyMethods> polyMethodsOp = reflectLoader.loadMethod(bean, methodName);
        if (!polyMethodsOp.isPresent()) {
            throw errorReporter.report("METHOD_NOT_FOUND", "'" + methodName + "' not found");
        }

        qContext.push(new DataValue(new QLambdaMethod(methodName, polyMethodsOp.get(), bean)));
        return QResult.NEXT_INSTRUCTION;
    }

    @Override
    public int stackInput() {
        return 1;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": GetMethod " + methodName, debug);
    }

}
