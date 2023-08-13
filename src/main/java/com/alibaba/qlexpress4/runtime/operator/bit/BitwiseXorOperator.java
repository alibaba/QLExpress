package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author 冰够
 */
public class BitwiseXorOperator extends BaseBinaryOperator {
    private static final BitwiseXorOperator INSTANCE = new BitwiseXorOperator();

    private BitwiseXorOperator() {
    }

    public static BitwiseXorOperator getInstance() {
        return INSTANCE;
    }

    @Override
    public String getOperator() {
        return "^";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.XOR;
    }

    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions, ErrorReporter errorReporter) {
        return bitwiseXor(left, right, errorReporter);
    }
}
