package com.alibaba.qlexpress4.runtime.operator.object;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;
import com.alibaba.qlexpress4.utils.PropertiesUtil;

/**
 * Author: DQinYuan
 */
public class OptionalChainingOperator extends BaseBinaryOperator {

    private static final OptionalChainingOperator INSTANCE = new OptionalChainingOperator();

    public static BinaryOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions, ErrorReporter errorReporter) {
        if (!(right instanceof LeftValue)) {
            throw errorReporter.report("INVALID_FIELD", "invalid field");
        }
        LeftValue field = (LeftValue) right;
        String fieldName = field.getSymbolName();
        if (fieldName == null) {
            throw errorReporter.report("INVALID_FIELD", "invalid field");
        }
        Object bean = left.get();
        if (bean == null) {
            return null;
        }
        Value fieldValue = PropertiesUtil.getField(bean, fieldName, qRuntime.getQLCaches().getQlFieldCache(),
                errorReporter, qlOptions.allowAccessPrivateMethod());
        return fieldValue.get();
    }

    @Override
    public String getOperator() {
        return "?.";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.GROUP;
    }
}
