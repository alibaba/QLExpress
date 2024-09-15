package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.MethodInvokeUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: invoke specified method of object on the top of stack
 * Input: ${argNum} + 1
 * Output: 1, method return value, null for void method
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
        Object[] params = new Object[this.argNum];
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
            throw errorReporter.report(new NullPointerException(), QLErrorCodes.NULL_METHOD_ACCESS.name(),
                    QLErrorCodes.NULL_METHOD_ACCESS.getErrorMsg());
        }
        Value invokeRes = MethodInvokeUtils.findMethodAndInvoke(bean, methodName, params, type, qContext.getReflectLoader(), errorReporter);
        qContext.push(invokeRes);
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
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": MethodInvoke " + methodName + " with argNum " + argNum, debug);
    }

    public String getMethodName() {
        return methodName;
    }


}
