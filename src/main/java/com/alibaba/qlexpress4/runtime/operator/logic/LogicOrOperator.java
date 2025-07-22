package com.alibaba.qlexpress4.runtime.operator.logic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * @author bingo
 */
public class LogicOrOperator extends BaseBinaryOperator {
    private static final Map<String, LogicOrOperator> INSTANCE_CACHE = new ConcurrentHashMap<>(2);
    
    static {
        INSTANCE_CACHE.put("||", new LogicOrOperator("||"));
        INSTANCE_CACHE.put("or", new LogicOrOperator("or"));
    }
    
    private final String operator;
    
    private LogicOrOperator(String operator) {
        this.operator = operator;
    }
    
    public static LogicOrOperator getInstance(String operator) {
        return INSTANCE_CACHE.get(operator);
    }
    
    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }
        
        return (Boolean)leftValue || (Boolean)rightValue;
    }
    
    @Override
    public String getOperator() {
        return operator;
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.OR;
    }
}
