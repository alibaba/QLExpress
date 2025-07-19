package com.alibaba.qlexpress4.runtime.operator.compare;

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
public class UnequalOperator extends BaseBinaryOperator {
    private static final Map<String, UnequalOperator> INSTANCE_CACHE = new ConcurrentHashMap<>(2);
    
    static {
        INSTANCE_CACHE.put("!=", new UnequalOperator("!="));
        INSTANCE_CACHE.put("<>", new UnequalOperator("<>"));
    }
    
    private final String operator;
    
    private UnequalOperator(String operator) {
        this.operator = operator;
    }
    
    public static UnequalOperator getInstance(String operator) {
        return INSTANCE_CACHE.get(operator);
    }
    
    @Override
    public String getOperator() {
        return operator;
    }
    
    @Override
    public int getPriority() {
        return QLPrecedences.EQUAL;
    }
    
    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return !equals(left, right, errorReporter);
    }
}
