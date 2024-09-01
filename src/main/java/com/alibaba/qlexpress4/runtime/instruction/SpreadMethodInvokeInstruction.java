package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.util.MethodInvokeUtils;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Operation: Invoke specified method of each object in the list
 * Input: ${argNum} + 1
 * Output: 1, a list composed of return values from methods.
 * Author: DQinYuan
 */
public class SpreadMethodInvokeInstruction extends QLInstruction {

    private final String methodName;

    private final int argNum;

    public SpreadMethodInvokeInstruction(ErrorReporter errorReporter, String methodName, int argNum) {
        super(errorReporter);
        this.methodName = methodName;
        this.argNum = argNum;
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Parameters parameters = qContext.pop(this.argNum + 1);
        Object traversable = parameters.get(0).get();
        if (traversable == null) {
            if (qlOptions.isAvoidNullPointer()) {
                qContext.push(DataValue.NULL_VALUE);
                return QResult.NEXT_INSTRUCTION;
            }
            throw errorReporter.reportFormat(QLErrorCodes.NONTRAVERSABLE_OBJECT.name(),
                    QLErrorCodes.NONTRAVERSABLE_OBJECT.getErrorMsg(),
                    "null");
        }
        Class<?>[] type = new Class[this.argNum];
        Object[] params = new Object[this.argNum];
        for (int i = 0; i < this.argNum; i++) {
            Value v = parameters.get(i + 1);
            params[i] = v.get();
            type[i] = v.getType();
        }

        if (traversable instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) traversable;
            List<? super Object> result = new ArrayList<>();
            for (Object item : iterable) {
                if (item == null) {
                    if (qlOptions.isAvoidNullPointer()) {
                        result.add(null);
                        continue;
                    }
                    throw errorReporter.report(new NullPointerException(), QLErrorCodes.GET_METHOD_FROM_NULL.name(),
                            QLErrorCodes.GET_METHOD_FROM_NULL.getErrorMsg());
                }
                Value invokeRes = MethodInvokeUtils.findMethodAndInvoke(item, methodName, params, type,
                        qContext.getReflectLoader(), errorReporter);
                result.add(invokeRes.get());
            }
            qContext.push(new DataValue(result));
        } else if (traversable.getClass().isArray()) {
            int arrLen = Array.getLength(traversable);
            List<? super Object> result = new ArrayList<>();
            for (int i = 0; i < arrLen; i++) {
                Object item = Array.get(traversable, i);
                if (item == null) {
                    if (qlOptions.isAvoidNullPointer()) {
                        result.add(null);
                        continue;
                    }
                    throw errorReporter.report(new NullPointerException(), QLErrorCodes.GET_METHOD_FROM_NULL.name(),
                            QLErrorCodes.GET_METHOD_FROM_NULL.getErrorMsg());
                }
                Value invokeRes = MethodInvokeUtils.findMethodAndInvoke(item, methodName, params, type,
                        qContext.getReflectLoader(), errorReporter);
                result.add(invokeRes.get());
            }
            qContext.push(new DataValue(result));
        } else {
            throw errorReporter.reportFormat(QLErrorCodes.NONTRAVERSABLE_OBJECT.name(),
                    QLErrorCodes.NONTRAVERSABLE_OBJECT.getErrorMsg(),
                    traversable.getClass().getName());
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
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": SpreadMethodInvoke " + methodName, debug);
    }
}
