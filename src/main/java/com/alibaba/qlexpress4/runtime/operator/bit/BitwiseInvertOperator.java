package com.alibaba.qlexpress4.runtime.operator.bit;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.number.NumberMath;

/**
 * @author bingo
 */
public class BitwiseInvertOperator extends BaseUnaryOperator {
    private static final BitwiseInvertOperator INSTANCE = new BitwiseInvertOperator();
    
    private BitwiseInvertOperator() {
    }
    
    public static BitwiseInvertOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!(operand instanceof Number)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }
        
        return NumberMath.bitwiseNegate((Number)operand);
    }
    
    @Override
    public String getOperator() {
        return "~";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.UNARY;
    }
}
