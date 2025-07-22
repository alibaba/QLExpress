package com.alibaba.qlexpress4.runtime.operator.arithmetic;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class DivideOperator extends BaseBinaryOperator {
    private static final DivideOperator INSTANCE = new DivideOperator();
    
    private DivideOperator() {
    }
    
    public static DivideOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return divide(left, right, qlOptions, errorReporter);
    }
    
    @Override
    public String getOperator() {
        return "/";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.MULTI;
    }
}