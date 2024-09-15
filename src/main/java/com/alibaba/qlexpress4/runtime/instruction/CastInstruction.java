package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.convert.ObjTypeConvertor;
import com.alibaba.qlexpress4.utils.PrintlnUtils;

import java.util.function.Consumer;

/**
 * Operation: force cast value to specified type
 * Input: 2 targetCls and value
 * Output: 1 casted value
 * <p>
 * Author: DQinYuan
 */
public class CastInstruction extends QLInstruction {

    public CastInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Value value = qContext.pop();
        Class<?> targetClz = popTargetClz(qContext.pop().get());
        if (value == null) {
            qContext.push(Value.NULL_VALUE);
            return QResult.NEXT_INSTRUCTION;
        }
        ObjTypeConvertor.QConverted result = ObjTypeConvertor.cast(value.get(), targetClz);
        if (!result.isConvertible()) {
            throw errorReporter.reportFormat(QLErrorCodes.INCOMPATIBLE_TYPE_CAST.name(),
                    QLErrorCodes.INCOMPATIBLE_TYPE_CAST.getErrorMsg(), value.getTypeName(), targetClz.getName());
        }
        Value dataCast = new DataValue(result.getConverted());
        qContext.push(dataCast);
        return QResult.NEXT_INSTRUCTION;
    }

    private Class<?> popTargetClz(Object target) {
        if (target instanceof MetaClass) {
            return ((MetaClass) target).getClz();
        } else if (target instanceof Class) {
            return (Class<?>) target;
        }
        throw errorReporter.reportFormat(QLErrorCodes.INVALID_CAST_TARGET.name(),
                QLErrorCodes.INVALID_CAST_TARGET.getErrorMsg(),
                target == null ? "null" : target.getClass().getName());
    }

    @Override
    public int stackInput() {
        return 2;
    }

    @Override
    public int stackOutput() {
        return 1;
    }

    @Override
    public void println(int index, int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, index + ": Cast", debug);
    }
}
