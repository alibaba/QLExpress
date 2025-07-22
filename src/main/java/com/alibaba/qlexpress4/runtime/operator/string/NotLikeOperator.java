package com.alibaba.qlexpress4.runtime.operator.string;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class NotLikeOperator extends BaseBinaryOperator {
    private static final NotLikeOperator INSTANCE = new NotLikeOperator();
    
    public static BinaryOperator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return !like(left, right, errorReporter);
    }
    
    @Override
    public String getOperator() {
        return "not_like";
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.IN_LIKE;
    }
}
