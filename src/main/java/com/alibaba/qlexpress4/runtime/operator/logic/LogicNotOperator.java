package com.alibaba.qlexpress4.runtime.operator.logic;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseUnaryOperator;

/**
 * @author bingo
 */
public class LogicNotOperator extends BaseUnaryOperator {
    private static final LogicNotOperator INSTANCE = new LogicNotOperator();
    
    private LogicNotOperator() {
    }
    
    public static LogicNotOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Object execute(Value value, ErrorReporter errorReporter) {
        Object operand = value.get();
        if (operand == null) {
            operand = false;
        }
        if (!(operand instanceof Boolean)) {
            throw buildInvalidOperandTypeException(value, errorReporter);
        }
        
        return !(Boolean)operand;
    }
    
    @Override
    public String getOperator() {
        return "!";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.UNARY;
    }
}
