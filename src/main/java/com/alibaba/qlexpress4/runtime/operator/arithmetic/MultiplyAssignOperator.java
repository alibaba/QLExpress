package com.alibaba.qlexpress4.runtime.operator.arithmetic;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.LeftValue;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class MultiplyAssignOperator extends BaseBinaryOperator {
    private static final MultiplyAssignOperator INSTANCE = new MultiplyAssignOperator();
    
    private MultiplyAssignOperator() {
    }
    
    public static MultiplyAssignOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        assertLeftValue(left, errorReporter);
        LeftValue leftValue = (LeftValue)left;
        Object result = multiply(left, right, qlOptions, errorReporter);
        leftValue.set(result, errorReporter);
        return result;
    }
    
    @Override
    public String getOperator() {
        return "*=";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.ASSIGN;
    }
}
