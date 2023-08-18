package com.alibaba.qlexpress4.runtime.operator.arithmetic;

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
public class ModOperator extends BaseBinaryOperator {
    private static final Map<String, ModOperator> INSTANCE_CACHE = new ConcurrentHashMap<>(2);

    static {
        INSTANCE_CACHE.put("%", new ModOperator("%"));
        INSTANCE_CACHE.put("mod", new ModOperator("mod"));
    }

    private final String operator;

    private ModOperator(String operator) {
        this.operator = operator;
    }

    public static ModOperator getInstance(String operator) {
        return INSTANCE_CACHE.get(operator);
    }

    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return mod(left, right, qlOptions, errorReporter);
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public int getPriority() {
        return QLPrecedences.MULTI;
    }
}