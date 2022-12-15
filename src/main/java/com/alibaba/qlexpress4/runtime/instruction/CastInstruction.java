package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.utils.PrintlnUtils;
import java.util.function.Consumer;
import com.alibaba.qlexpress4.runtime.data.convert.InstanceConversion;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResult;
import com.alibaba.qlexpress4.runtime.data.implicit.QLConvertResultType;

/**
 * @Operation: force cast value to specified type
 * @Input: 2 targetCls and value
 * @Output: 1 casted value
 *
 * Author: DQinYuan
 */
public class CastInstruction extends QLInstruction {

    public CastInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public QResult execute(QContext qContext, QLOptions qlOptions) {
        Object value = qContext.pop().get();
        Class<?> targetClz = popTargetClz(qContext.pop().get());
        if (value == null) {
            qContext.push(Value.NULL_VALUE);
            return QResult.NEXT_INSTRUCTION;
        }
        QLConvertResult result = InstanceConversion.castObject(value, targetClz);
        if(result.getResultType().equals(QLConvertResultType.NOT_TRANS)){
            throw errorReporter.report("CAST_OBJECT_ERROR", "can not cast from this type");
        }
        Value dataCast = new DataValue(result.getCastValue());
        qContext.push(dataCast);
        return QResult.NEXT_INSTRUCTION;
    }

    private Class<?> popTargetClz(Object target) {
        if (target instanceof MetaClass) {
            return ((MetaClass) target).getClz();
        } else if (target instanceof Class) {
            return (Class<?>) target;
        }
        throw errorReporter.reportFormat("INVALID_CAST_TARGET",
                "cast target must be a class, but accept %s",
                target == null? "null": target.getClass().getName());
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
    public void println(int depth, Consumer<String> debug) {
        PrintlnUtils.printlnByCurDepth(depth, "Cast", debug);
    }
}
