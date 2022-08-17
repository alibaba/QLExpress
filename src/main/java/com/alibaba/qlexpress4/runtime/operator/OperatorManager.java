package com.alibaba.qlexpress4.runtime.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.qlexpress4.runtime.operator.arithmetic.DivideOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MinusOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.ModOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseAndOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseLeftShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseOrOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftUnsignedOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseXorOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.EqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.UnequalOperator;
import com.alibaba.qlexpress4.runtime.operator.in.InOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicAndOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicOrOperator;

/**
 * 不考虑一元操作符
 *
 * @author 冰够
 */
public class OperatorManager {
    private static final Map<String, BinaryOperator> DEFAULT_BINARY_OPERATOR_MAP = new ConcurrentHashMap<>();
    private final Map<String, BinaryOperator> customBinaryOperatorMap = new ConcurrentHashMap<>();

    static {
        // TODO: 缺少赋值操作符
        List<BinaryOperator> binaryOperatorList = new ArrayList<>(32);
        binaryOperatorList.add(AssignOperator.getInstance());
        binaryOperatorList.add(PlusOperator.getInstance());
        binaryOperatorList.add(MinusOperator.getInstance());
        binaryOperatorList.add(MultiplyOperator.getInstance());
        binaryOperatorList.add(DivideOperator.getInstance());
        binaryOperatorList.add(ModOperator.getInstance("%"));
        binaryOperatorList.add(ModOperator.getInstance("mod"));
        binaryOperatorList.add(BitwiseAndOperator.getInstance());
        binaryOperatorList.add(BitwiseOrOperator.getInstance());
        binaryOperatorList.add(BitwiseXorOperator.getInstance());
        binaryOperatorList.add(BitwiseLeftShiftOperator.getInstance());
        binaryOperatorList.add(BitwiseRightShiftOperator.getInstance());
        binaryOperatorList.add(BitwiseRightShiftUnsignedOperator.getInstance());
        binaryOperatorList.add(LogicAndOperator.getInstance("&&"));
        binaryOperatorList.add(LogicAndOperator.getInstance("and"));
        binaryOperatorList.add(LogicOrOperator.getInstance("||"));
        binaryOperatorList.add(LogicOrOperator.getInstance("or"));
        binaryOperatorList.add(EqualOperator.getInstance());
        binaryOperatorList.add(UnequalOperator.getInstance());
        binaryOperatorList.add(GreaterOperator.getInstance());
        binaryOperatorList.add(GreaterEqualOperator.getInstance());
        binaryOperatorList.add(LessOperator.getInstance());
        binaryOperatorList.add(LessEqualOperator.getInstance());
        binaryOperatorList.add(InOperator.getInstance());
        for (BinaryOperator binaryOperator : binaryOperatorList) {
            DEFAULT_BINARY_OPERATOR_MAP.put(binaryOperator.getOperator(), binaryOperator);
        }
    }

    public void addOperator(String operator, BinaryOperator binaryOperator) {
        customBinaryOperatorMap.put(operator, binaryOperator);
    }

    public Map<String, Integer> getOperatorPrecedenceMap() {
        Map<String, Integer> operatorPrecedenceMap = new HashMap<>(32);
        customBinaryOperatorMap.forEach(
            (operator, binaryOperator) -> operatorPrecedenceMap.putIfAbsent(operator, binaryOperator.getPriority()));
        DEFAULT_BINARY_OPERATOR_MAP.forEach(
            (operator, binaryOperator) -> operatorPrecedenceMap.putIfAbsent(operator, binaryOperator.getPriority()));
        return operatorPrecedenceMap;
    }

    public BinaryOperator getBinaryOperator(String operatorLexeme) {
        BinaryOperator customBinaryOperator = customBinaryOperatorMap.get(operatorLexeme);
        if (customBinaryOperator != null) {
            return customBinaryOperator;
        }

        return DEFAULT_BINARY_OPERATOR_MAP.get(operatorLexeme);
    }
}
