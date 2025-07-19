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
public class PlusPlusSuffixUnaryOperator extends BaseUnaryOperator {
    private static final PlusPlusSuffixUnaryOperator INSTANCE = new PlusPlusSuffixUnaryOperator();
    
    private PlusPlusSuffixUnaryOperator() {
    }
    
    public static PlusPlusSuffixUnaryOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getOperator() {
        return "++";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.UNARY_SUFFIX;
    }
    
    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (!(operand instanceof Number)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }
        
        if (value instanceof LeftValue) {
            ((LeftValue)value).set(NumberMath.add((Number)operand, 1), errorReporter);
        }
        return operand;
    }
}
