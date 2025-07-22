package com.alibaba.qlexpress4.runtime.operator.collection;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class NotInOperator extends BaseBinaryOperator {
    private static final NotInOperator INSTANCE = new NotInOperator();
    
    private NotInOperator() {
    }
    
    public static NotInOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return !in(left, right, errorReporter);
    }
    
    @Override
    public String getOperator() {
        return "not_in";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.IN_LIKE;
    }
}
