package com.alibaba.qlexpress4.runtime.operator;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * 是否支持BigDecimal等类型的++
 *
 * @author 冰够
 */
@Deprecated
public class MinusMinusOperator extends BaseUnaryOperator {
    public MinusMinusOperator() {
        super("--");
    }

    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!(operand instanceof Number)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }

        return NumberMath.subtract((Number)operand, 1);
    }
}
