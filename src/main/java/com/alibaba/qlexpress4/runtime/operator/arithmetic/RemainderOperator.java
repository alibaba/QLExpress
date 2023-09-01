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
public class RemainderOperator extends BaseBinaryOperator {
    private static final Map<String, RemainderOperator> INSTANCE_CACHE = new ConcurrentHashMap<>(2);

    static {
        INSTANCE_CACHE.put("%", new RemainderOperator("%"));
        //INSTANCE_CACHE.put("mod", new RemainderOperator("mod"));
    }

    private final String operator;

    private RemainderOperator(String operator) {
        this.operator = operator;
    }

    public static RemainderOperator getInstance(String operator) {
        return INSTANCE_CACHE.get(operator);
    }

    @Override
    public Object execute(Value left, Value right, QRuntime qRuntime, QLOptions qlOptions,
        ErrorReporter errorReporter) {
        return remainder(left, right, qlOptions, errorReporter);
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