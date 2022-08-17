package com.alibaba.qlexpress4.runtime.operator.logic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.operator.base.BaseBinaryOperator;

/**
 * TODO bingo null 如何处理？
 * TODO 参考groovy
 * 普通类：null-false, 非null-true
 *
 * @author 冰够
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

    @Override
    public Object execute(Value left, Value right, ErrorReporter errorReporter) {
        Object leftValue = left.get();
        Object rightValue = right.get();
        // TODO 调用灵葙的工具类，object > boolean，隐式转换 @灵葙
        // 抽取至类型转换工具类
        if (leftValue == null) {
            leftValue = false;
        }
        if (rightValue == null) {
            rightValue = false;
        }

        if (!(leftValue instanceof Boolean) || !(rightValue instanceof Boolean)) {
            throw buildInvalidOperandTypeException(left, right, errorReporter);
        }

        return (Boolean)leftValue || (Boolean)rightValue;
    }

    @Override
    public String getOperator() {
        return "||";
    }

    @Override
    public int getPriority() {
        return QLPrecedences.OR;
    }

    public static LogicOrOperator getInstance(String operator) {
        return INSTANCE_CACHE.get(operator);
    }
}
