package com.alibaba.qlexpress4.runtime.operator.unary;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author bingo
 */
public class MinusMinusPrefixUnaryOperator extends BaseUnaryOperator {
    private static final MinusMinusPrefixUnaryOperator INSTANCE = new MinusMinusPrefixUnaryOperator();
    
    private MinusMinusPrefixUnaryOperator() {
    }
    
    public static MinusMinusPrefixUnaryOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getOperator() {
        return "--";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.UNARY;
    }
    
    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!(operand instanceof Number)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }
        
        if (value instanceof LeftValue) {
            ((LeftValue)value).set(NumberMath.subtract((Number)operand, 1), errorReporter);
        }
        return operand;
    }
}
